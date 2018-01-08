package com.youxiang.shiro.config.event;

import java.util.Map;

/**
 * Author: RiversLau
 * Date: 2018/1/8 13:59
 */
public class ConfiguredBeanEvent extends BeanEvent {

    public ConfiguredBeanEvent(final String beanName, final Object bean, final Map<String, Object> beanContext) {
        super(beanName, bean, beanContext);
    }
}
