package com.youxiang.shiro.event.support;

import com.youxiang.shiro.event.Subscribe;
import com.youxiang.shiro.util.ClassUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Author: RiversLau
 * Date: 2018/1/5 16:33
 */
public class AnnotationEventListenerResolver implements EventListenerResolver {

    private Class<? extends Annotation> annotationClass;

    public  AnnotationEventListenerResolver() {
        this.annotationClass = Subscribe.class;
    }

    public List<EventListener> getEventListeners(Object instance) {
        if (instance == null) {
            return Collections.emptyList();
        }

        List<Method> methods = ClassUtils.getAnnotatedMethods(instance.getClass(), getAnnotationClass());
        if (methods == null || methods.isEmpty()) {
            return Collections.emptyList();
        }

        List<EventListener> listeners = new ArrayList<EventListener>(methods.size());
        for (Method m : methods) {
            listeners.add(new SingleArgumentMethodEventListener(instance, m));
        }
        return listeners;
    }

    public Class<? extends Annotation> getAnnotationClass() {
        return annotationClass;
    }

    public void setAnnotationClass(Class<? extends Annotation> annotationClass) {
        this.annotationClass = annotationClass;
    }
}
