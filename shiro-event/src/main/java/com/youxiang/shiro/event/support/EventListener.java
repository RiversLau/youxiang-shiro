package com.youxiang.shiro.event.support;

/**
 * Author: RiversLau
 * Date: 2018/1/5 15:55
 */
public interface EventListener {

    boolean accepts(Object event);

    void onEvent(Object event);
}
