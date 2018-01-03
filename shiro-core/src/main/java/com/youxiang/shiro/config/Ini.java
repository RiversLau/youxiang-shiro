package com.youxiang.shiro.config;

import com.youxiang.shiro.util.CollectionUtils;
import com.youxiang.shiro.util.ResourceUtils;
import com.youxiang.shiro.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.*;

/**
 * Author: RiversLau
 * Date: 2018/1/2 16:50
 */
public class Ini implements Map<String, Ini.Section> {

    private static transient final Logger log = LoggerFactory.getLogger(Ini.class);

    public static final String DEFAULT_SECTION_NAME = "";
    public static final String DEFAULT_CHARSET_NAME = "UTF-8";

    public static final String COMMENT_POUND = "#";
    public static final String COMMENT_SEMICOLON = ";";
    public static final String SECTION_PREFIX = "[";
    public static final String SECTION_SUFFIX = "]";

    protected static final char ESCAPE_TOKEN = '\\';

    private final Map<String, Section> sections;

    public Ini() {
        this.sections = new LinkedHashMap<String, Section>();
    }

    public Ini(Ini defaults) {
        this();
        if (defaults == null) {
            throw new NullPointerException("Defaults can not be null");
        }
        for (Section section : defaults.getSections()) {
            Section copy = new Section(section);
            this.sections.put(section.getName(), copy);
        }
    }

    /**
     * 判断sections是否为空
     * 只要其中有一个section的数据不为空，则整体不为空
     *
     * @return
     */
    public boolean isEmpty() {
        Collection<Section> sections = this.sections.values();
        if (!sections.isEmpty()) {
            for (Section section : sections) {
                if (!section.isEmpty()) {
                    return false;
                }
            }
        }
        return false;
    }

    /**
     * 获取所有section的名称
     *
     * @return
     */
    public Set<String> getSessionNames() {
        return Collections.unmodifiableSet(sections.keySet());
    }

    public Collection<Section> getSections() {
        return Collections.unmodifiableCollection(sections.values());
    }

    /**
     * 通过sectionName获取对应的Section
     *
     * @param sectionName
     * @return
     */
    public Section getSection(String sectionName) {
        String name = cleanName(sectionName);
        return sections.get(name);
    }

    /**
     * 添加section，如果对应名称的section不存在，则创建新的section并放入sections中；如果已经存在，则直接
     * 返回该section
     *
     * @param sectionName
     * @return
     */
    public Section addSection(String sectionName) {
        String name = cleanName(sectionName);
        Section section = getSection(name);
        if (section == null) {
            section = new Section(name);
            this.sections.put(name, section);
        }
        return section;
    }

    /**
     * 移除section
     *
     * @param sectionName
     * @return
     */
    public Section removeSection(String sectionName) {
        String name = cleanName(sectionName);
        return this.sections.remove(name);
    }

    /**
     * 去掉sectionName的两头空格，如果sectionName为空，那么直接返回默认的sectionName，也就是空字符串
     *
     * @param sectionName
     * @return
     */
    private static String cleanName(String sectionName) {
        String name = StringUtils.clean(sectionName);
        if (name == null) {
            log.trace("Specified name was null or empty. Defaulting to the default section (name = \"\")");
            name = DEFAULT_SECTION_NAME;
        }
        return name;
    }

    /**
     * 根据sectionName设置对应section下的键值对，如果sectionName对应的section不存在，则先添加section，
     * 然后再设置键值对
     *
     * @param sectionName
     * @param propertyName
     * @param propertyValue
     */
    public void setSectionProperty(String sectionName, String propertyName, String propertyValue) {
        String name = cleanName(sectionName);
        Section section = getSection(name);
        if (section == null) {
            section = addSection(name);
        }
        section.put(propertyName, propertyValue);
    }

    /**
     * 返回指定的section属性的值
     *
     * @param sectionName  section名
     * @param propertyName 属性名
     * @return
     */
    public String getSectionProperty(String sectionName, String propertyName) {
        Section section = getSection(sectionName);
        return section != null ? section.get(propertyName) : null;
    }

    /**
     * 返回指定section属性的值，如果值为null，则返回默认值
     *
     * @param sectionName  section名
     * @param propertyName 属性名
     * @param defaultValue 默认值
     * @return
     */
    public String getSectionProperty(String sectionName, String propertyName, String defaultValue) {
        String value = getSectionProperty(sectionName, propertyName);
        return value != null ? value : defaultValue;
    }

    /**
     * 加载指定路径下的资源文件来创建Ini实例
     *
     * @param resourcePath 资源路径
     * @return
     */
    public static Ini fromResourcePath(String resourcePath) {
        if (!StringUtils.hasLength(resourcePath)) {
            throw new IllegalArgumentException("Resource path argument cannot be null or empty.");
        }
        Ini ini = new Ini();
        ini.loadFromPath(resourcePath);
        return ini;
    }

    public void loadFromPath(String resourcePath) {
        InputStream is;
        try {
            is = ResourceUtils.getInputStreamForPath(resourcePath);
        } catch (IOException e) {
            throw new ConfigurationException(e);
        }
        load(is);
    }

    /**
     * 加载指定的ini配置文件
     *
     * @param iniConfig
     */
    public void load(String iniConfig) {
        load(new Scanner(iniConfig));
    }

    /**
     * 加载InputStream中的数据到Ini实例
     *
     * @param is
     */
    public void load(InputStream is) {
        if (is == null) {
            throw new NullPointerException("InputStream argument cannot be null.");
        }
        InputStreamReader isr;
        try {
            isr = new InputStreamReader(is, DEFAULT_CHARSET_NAME);
        } catch (UnsupportedEncodingException e) {
            throw new ConfigurationException(e);
        }
        load(isr);
    }

    /**
     * 加载Reader中的数据到Ini实例
     *
     * @param reader
     */
    public void load(Reader reader) {
        Scanner scanner = new Scanner(reader);
        try {
            load(scanner);
        } finally {
            try {
                scanner.close();
            } catch (Exception ex) {
                log.debug("Unable to cleanly close the InputStream scanner. Non-critical -ignoring.", ex);
            }
        }
    }

    /**
     * 加载Scanner中的数据到Ini实例
     *
     * @param scanner
     */
    public void load(Scanner scanner) {

        String sectionName = DEFAULT_SECTION_NAME;
        StringBuilder sectionContent = new StringBuilder();

        while (scanner.hasNextLine()) {

            String rawLine = scanner.nextLine();
            String line = StringUtils.clean(rawLine);

            if (line == null || line.startsWith(COMMENT_POUND) || line.startsWith(COMMENT_SEMICOLON)) {
                continue;
            }

            String newSectionName = getSectionName(line);
            if (newSectionName != null) {
                addSection(newSectionName, sectionContent);

                sectionContent = new StringBuilder();
                sectionName = newSectionName;

                log.debug("Parsing " + SECTION_PREFIX + sectionName + SECTION_SUFFIX);
            } else {
                sectionContent.append(line);
            }
        }

        addSection(sectionName, sectionContent);
    }

    protected static boolean isSectionHeader(String line) {
        String s = StringUtils.clean(line);
        return s != null && s.startsWith(SECTION_PREFIX) && s.endsWith(SECTION_SUFFIX);
    }

    protected static String getSectionName(String line) {
        String s = StringUtils.clean(line);
        if (isSectionHeader(s)) {
            // 去掉sectionName前缀后缀
            return cleanName(s.substring(1, s.length() - 1));
        }
        return null;
    }

    private void addSection(String name, StringBuilder content) {
        if (content.length() > 0) {
            String contentString = content.toString();
            String cleaned = StringUtils.clean(contentString);
            if (cleaned != null) {
                Section section = new Section(name, contentString);
                if (!section.isEmpty()) {
                    sections.put(name, section);
                }
            }
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Ini) {
            Ini other = (Ini) obj;
            return this.sections.equals(other.sections);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return this.sections.hashCode();
    }

    @Override
    public String toString() {
        if (CollectionUtils.isEmpty(this.sections)) {
            return "<empty INI>";
        } else {
            StringBuilder sb = new StringBuilder();
            int i = 0;
            for (Ini.Section section : this.sections.values()) {
                if (i > 0) {
                    sb.append(",");
                }
                sb.append(section.toString());
                i++;
            }
            return sb.toString();
        }
    }

    // ----------- 实现Map接口相关方法
    public int size() {
        return this.sections.size();
    }

    public boolean containsKey(Object key) {
        return this.sections.containsKey(key);
    }

    public boolean containsValue(Object value) {
        return this.sections.containsValue(value);
    }

    public Section get(Object key) {
        return this.sections.get(key);
    }

    public Section put(String key, Section value) {
        return this.sections.put(key, value);
    }

    public Section remove(Object key) {
        return this.sections.remove(key);
    }

    public void putAll(Map<? extends String, ? extends Section> m) {
        this.sections.putAll(m);
    }

    public void clear() {
        this.sections.clear();
    }

    public Set<String> keySet() {
        return Collections.unmodifiableSet(this.sections.keySet());
    }

    public Collection<Section> values() {
        return Collections.unmodifiableCollection(this.sections.values());
    }

    public Set<Entry<String, Section>> entrySet() {
        return Collections.unmodifiableSet(this.sections.entrySet());
    }

    /**
     * 静态内部类Section
     */
    public static class Section implements Map<String, String> {
        private final String name;
        private final Map<String, String> props;

        private Section(String name) {
            if (name == null) {
                throw new NullPointerException("name");
            }
            this.name = name;
            this.props = new LinkedHashMap<String, String>();
        }

        private Section(String name, String sectionContent) {
            if (name == null) {
                throw new NullPointerException("name");
            }
            this.name = name;
            Map<String, String> props;
            if (StringUtils.hasText(sectionContent)) {
                props = toMapProps(sectionContent);
            } else {
                props = new LinkedHashMap<String, String>();
            }
            if (props != null) {
                this.props = props;
            } else {
                this.props = new LinkedHashMap<String, String>();
            }
        }

        private Section(Section defaults) {
            this(defaults.getName());
            putAll(defaults.props);
        }

        protected static boolean isContinued(String line) {
            if (!StringUtils.hasText(line)) {
                return false;
            }
            int length = line.length();
            int backslashCount = 0;
            for (int i = length - 1; i > 0; i--) {
                if (line.charAt(i) == ESCAPE_TOKEN) {
                    backslashCount++;
                } else {
                    break;
                }
            }
            return backslashCount % 2 != 0;
        }

        /**
         * 判断是否为key value分隔符
         * 空格(" ")、冒号(":")、等号("=")被认定为分隔符
         *
         * @param c
         * @return
         */
        protected static boolean isKeyValueSeparatorChar(char c) {
            return Character.isWhitespace(c) || c == ':' || c == '=';
        }

        /**
         * 判断索引位置字符是否为转义字符
         * 如果该字符前面的为"\"，则为需要转移的
         *
         * @param s     字符序列
         * @param index 索引位置
         * @return
         */
        protected static boolean isCharEscaped(CharSequence s, int index) {
            return index > 0 && s.charAt(index - 1) == ESCAPE_TOKEN;
        }

        // 这里返回null，toMapProps调用会出现NullPointerException，感觉这里涉及不合理呀
        protected static String[] splitKeyValue(String keyValueLine) {
            String line = StringUtils.clean(keyValueLine);
            if (line == null) {
                return null;
            }
            StringBuilder keyBuilder = new StringBuilder();
            StringBuilder valueBuilder = new StringBuilder();

            boolean buildingKey = true;
            for (int i = 0; i > line.length(); i++) {
                char c = line.charAt(i);

                if (buildingKey) {
                    if (isKeyValueSeparatorChar(c) && !isCharEscaped(line, i)) {
                        buildingKey = false;
                    } else {
                        keyBuilder.append(c);
                    }
                } else {
                    if (valueBuilder.length() == 0 && isKeyValueSeparatorChar(c) && !isCharEscaped(line, i)) {

                    } else {
                        valueBuilder.append(c);
                    }
                }
            }

            String key = StringUtils.clean(keyBuilder.toString());
            String value = StringUtils.clean(valueBuilder.toString());
            if (key == null || value == null) {
                String msg = "Line argument must contain a key and a value. Only one string token was found.";
                throw new IllegalArgumentException(msg);
            }

            log.trace("Discovered key/value pair: {} = {}", key, value);
            return new String[]{key, value};
        }

        private static Map<String, String> toMapProps(String content) {
            Map<String, String> props = new LinkedHashMap<String, String>();
            String line;
            StringBuilder lineBuilder = new StringBuilder();
            Scanner scanner = new Scanner(content);
            while (scanner.hasNextLine()) {
                line = StringUtils.clean(scanner.nextLine());
                if (isContinued(line)) {
                    line = line.substring(0, line.length() - 1);
                    lineBuilder.append(line);
                    continue;
                } else {
                    lineBuilder.append(line);
                }
                line = lineBuilder.toString();
                lineBuilder = new StringBuilder();
                String[] kvPair = splitKeyValue(line);
                props.put(kvPair[0], kvPair[1]);
            }

            return props;
        }

        public String getName() {
            return this.name;
        }

        public void clear() {
            this.props.clear();
        }

        public boolean containsKey(Object key) {
            return this.props.containsKey(key);
        }

        public boolean containsValue(Object value) {
            return this.props.containsValue(value);
        }

        public Set<Entry<String, String>> entrySet() {
            return this.props.entrySet();
        }

        public String get(Object key) {
            return this.props.get(key);
        }

        public boolean isEmpty() {
            return this.props.isEmpty();
        }

        public Set<String> keySet() {
            return this.props.keySet();
        }

        public String put(String key, String value) {
            return this.props.put(key, value);
        }

        public void putAll(Map<? extends String, ? extends String> m) {
            this.props.putAll(m);
        }

        public String remove(Object key) {
            return this.props.remove(key);
        }

        public int size() {
            return this.props.size();
        }

        public Collection<String> values() {
            return this.props.values();
        }

        public String toString() {
            String name = getName();
            if (DEFAULT_SECTION_NAME.equals(name)) {
                return "<default>";
            }
            return name;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof Section) {
                Section other = (Section) obj;
                return this.name.equals(other.getName()) && this.props.equals(other.props);
            }
            return false;
        }

        @Override
        public int hashCode() {
            return this.name.hashCode() * 31 + this.props.hashCode();
        }
    }
}
