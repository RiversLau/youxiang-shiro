package com.youxiang.shiro.util;

import java.util.Collection;
import java.util.Map;

/**
 * Author: RiversLau
 * Date: 2018/1/2 17:13
 */
public class CollectionUtils {

    public static boolean isEmpty(Collection c) {
        return c == null || c.isEmpty();
    }

    public static boolean isEmpty(Map m) {
        return m == null || m.isEmpty();
    }
}
