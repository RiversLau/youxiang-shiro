package com.youxiang.shiro.event;

import java.util.EventObject;

/**
 * Author: RiversLau
 * Date: 2018/1/8 13:52
 */
public abstract class Event extends EventObject {
    private final long timestamp;

    public Event(Object source) {
        super(source);
        this.timestamp = System.currentTimeMillis();
    }

    public long getTimestamp() {
        return timestamp;
    }
}
