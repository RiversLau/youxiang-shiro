package com.youxiang.shiro.config;

import com.youxiang.shiro.codec.Base64;
import com.youxiang.shiro.codec.Hex;
import com.youxiang.shiro.config.event.*;
import com.youxiang.shiro.event.EventBus;
import com.youxiang.shiro.event.EventBusAware;
import com.youxiang.shiro.event.Subscribe;
import com.youxiang.shiro.event.support.DefaultEventBus;
import com.youxiang.shiro.util.*;
import org.apache.commons.beanutils.BeanUtilsBean;
import org.apache.commons.beanutils.SuppressPropertiesBeanIntrospector;
import org.junit.experimental.theories.internal.Assignments;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.net.ProtocolException;
import java.util.*;

/**
 * Author: RiversLau
 * Date: 2018/1/5 15:07
 */
public class ReflectionBuilder {

    private static final transient Logger log = LoggerFactory.getLogger(ReflectionBuilder.class);

    private static final String OBJECT_REFERENCE_BEGIN_TOKEN = "$";
    private static final String ESCAPED_OBJECT_REFERENCE_BEGIN_TOKEN = "\\$";
    private static final String GLOBAL_PROPERTY_PREFIX = "shiro";
    private static final char MAP_KEY_VALUE_DELIMITER = ':';
    private static final String HEX_BEGIN_TOKEN = "0x";
    private static final String NULL_VALUE_TOKEN = "null";
    private static final String EMPTY_STRING_VALUE_TOKEN = "\"\"";
    private static final char STRING_VALUE_DELIMITER = '"';
    private static final char MAP_PROPERTY_BEGIN_TOKEN = '[';
    private static final char MAP_PROPERTY_END_TOKEN = ']';

    private static final String EVENT_BUS_NAME = "eventBus";

    private final Map<String, Object> objects;

    private Interpolator interpolator;

    private EventBus eventBus;

    private final Map<String, Object> registeredEventSubscribers;

    private final BeanUtilsBean beanUtilsBean;

    private Map<String, Object> createDefaultObjectMap() {
        Map<String, Object> map = new LinkedHashMap<String, Object>();
        map.put(EVENT_BUS_NAME, new DefaultEventBus());
        return map;
    }

    public ReflectionBuilder() {
        this(null);
    }

    public ReflectionBuilder(Map<String, ?> defaults) {
        beanUtilsBean = new BeanUtilsBean();
        beanUtilsBean.getPropertyUtils().addBeanIntrospector(SuppressPropertiesBeanIntrospector.SUPPRESS_CLASS);

        this.interpolator = createInterpolator();

        this.objects = createDefaultObjectMap();
        this.registeredEventSubscribers = new LinkedHashMap<String, Object>();
        apply(defaults);
    }

    private void apply(Map<String, ?> objects) {
        if (!isEmpty(objects)) {
            this.objects.putAll(objects);
        }
        EventBus found = findEventBus(this.objects);
        Assert.notNull(found, "An " + EventBus.class.getName() + " instance must be present in the object defaults");
        enableEvents(found);
    }

    public Map<String, ?> getObjects() {
        return objects;
    }

    public void setObjects(Map<String, ?> objects) {
        this.objects.clear();
        this.objects.putAll(createDefaultObjectMap());
        apply(objects);
    }

    private void enableEvents(EventBus eventBus) {
        Assert.notNull(eventBus, "Event argument cannot be null.");
        for (Object subscriber : this.registeredEventSubscribers.values()) {
            this.eventBus.unregister(subscriber);
        }
        this.registeredEventSubscribers.clear();

        this.eventBus = eventBus;

        for (Map.Entry<String, Object> entry : this.objects.entrySet()) {
            enableEventsIfNecessary(entry.getValue(), entry.getKey());
        }
    }

    private void enableEventsIfNecessary(Object bean, String name) {
        boolean applied = applyEventBusIfNecessary(bean);
        if (!applied) {
            if (isEventSubscriber(bean, name)) {
                this.eventBus.register(bean);
                this.registeredEventSubscribers.put(name, bean);
            }
        }
    }

    private boolean isEventSubscriber(Object bean, String name) {
        List annotatedMethods = ClassUtils.getAnnotatedMethods(bean.getClass(), Subscribe.class);
        return !isEmpty(annotatedMethods);
    }

    protected EventBus findEventBus(Map<String, ?> objects) {
        if (isEmpty(objects)) {
            return null;
        }
        Object value = objects.get(EVENT_BUS_NAME);
        if (value != null && value instanceof EventBus) {
            return (EventBus) value;
        }

        for (Object v : objects.values()) {
            if (v instanceof EventBus) {
                return (EventBus) v;
            }
        }
        return null;
    }

    private boolean applyEventBusIfNecessary(Object bean) {
        if (bean instanceof EventBusAware) {
            ((EventBusAware)bean).setEventBus(this.eventBus);
            return true;
        }
        return false;
    }

    public Object getBean(String id) {
        return objects.get(id);
    }

    public <T> T getBean(String id, Class<T> requiredType) {
        if (requiredType == null) {
            throw new NullPointerException("requiredType argument cannot be null.");
        }
        Object bean = getBean(id);
        if (bean == null) {
            return null;
        }
        Assert.state(requiredType.isAssignableFrom(bean.getClass()), "Bean with id [" + id + "] is not of the required type [" + requiredType.getName() + "].");
        return (T) bean;
    }

    private String parseBeanId(String lhs) {
        Assert.notNull(lhs);
        if (lhs.indexOf('.') < 0) {
            return lhs;
        }
        String classSuffix = ".class";
        int index = lhs.indexOf(classSuffix);
        if (index > 0) {
            return lhs.substring(0, index);
        }
        return null;
    }

    public Map<String, ?> buildObjects(Map<String, String> kvPairs) {

        if (kvPairs != null && !kvPairs.isEmpty()) {

            BeanConfigurationProcessor processor = new BeanConfigurationProcessor();

            for (Map.Entry<String, String> entry : kvPairs.entrySet()) {
                String lhs = entry.getKey();
                String rhs = interpolator.interpolate(entry.getValue());

                String beanId = parseBeanId(lhs);
                if (beanId != null) {
                    processor.add(new InstantiationStatement(beanId, rhs));
                } else {
                    processor.add(new AssignmentStatement(lhs, rhs));
                }
            }
            processor.execute();
        }
        LifecycleUtils.init(objects.values());
        return objects;
    }

    public void destroy() {
        final Map<String, Object> immutableObjects = Collections.unmodifiableMap(objects);

        List<Map.Entry<String, ?>> entries = new ArrayList<Map.Entry<String, ?>>(objects.entrySet());
        Collections.reverse(entries);

        for (Map.Entry<String, ?> entry : entries) {
            String id = entry.getKey();
            Object bean = entry.getValue();

            if (bean != this.eventBus) {
                LifecycleUtils.destroy(bean);
                BeanEvent event = new DestroyedBeanEvent(id, bean, immutableObjects);
                eventBus.publish(event);
                this.eventBus.unregister(bean);
            }
        }
        LifecycleUtils.destroy(this.eventBus);
    }

    protected void createNewInstance(Map<String, Object> objects, String name, String value) {
        Object currentInstance = objects.get(name);
        if (currentInstance != null) {
            log.info("An instance with name '{}' already exists. " + " Redefining this object as a new instance of type {}", name, value);
        }
        Object instance;
        try {
            instance = ClassUtils.newInstance(value);
            if (instance instanceof Nameable) {
                ((Nameable)instance).setName(name);
            }
        } catch (Exception e) {
            String msg = "Unable to instantiate class [" + value + "] for object named '" + name + "'." +
                    "Please ensure you've specified the fully qualified class name correctly.";
            throw new ConfigurationException(msg);
        }
        objects.put(name, value);
    }

    protected void applyProperty(String key, String value, Map objects) {
        int index = key.indexOf('.');

        if (index >= 0) {
            String name = key.substring(0, index);
            String property = key.substring(index + 1, key.length());

            if (GLOBAL_PROPERTY_PREFIX.equalsIgnoreCase(name)) {
                applyGlobalProperty(objects, property, value);
            } else {
                applySingleProperty(objects, name, property, value);
            }
        } else {
            throw new IllegalArgumentException("All property keys must contain a '.' character. " +
                        "(e.g. myBean.property = value) These should already be seperated out by buildObject().");
        }
    }

    protected void applyGlobalProperty(Map objects, String property, String value) {
        for (Object instance : objects.values()) {
            try {
                PropertyDescriptor pd = beanUtilsBean.getPropertyUtils().getPropertyDescriptor(instance, property);
                if (pd != null) {
                    applyProperty(instance, property, value);
                }
            } catch (Exception e) {
                String msg = "Error retrieving property descriptor for instance of type [" + instance.getClass().getName() +
                        "] while setting property [" + property + "]";
                throw new ConfigurationException(msg);
            }

        }
    }

    protected void applySingleProperty(Map objects, String name, String property, String value) {
        Object obj = objects.get(name);
        if (property.equals("class")) {
            throw new IllegalArgumentException("Property keys should not contain 'class' properties since these " +
                    "should already be seperated out by buildObjects().");
        } else if (obj == null) {
            String msg = "Configuration error.  Specified object [" + name + "] with property [" +
                    property + "] without first defining that object's class.  Please first " +
                    "specify the class property first, e.g. myObject = fully_qualified_class_name " +
                    "and then define additional properties.";
            throw new IllegalArgumentException(msg);
        } else {
            applyProperty(obj, property, value);
        }
    }

    protected boolean isReference(String value) {
        return value != null && value.startsWith(OBJECT_REFERENCE_BEGIN_TOKEN);
    }

    protected String getId(String referenceToken) {
        return referenceToken.substring(OBJECT_REFERENCE_BEGIN_TOKEN.length());
    }

    public Object getReferencedObject(String id) {
        Object o = objects != null && !objects.isEmpty() ? objects.get(id) : null;
        if (o == null) {
            String msg = "The object with id [" + id + "] has not yet been defined and therefore cannot be " +
                    "referenced.  Please ensure objects are defined in the order in which they should be " +
                    "created and made available for future reference.";
            throw new UnresolveableReferenceException(msg);
        }
        return o;
    }

    protected String unescapeIfNecessary(String value) {
        if (value != null && value.startsWith(ESCAPED_OBJECT_REFERENCE_BEGIN_TOKEN)) {
            return value.substring(ESCAPED_OBJECT_REFERENCE_BEGIN_TOKEN.length() - 1);
        }
        return value;
    }

    protected Object resolveReference(String reference) {
        String id = getId(reference);
        log.debug("Encountered object reference '{}'. Looking up object with id '{}'", reference, id);
        final Object referencedObject = getReferencedObject(id);
        if (referencedObject instanceof Factory) {
            return ((Factory) referencedObject).getInstance();
        }
        return referencedObject;
    }

    protected boolean isTypedProperty(Object object, String propertyName, Class clazz) {
        if (clazz == null) {
            throw new NullPointerException("type (class) argument cannot be null.");
        }

        try {
            PropertyDescriptor descriptor = beanUtilsBean.getPropertyUtils().getPropertyDescriptor(object, propertyName);
            if (descriptor == null) {
                String msg = "Property '" + propertyName + "' does not exist for object of type " + object.getClass().getName() + ".";
                throw new ConfigurationException(msg);
            }
            Class propertyClazz = descriptor.getPropertyType();
            return clazz.isAssignableFrom(propertyClazz);
        } catch (ConfigurationException ce) {
            throw ce;
        } catch (Exception e) {
            String msg = "Unable to detemine if property [" + propertyName + "] represents a " + clazz.getName();
            throw new ConfigurationException(msg);
        }
    }

    protected Set<?> toSet(String sValue) {
        String[] tokens = StringUtils.split(sValue);
        if (tokens == null || tokens.length <= 0) {
            return null;
        }

        if (tokens.length == 1 && isReference(tokens[0])) {
            Object reference = resolveReference(tokens[0]);
            if (reference instanceof Set) {
                return (Set) reference;
            }
        }

        Set<String> setTokens = new LinkedHashSet<String>(Arrays.asList(tokens));
        Set<Object> values = new LinkedHashSet<Object>(setTokens.size());
        for (String token : setTokens) {
            Object value = resolveValue(token);
            values.add(value);
        }
        return values;
    }

    protected Map<?, ?> toMap(String sValue) {
        String[] tokens = StringUtils.split(sValue, StringUtils.DEFAULT_DELIMITER_CHAR, StringUtils.DEFAULT_QUOTE_CHAR,
                StringUtils.DEFAULT_QUOTE_CHAR, true, true);
        if (tokens == null || tokens.length <= 0) {
            return null;
        }

        if (tokens.length == 1 && isReference(tokens[0])) {
            Object reference = resolveReference(tokens[0]);
            if (reference instanceof Map) {
                return (Map<?, ?>) reference;
            }
        }

        Map<String, String> mapTokens = new LinkedHashMap<String, String>(tokens.length);
        for (String token : tokens) {
            String[] kvPair = StringUtils.split(token, MAP_KEY_VALUE_DELIMITER);
            if (kvPair == null || kvPair.length != 2) {
                String msg = "Map property value [" + sValue + "] contained key-value pair token [" +
                        token + "] that does not properly split to a single key and pair.  This must be the " +
                        "case for all map entries.";
                throw new ConfigurationException(msg);
            }
            mapTokens.put(kvPair[0], kvPair[1]);
        }

        Map<Object, Object> map = new LinkedHashMap<Object, Object>(mapTokens.size());
        for (Map.Entry<String, String> entry : mapTokens.entrySet()) {
            Object key = resolveValue(entry.getKey());
            Object value = resolveValue(entry.getValue());
            map.put(key, value);
        }
        return map;
    }

    protected Collection<?> toCollection(String sValue) {
        String[] tokens = StringUtils.split(sValue);
        if (tokens == null || tokens.length <= 0) {
            return null;
        }

        if (tokens.length == 1 && isReference(tokens[0])) {
            Object reference = resolveReference(tokens[0]);
            if (reference instanceof Collection) {
                return (Collection) reference;
            }
        }

        List<Object> values = new ArrayList<Object>(tokens.length);
        for (String token : tokens) {
            Object value = resolveValue(token);
            values.add(value);
        }
        return values;
    }

    protected List<?> toList(String sValue) {
        String[] tokens = StringUtils.split(sValue);
        if (tokens == null || tokens.length <= 0) {
            return null;
        }

        if (tokens.length == 1 && isReference(tokens[0])) {
            Object reference = resolveReference(tokens[0]);
            if (reference instanceof List) {
                return (List<?>) reference;
            }
        }

        List<Object> values = new ArrayList<Object>(tokens.length);
        for (String token : tokens) {
            Object value = resolveValue(token);
            values.add(value);
        }
        return values;
    }

    protected byte[] toBytes(String sValue) {
        if (sValue == null) {
            return null;
        }
        byte[] bytes;
        if (sValue.startsWith(HEX_BEGIN_TOKEN)) {
            String hex = sValue.substring(HEX_BEGIN_TOKEN.length());
            bytes = Hex.decode(hex);
        } else {
            bytes = Base64.decode(sValue);
        }
        return bytes;
    }

    protected Object resolveValue(String stringValue) {
        Object value;
        if (isReference(stringValue)) {
            value = resolveReference(stringValue);
        } else {
            value = unescapeIfNecessary(stringValue);
        }
        return value;
    }

    protected String checkForNullOrEmptyLiteral(String stringValue) {
        if (stringValue == null) {
            return null;
        }
        if (stringValue.equals("\"null\"")) {
            return NULL_VALUE_TOKEN;
        } else if (stringValue.equals("\"\"\"\"")) {
            return EMPTY_STRING_VALUE_TOKEN;
        } else {
            return stringValue;
        }
    }

    protected void applyProperty(Object object, String propertyPath, Object value) {

        int mapBegin = propertyPath.indexOf(MAP_PROPERTY_BEGIN_TOKEN);
        int mapEnd = -1;

        String mapPropertyPath = null;
        String keyString = null;

        String remaining = null;
        if (mapBegin >= 0) {
            mapPropertyPath = propertyPath.substring(0, mapBegin);
            mapEnd = propertyPath.indexOf(MAP_PROPERTY_END_TOKEN, mapBegin);
            keyString = propertyPath.substring(mapBegin + 1, mapEnd);
            if (propertyPath.length() > (mapEnd + 1)) {
                remaining = propertyPath.substring(mapEnd + 1);
                if (remaining.startsWith(".")) {
                    remaining = StringUtils.clean(remaining.substring(1));
                }
            }
        }

        if (remaining == null) {
            if (keyString == null) {
                setProperty(object, propertyPath, value);
            } else {
                if (isTypedProperty(object, mapPropertyPath, Map.class)) {
                    Map map = (Map)getProperty(object, mapPropertyPath);
                    Object mapKey = resolveValue(keyString);
                    map.put(mapKey, value);
                } else {
                    int index = Integer.valueOf(keyString);
                    setIndexedProperty(object, mapPropertyPath, index, value);
                }
            }
        } else {
            Object referencedValue = null;
            if (isTypedProperty(object, mapPropertyPath, Map.class)) {
                Map map = (Map) getProperty(object, mapPropertyPath);
                Object mapKey = resolveValue(keyString);
                referencedValue = map.get(mapKey);
            } else {
                int index = Integer.valueOf(keyString);
                referencedValue = getIndexedProperty(object, mapPropertyPath, index);
            }

            if (referencedValue == null) {
                throw new ConfigurationException("Referenced map/array value " + mapPropertyPath + "[" +
                        keyString + "] does not exist.");
            }

            applyProperty(referencedValue, remaining, value);
        }
    }

    private void setProperty(Object object, String propertyPath, Object value) {
        try {
            if (log.isTraceEnabled()) {
                log.trace("Applying property [{}] value [{}] on object of type [{}]",
                        new Object[]{propertyPath, value, object.getClass().getName()});
            }
            beanUtilsBean.setProperty(object, propertyPath, value);
        } catch (Exception e) {
            String msg = "Unable to set property '" + propertyPath + "' with value [" + value + "] on object " +
                    "of type " + (object != null ? object.getClass().getName() : null) + ".  If " +
                    "'" + value + "' is a reference to another (previously defined) object, prefix it with " +
                    "'" + OBJECT_REFERENCE_BEGIN_TOKEN + "' to indicate that the referenced " +
                    "object should be used as the actual value.  " +
                    "For example, " + OBJECT_REFERENCE_BEGIN_TOKEN + value;
            throw new ConfigurationException(msg, e);
        }
    }

    private Object getProperty(Object object, String propertyPath) {
        try {
            return beanUtilsBean.getPropertyUtils().getProperty(object, propertyPath);
        } catch (Exception e) {
            throw new ConfigurationException("Unable to access property '" + propertyPath + "'", e);
        }
    }

    private void setIndexedProperty(Object object, String propertyPath, int index, Object value) {
        try {
            beanUtilsBean.getPropertyUtils().setIndexedProperty(object, propertyPath, index, value);
        } catch (Exception e) {
            throw new ConfigurationException("Unable to set array property '" + propertyPath + "'.", e);
        }
    }

    private Object getIndexedProperty(Object object, String propertyPath, int index) {
        try {
            return beanUtilsBean.getPropertyUtils().getIndexedProperty(object, propertyPath, index);
        } catch (Exception e) {
            throw new ConfigurationException("Unable to acquire array property '" + propertyPath + "'.", e);
        }
    }

    protected boolean isIndexedPropertyAssignment(String propertyPath) {
        return propertyPath.endsWith("" + MAP_PROPERTY_END_TOKEN);
    }

    protected void applyProperty(Object object, String propertyName, String stringValue) {

        Object value;
        if (NULL_VALUE_TOKEN.equals(stringValue)) {
            value = null;
        } else if (EMPTY_STRING_VALUE_TOKEN.equals(stringValue)) {
            value = StringUtils.EMPTY_STRING;
        } else if (isIndexedPropertyAssignment(propertyName)) {
            String checked = checkForNullOrEmptyLiteral(stringValue);
            value = resolveValue(checked);
        } else if (isTypedProperty(object, propertyName, Set.class)) {
            value = toSet(stringValue);
        } else if (isTypedProperty(object, propertyName, Map.class)) {
            value = toMap(stringValue);
        } else if (isTypedProperty(object, propertyName, List.class)) {
            value = toList(stringValue);
        } else if (isTypedProperty(object, propertyName, Collection.class)) {
            value = toCollection(stringValue);
        } else if (isTypedProperty(object, propertyName, byte[].class)) {
            value = toBytes(stringValue);
        } else if (isTypedProperty(object, propertyName, ByteSource.class)) {
            byte[] bytes = toBytes(stringValue);
            value = ByteSource.Util.bytes(bytes);
        } else {
            String checked = checkForNullOrEmptyLiteral(stringValue);
            value = resolveValue(checked);
        }

        applyProperty(object, propertyName, value);
    }

    private Interpolator createInterpolator() {
        if (ClassUtils.isAvailable("org.apache.commons.configuration2.interpol.ConfigurationInterpolator")) {
            return new CommonsInterpolator();
        }
        return new DefaultInterpolator();
    }

    private static boolean isEmpty(Map m) {
        return m == null || m.isEmpty();
    }

    private static boolean isEmpty(Collection c) {
        return c == null || c.isEmpty();
    }

    private class BeanConfigurationProcessor {

        private final List<Statement> statements = new ArrayList<Statement>();
        private final List<BeanConfiguration> beanConfigurations = new ArrayList<BeanConfiguration>();

        public void add(Statement statement) {
            statements.add(statement);
            if (statement instanceof InstantiationStatement) {
                InstantiationStatement is = (InstantiationStatement) statement;
                beanConfigurations.add(new BeanConfiguration(is));
            } else {
                AssignmentStatement as = (AssignmentStatement) statement;
                boolean addedToConfig = false;
                String beanName = as.getRootBeanName();
                for (int i = beanConfigurations.size() - 1; i >= 0; i--) {
                    BeanConfiguration mostRecent = beanConfigurations.get(i);
                    String mostRecentBeanName = mostRecent.getBeanName();
                    if (beanName.equals(mostRecentBeanName)) {
                        mostRecent.add(as);
                        addedToConfig = true;
                        break;
                    }
                }
                if (!addedToConfig) {
                    beanConfigurations.add(new BeanConfiguration(as));
                }
            }
        }

        public void execute() {
            for (Statement statement : statements) {
                statement.execute();

                BeanConfiguration bd = statement.getBeanConfiguration();
                if (bd.isExecuted()) {
                    if (bd.getBeanName().equals(EVENT_BUS_NAME)) {
                        EventBus eventBus = (EventBus) bd.getBean();
                        enableEvents(eventBus);
                    }
                    if (!bd.isGlobalConfig()) {
                        BeanEvent event = new ConfiguredBeanEvent(bd.getBeanName(), bd.getBean(), Collections.unmodifiableMap(objects));
                        eventBus.publish(event);
                    }
                    LifecycleUtils.init(bd.getBean());
                    if (!bd.isGlobalConfig()) {
                        BeanEvent event = new InitializedBeanEvent(bd.getBeanName(), bd.getBean(),
                                Collections.unmodifiableMap(objects));
                        eventBus.publish(event);
                    }
                }
            }
        }
    }

    private class BeanConfiguration {
        private final InstantiationStatement instantiationStatement;
        private final List<AssignmentStatement> assignments = new ArrayList<AssignmentStatement>();
        private final String beanName;
        private Object bean;

        private BeanConfiguration(InstantiationStatement statement) {
            statement.setBeanConfiguration(this);
            this.instantiationStatement = statement;
            this.beanName = statement.lhs;
        }

        private BeanConfiguration(AssignmentStatement statement) {
            this.instantiationStatement = null;
            this.beanName = statement.getRootBeanName();
            add(statement);
        }

        public String getBeanName() {
            return this.beanName;
        }

        public boolean isGlobalConfig() {
            return GLOBAL_PROPERTY_PREFIX.equals(getBeanName());
        }

        public void add(AssignmentStatement as) {
            as.setBeanConfiguration(this);
            assignments.add(as);
        }

        public void setBean(Object bean) {
            this.bean = bean;
        }

        public Object getBean() {
            return this.bean;
        }

        public boolean isExecuted() {
            if (instantiationStatement != null && !instantiationStatement.isExecuted()) {
                return false;
            }
            for (AssignmentStatement as : assignments) {
                if (!as.isExecuted()) {
                    return false;
                }
            }
            return true;
        }
    }

    private abstract class Statement {
        protected final String lhs;
        protected final String rhs;
        protected Object bean;
        private Object result;
        private boolean executed;
        private BeanConfiguration beanConfiguration;

        private Statement(String lhs, String rhs) {
            this.lhs = lhs;
            this.rhs = rhs;
            this.executed = false;
        }

        public void setBeanConfiguration(BeanConfiguration beanConfiguration) {
            this.beanConfiguration = beanConfiguration;
        }

        public BeanConfiguration getBeanConfiguration() {
            return beanConfiguration;
        }

        public Object execute() {
            if (!isExecuted()) {
                this.result = doExecute();
                this.executed = true;
            }
            if (!getBeanConfiguration().isGlobalConfig()) {
                Assert.notNull(this.bean, "Implemention must set the root bean for which it executed.");
            }
            return this.result;
        }

        public Object getBean() {
            return bean;
        }

        protected void setBean(Object bean) {
            this.bean = bean;
            if (this.beanConfiguration.getBean() == null) {
                this.beanConfiguration.setBean(bean);
            }
        }

        public Object getResult() {
            return this.result;
        }

        protected abstract Object doExecute();

        public boolean isExecuted() {
            return executed;
        }
    }

    private class InstantiationStatement extends Statement {
        private InstantiationStatement(String lhs, String rhs) {
            super(lhs, rhs);
        }

        @Override
        protected Object doExecute() {
            String beanName = this.lhs;
            createNewInstance(objects, beanName, this.rhs);
            Object instantiated = objects.get(beanName);
            setBean(instantiated);

            enableEventsIfNecessary(instantiated, beanName);

            BeanEvent beanEvent = new InstantiatedBeanEvent(beanName, instantiated, Collections.unmodifiableMap(objects));
            eventBus.publish(beanEvent);
            return instantiated;
        }
    }

    private class AssignmentStatement extends Statement {
        private final String rootBeanName;
        private AssignmentStatement(String lhs, String rhs) {
            super(lhs, rhs);
            int index = lhs.indexOf('.');
            this.rootBeanName = lhs.substring(0, index);
        }

        @Override
        protected Object doExecute() {
            applyProperty(lhs, rhs, objects);
            Object bean = objects.get(this.rootBeanName);
            setBean(bean);
            return null;
        }

        public String getRootBeanName() {
            return rootBeanName;
        }
    }
}
