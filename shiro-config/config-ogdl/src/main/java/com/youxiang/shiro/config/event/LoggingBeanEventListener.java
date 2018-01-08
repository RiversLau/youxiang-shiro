package com.youxiang.shiro.config.event;

import com.youxiang.shiro.event.Subscribe;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Author: RiversLau
 * Date: 2018/1/8 14:06
 */
public class LoggingBeanEventListener {

    private static final Logger log = LoggerFactory.getLogger(LoggingBeanEventListener.class);
    private static final String SUFFIX = BeanEvent.class.getSimpleName();

    @Subscribe
    public void onEvent(BeanEvent e) {
        String className = e.getClass().getSimpleName();
        int i = className.lastIndexOf(SUFFIX);
        String subclassPrefix = i > 0 ? className.substring(0, i) : className;
        log.trace("{} bean '{}' [{}]", new Object[]{subclassPrefix, e.getBeanName(), e.getBean()});
    }
}
