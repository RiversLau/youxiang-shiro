package com.youxiang.shiro.event.support;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

/**
 * Author: RiversLau
 * Date: 2018/1/5 16:41
 */
public class SingleArgumentMethodEventListener implements TypedEventListener {

    private final Object target;
    private final Method method;

    public SingleArgumentMethodEventListener(Object target, Method method) {
        this.target = target;
        this.method = method;

        getMethodArgumentType(method);

        assertPublicMethod(method);
    }

    public Object getTarget() {
        return this.target;
    }

    public Method getMethod() {
        return this.method;
    }

    private void assertPublicMethod(Method method) {
        int modifiers = method.getModifiers();
        if (!Modifier.isPublic(modifiers)) {
            throw new IllegalArgumentException("Event handler method [" + method + "] must be public.");
        }
    }

    public boolean accepts(Object event) {
        return event != null && getEventType().isInstance(event);
    }

    public Class getEventType() {
        return getMethodArgumentType(getMethod());
    }

    public void onEvent(Object event) {
        Method method = getMethod();
        try {
            method.invoke(getTarget(), event);
        } catch (Exception e) {
            throw new IllegalStateException("Unable to invoke event handler method [" + method + "].", e);
        }
    }

    protected Class getMethodArgumentType(Method method) {
        Class[] paramType = method.getParameterTypes();
        if (paramType.length != 1) {
            String msg = "Event handler method must accept a single argument.";
            throw new IllegalArgumentException(msg);
        }
        return paramType[0];
    }
}
