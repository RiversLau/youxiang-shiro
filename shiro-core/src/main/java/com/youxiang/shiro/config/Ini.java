package com.youxiang.shiro.config;

import com.youxiang.shiro.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Scanner;

/**
 * Author: RiversLau
 * Date: 2018/1/2 16:50
 */
public class Ini implements Map<String, Ini.Section> {

    private static transient final Logger log = LoggerFactory.getLogger(Ini.class);

    protected static final char ESCAPE_TOKEN = '\\';

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
         * @param c
         * @return
         */
        protected static boolean isKeyValueSeparatorChar(char c) {
            return Character.isWhitespace(c) || c == ':' || c == '=';
        }

        /**
         * 判断索引位置字符是否为转义字符
         * @param s 字符序列
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
    }
}
