package com.youxiang.shiro.config;

import com.youxiang.shiro.event.EventBus;
import org.apache.commons.beanutils.BeanUtilsBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedHashMap;
import java.util.Map;

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
}
