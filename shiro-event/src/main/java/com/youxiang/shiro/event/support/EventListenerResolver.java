package com.youxiang.shiro.event.support;

import java.util.List;

/**
 * Author: RiversLau
 * Date: 2018/1/5 16:27
 */
public interface EventListenerResolver {

    List<EventListener> getEventListeners(Object instance);
}
