package com.youxiang.shiro.mgt;

import com.youxiang.shiro.cache.CacheManager;
import com.youxiang.shiro.cache.CacheManagerAware;
import com.youxiang.shiro.event.EventBus;
import com.youxiang.shiro.event.EventBusAware;
import com.youxiang.shiro.realm.Realm;
import com.youxiang.shiro.util.LifecycleUtils;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Author: RiversLau
 * Date: 2018/1/11 11:15
 */
public abstract class RealmSecurityManager extends CachingSecurityManager {

    private Collection realms;

    public RealmSecurityManager() {
        super();
    }

    public void setRealm(Realm realm) {
        if (realm == null) {
            throw new IllegalArgumentException("Realm argument cannot be null");
        }
        Collection<Realm> realms = new ArrayList<Realm>(1);
        realms.add(realm);
        setRealms(realms);
    }

    public void setRealms(Collection<Realm> realms) {
        if (realms == null) {
            throw new IllegalArgumentException("Realms collection argument cannot be null.");
        }
        if (realms.isEmpty()) {
            throw new IllegalArgumentException("Realms collection argument cannot be empty.");
        }
        this.realms = realms;
        afterRealmsSet();
    }

    protected void afterRealmsSet() {
        applyCacheManagerToRealms();
        applyEventBusToRealms();
    }

    public Collection<Realm> getRealms() {
        return realms;
    }

    protected void applyCacheManagerToRealms() {
        CacheManager cacheManager = getCacheManager();
        Collection<Realm> realms = getRealms();
        if (cacheManager != null && realms != null && !realms.isEmpty()) {
            for (Realm realm : realms) {
                if (realm instanceof CacheManagerAware) {
                    ((CacheManagerAware) realm).setCacheManager(cacheManager);
                }
            }
        }
    }

    protected void applyEventBusToRealms() {
        EventBus eventBus = getEventBus();
        Collection<Realm> realms = getRealms();
        if (eventBus != null && realms != null && !realms.isEmpty()) {
            for (Realm realm : realms) {
                if (realm instanceof EventBusAware) {
                    ((EventBusAware) realm).setEventBus(eventBus);
                }
            }
        }
    }

    @Override
    protected void afterCacheManagerSet() {
        super.afterCacheManagerSet();
        applyCacheManagerToRealms();
    }

    @Override
    protected void afterEventBusSet() {
        super.afterEventBusSet();
        applyEventBusToRealms();
    }

    public void destroy() {
        LifecycleUtils.destroy(getRealms());
        this.realms = null;
        super.destroy();
    }
}
