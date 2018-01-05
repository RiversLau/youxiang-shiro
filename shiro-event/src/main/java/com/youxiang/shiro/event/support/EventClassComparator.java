package com.youxiang.shiro.event.support;

import java.util.Comparator;

/**
 * Author: RiversLau
 * Date: 2018/1/5 15:56
 */
public class EventClassComparator implements Comparator<Class> {

    public int compare(Class a, Class b) {
        if (a == null) {
            if (b == null) {
                return 0;
            } else {
                return -1;
            }
        } else if (b == null) {
            return 1;
        } else if (a == b || a.equals(b)) {
            return 0;
        } else {
            if (a.isAssignableFrom(b)) {
                return 1;
            } else if (b.isAssignableFrom(a)) {
                return -1;
            } else {
                return 0;
            }
        }
    }
}
