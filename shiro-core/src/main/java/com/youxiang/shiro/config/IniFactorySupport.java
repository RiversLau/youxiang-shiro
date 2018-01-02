package com.youxiang.shiro.config;

import com.youxiang.shiro.util.AbstractFactory;
import com.youxiang.shiro.util.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Author: RiversLau
 * Date: 2018/1/2 16:36
 */
public abstract class IniFactorySupport<T> extends AbstractFactory<T> {

    public static final String DEFAULT_INI_RESOURCE_PATH = "classpath:shiro.ini";

    private static transient final Logger log = LoggerFactory.getLogger(IniFactorySupport.class);

    private Ini ini;

    protected IniFactorySupport() {
    }

    protected IniFactorySupport(Ini ini) {
        this.ini = ini;
    }

    public Ini getIni() {
        return ini;
    }

    public void setIni(Ini ini) {
        this.ini = ini;
    }

    public static Ini loadDefaultClassPathIni() {
        Ini ini = null;
        if (ResourceUtils.resourceExists(DEFAULT_INI_RESOURCE_PATH)) {
            log.debug("Found shiro.ini at the root of the classpath.");
            ini = new Ini();
            ini.loadFromPath(DEFAULT_INI_RESOURCE_PATH);
            if (CollectionUtils.isEmpty(ini)) {
                log.warn("shiro.ini found at the root of the classpath, but it did not contain any data.");
            }
        }
        return ini;
    }

    protected Ini resolveIni() {
        Ini ini = getIni();
        if (CollectionUtils.isEmpty(ini)) {
            log.debug("Null or empty Ini instance. Falling back to the default {} file", DEFAULT_INI_RESOURCE_PATH);
            ini = loadDefaultClassPathIni();
        }
        return ini;
    }

    public T createInstance() {
        Ini ini = resolveIni();

        T instance;

        if (CollectionUtils.isEmpty(ini)) {
            log.debug("No populated Ini instance. Creating a default instance.");
            instance = createDefaultInstance();
            if (instance == null) {
                String msg = getClass().getName() + " implemention did not return a default instance in " +
                        "the event of a null/empty Ini configuration. This is required to support the " +
                        "Factory instance. Please check your implemention.";
                throw new IllegalStateException(msg);
            }
        } else {
            log.debug("Creating instance from Ini [" + ini + "]");
            instance = createInstance(ini);
            if (instance == null) {
                String msg = getClass().getName() + " implemention did not return a constructed instance from " +
                        "the createInstance(Ini) method implemention.";
                throw new IllegalStateException(msg);
            }
        }

        return instance;
    }

    protected abstract T createInstance(Ini ini);

    protected abstract T createDefaultInstance();
}
