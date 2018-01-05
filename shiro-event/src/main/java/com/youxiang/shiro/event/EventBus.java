package com.youxiang.shiro.event;

/**
 * Author: RiversLau
 * Date: 2018/1/5 15:40
 */
public interface EventBus {

    void publish(Object event);

    void register(Object subscriber);

    void unregister(Object subscriber);
}
