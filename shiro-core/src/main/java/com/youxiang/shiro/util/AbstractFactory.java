package com.youxiang.shiro.util;

/**
 * Author: RiversLau
 * Date: 2018/1/2 16:28
 */
public abstract class AbstractFactory<T> implements Factory<T> {

    private boolean singleton;
    private T singletonInstance;

    public AbstractFactory() {
        this.singleton = true;
    }

    public boolean isSingleton() {
        return singleton;
    }

    public void setSingleton(boolean singleton) {
        this.singleton = singleton;
    }

    public T getInstance() {
        T instance;
        if (isSingleton()) {
            if (this.singletonInstance == null) {
                this.singletonInstance = createInstance();
            }
            instance = this.singletonInstance;
        } else {
            instance = createInstance();
        }
        if (instance == null) {
            String msg = "Factory 'createInstance' implemention returned a null object.";
            throw new IllegalStateException(msg);
        }
        return instance;
    }

    public abstract T createInstance();
}
