package com.youxiang.shiro.config.event;

import java.util.Map;

/**
 * Author: RiversLau
 * Date: 2018/1/8 14:01
 */
public class InitializedBeanEvent extends BeanEvent {

    public InitializedBeanEvent(final String beanName, final Object bean, final Map<String, Object> beanContext) {
        super(beanName, bean, beanContext);
    }
}
