package com.youxiang.shiro.event.support;

/**
 * Author: RiversLau
 * Date: 2018/1/5 16:24
 */
public interface TypedEventListener extends EventListener {

    Class getEventType();
}
