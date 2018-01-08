package com.youxiang.shiro.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;

/**
 * Author: RiversLau
 * Date: 2018/1/8 14:56
 */
public abstract class LifecycleUtils {

    private static final Logger logger = LoggerFactory.getLogger(LifecycleUtils.class);

    public static void init(Object o) {
        if (o instanceof Initializable) {
            init((Initializable)o);
        }
    }

    public static void init(Initializable initializable) {
        initializable.init();
    }

    public static void init(Collection c) {
        if (c == null || c.isEmpty()) {
            return;
        }
        for (Object o : c) {
            init(o);
        }
    }

    public static void destroy(Object o) {
        if (o instanceof Destroyable) {
            destroy((Destroyable) o);
        } else if (o instanceof Collection) {
            destroy((Collection)o);
        }
    }

    public static void destroy(Destroyable d) {
        if (d != null) {
            try {
                d.destroy();
            } catch (Exception e) {
                if (logger.isDebugEnabled()) {
                    String msg = "Unable to cleanly destroy instance [" + d + "] of type [" + d.getClass().getName() + "]";
                    logger.debug(msg);
                }
            }
        }
    }

    public static void destroy(Collection c) {
        if (c == null || c.isEmpty()) {
            return;
        }
        for (Object o : c) {
            destroy(o);
        }
    }
}
