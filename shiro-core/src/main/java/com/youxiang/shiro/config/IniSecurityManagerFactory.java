package com.youxiang.shiro.config;

import com.youxiang.shiro.mgt.RealmSecurityManager;
import com.youxiang.shiro.mgt.SecurityManager;
import com.youxiang.shiro.realm.Realm;
import com.youxiang.shiro.realm.RealmFactory;
import com.youxiang.shiro.realm.text.IniRealm;
import com.youxiang.shiro.util.CollectionUtils;
import com.youxiang.shiro.util.LifecycleUtils;
import com.youxiang.shiro.util.Nameable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Author: RiversLau
 * Date: 2018/1/3 16:06
 */
public class IniSecurityManagerFactory extends IniFactorySupport<SecurityManager> {

    private static final String MAIN_SECTION_NAME = "main";

    private static final String SECURITY_MANAGER_NAME = "securityManager";
    private static final String INI_REALM_NAME = "iniRealm";

    private static transient final Logger log = LoggerFactory.getLogger(IniSecurityManagerFactory.class);

    private ReflectionBuilder builder;
    public IniSecurityManagerFactory() {
        this.builder = new ReflectionBuilder();
    }

    public IniSecurityManagerFactory(Ini config) {
        super();
        setIni(config);
    }

    public IniSecurityManagerFactory(String iniResourcePath) {
        this(Ini.fromResourcePath(iniResourcePath));
    }

    public Map<String, ?> getBeans() {
        return this.builder != null ? Collections.unmodifiableMap(builder.getObjects()) : null;
    }

    public void destroy() {
        if (getReflectionBuilder() != null) {
            getReflectionBuilder().destroy();
        }
    }

    private SecurityManager getSecurityManagerBean() {
        return getReflectionBuilder().getBean(SECURITY_MANAGER_NAME, SecurityManager.class);
    }

    protected SecurityManager createDefaultInstance() {
        return new DefaultSecurityManager();
    }

    protected SecurityManager createInstance(Ini ini) {
        if (CollectionUtils.isEmpty(ini)) {
            throw new NullPointerException("Ini argument cannot be null.");
        }
        SecurityManager securityManager = createSecurityManager(ini);
        if (securityManager == null) {
            String msg = SecurityManager.class + " instance cannot be null.";
            throw new ConfigurationException(msg);
        }
        return securityManager;
    }

    private SecurityManager createSecurityManager(Ini ini) {
        return createSecurityManager(ini, getConfigSection(ini));
    }

    private Ini.Section getConfigSection(Ini ini) {
        Ini.Section mainSection = ini.getSection(MAIN_SECTION_NAME);
        if (CollectionUtils.isEmpty(mainSection)) {
            mainSection = ini.getSection(Ini.DEFAULT_SECTION_NAME);
        }
        return mainSection;
    }

    protected boolean isAutoApplyRealms(SecurityManager securityManager) {
        boolean autoApply = true;
        if (securityManager instanceof RealmSecurityManager) {
            RealmSecurityManager realmSecurityManager = (RealmSecurityManager) securityManager;
            Collection<Realm> realms = realmSecurityManager.getRealms();
            if (!CollectionUtils.isEmpty(realms)) {
                log.info("Realms have been explicitly set on the SecurityManager instance - auto-setting of " +
                        "realms will not occur.");
                autoApply = false;
            }
        }
        return autoApply;
    }

    private SecurityManager createSecurityManager(Ini ini, Ini.Section mainSection) {
        getReflectionBuilder().setObjects(createDefaults(ini, mainSection));
        Map<String, ?> objects = buildInstances(mainSection);

        SecurityManager securityManager = getSecurityManagerBean();
        boolean autoApplyRealms = isAutoApplyRealms(securityManager);

        if (autoApplyRealms) {
            Collection<Realm> realms = getRealms(objects);
            if (!CollectionUtils.isEmpty(realms)) {
                applyRealmsToSecurityManager(realms, securityManager);
            }
        }

        return securityManager;
    }

    protected Map<String, ?> createDefaults(Ini ini, Ini.Section mainSection) {

        Map<String, Object> defaults = new LinkedHashMap<String, Object>();

        SecurityManager securityManager = createDefaultInstance();
        defaults.put(SECURITY_MANAGER_NAME, securityManager);

        if (shouldImplicitlyCreateRealm(ini)) {
            Realm realm = createRealm(ini);
            if (realm != null) {
                defaults.put(INI_REALM_NAME, realm);
            }
        }

        Map<String, ?> defaultBeans = getDefaults();
        if (!CollectionUtils.isEmpty(defaultBeans)) {
            defaults.putAll(defaultBeans);
        }
        return defaults;
    }

    private Map<String, ?> buildInstances(Ini.Section section) {
        return getReflectionBuilder().buildObjects(section);
    }

    private void addToRealms(Collection<Realm> realms, RealmFactory realmFactory) {
        LifecycleUtils.init(realmFactory);
        Collection<Realm> factoryRealms = realmFactory.getRealms();
        if (!CollectionUtils.isEmpty(factoryRealms)) {
            realms.addAll(factoryRealms);
        }
    }

    private Collection<Realm> getRealms(Map<String, ?> instance) {
        List<Realm> realms = new ArrayList<Realm>();
        for (Map.Entry<String, ?> entry : instance.entrySet()) {
            String name = entry.getKey();
            Object value = entry.getValue();

            if (value instanceof RealmFactory) {
                addToRealms(realms, (RealmFactory)value);
            } else if (value instanceof Realm) {
                Realm realm = (Realm) value;
                String existingName = realm.getName();
                if (existingName == null || existingName.startsWith(realm.getClass().getName())) {
                    if (realm instanceof Nameable) {
                        ((Nameable) realm).setName(name);
                        log.debug("Applied name '{}' to Nameable realm instance {}", name, realm);
                    } else {
                        log.info("Realm does not implement the {} interface. Configured name will not be applied.");
                    }
                }
                realms.add(realm);
            }
        }
        return realms;
    }

    private void assertRealmSecurityManager(SecurityManager securityManager) {
        if (securityManager == null) {
            throw new NullPointerException("securityManager instance cannot be null.");
        }
        if (!(securityManager instanceof RealmSecurityManager)) {
            String msg = "securityManager instance is not a " + RealmSecurityManager.class.getName() +
                    " instance.  This is required to access or configure realms on the instance.";
            throw new ConfigurationException(msg);
        }
    }

    protected void applyRealmsToSecurityManager(Collection<Realm> realms, SecurityManager securityManager) {
        assertRealmSecurityManager(securityManager);
        ((RealmSecurityManager) securityManager).setRealms(realms);
    }

    protected boolean shouldImplicitlyCreateRealm(Ini ini) {
        return !CollectionUtils.isEmpty(ini) &&
                (!CollectionUtils.isEmpty(ini.getSection(IniRealm.ROLES_SECTION_NAME)) ||
                !CollectionUtils.isEmpty(ini.getSection(IniRealm.USERS_SECTION_NAME)));
    }

    protected Realm createRealm(Ini ini) {
        IniRealm realm = new IniRealm();
        realm.setName(INI_REALM_NAME);
        realm.setIni(ini);
        return realm;
    }

    public ReflectionBuilder getReflectionBuilder() {
        return builder;
    }

    public void setReflectionBuilder(ReflectionBuilder builder) {
        this.builder = builder;
    }
}
