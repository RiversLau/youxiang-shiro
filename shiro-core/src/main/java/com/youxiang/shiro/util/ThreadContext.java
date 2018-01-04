package com.youxiang.shiro.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * Author: RiversLau
 * Date: 2018/1/4 17:56
 */
public abstract class ThreadContext {

    private static final Logger log = LoggerFactory.getLogger(ThreadContext.class);

    public static final String SECURITY_MANAGER_KEY = ThreadContext.class.getName() + "_SECURITY_MANAGER_KEY";
    public static final String SUBJECT_KEY = ThreadContext.class.getName() + "_SUBJECT_KEY";

    private static final ThreadLocal<Map<Object, Object>> resources = new InheritableThreadLocalMap<Map<Object, Object>>();

    private static final class InheritableThreadLocalMap<T extends Map<Object, Object>> extends InheritableThreadLocal<Map<Object, Object>> {
        protected Map<Object, Object> childValue(Map<Object, Object> parentValue) {
            if (parentValue != null) {
                return (Map<Object, Object>)((HashMap<Object, Object>) parentValue).clone();
            } else {
                return null;
            }
        }
    }
}
