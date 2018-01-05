package com.youxiang.shiro.event.support;

import java.util.Comparator;

/**
 * Author: RiversLau
 * Date: 2018/1/5 15:55
 */
public class EventListenerComparator implements Comparator<EventListener> {

    private static final EventClassComparator EVENT_CLASS_COMPARATOR = new EventClassComparator();

    public int compare(EventListener a, EventListener b) {
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
            if (a instanceof TypedEventListener) {
                TypedEventListener ta = (TypedEventListener) a;
                if (b instanceof TypedEventListener) {
                    TypedEventListener tb = (TypedEventListener) b;
                    return EVENT_CLASS_COMPARATOR.compare(ta.getEventType(), tb.getEventType());
                } else {
                    return -1;
                }
            } else {
                if (b instanceof TypedEventListener) {
                    return 1;
                } else {
                    return 0;
                }
            }
        }
    }
}
