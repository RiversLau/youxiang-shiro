package com.youxiang.shiro.config.event;

import com.youxiang.shiro.event.Event;

import java.util.Map;

/**
 * Author: RiversLau
 * Date: 2018/1/8 13:55
 */
public abstract class BeanEvent extends Event {

    private String beanName;
    private Object bean;
    private final Map<String, Object> beanContext;

    public BeanEvent(final String beanName, final Object bean, final Map<String, Object> beanContext) {
        super(bean);
        this.beanName = beanName;
        this.bean = bean;
        this.beanContext = beanContext;
    }

    public String getBeanName() {
        return beanName;
    }

    public Object getBean() {
        return bean;
    }

    public Map<String, Object> getBeanContext() {
        return beanContext;
    }
}
