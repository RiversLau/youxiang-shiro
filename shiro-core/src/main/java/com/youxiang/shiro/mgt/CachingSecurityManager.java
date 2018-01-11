package com.youxiang.shiro.mgt;

import com.youxiang.shiro.cache.CacheManager;
import com.youxiang.shiro.cache.CacheManagerAware;
import com.youxiang.shiro.event.EventBus;
import com.youxiang.shiro.event.EventBusAware;
import com.youxiang.shiro.event.support.DefaultEventBus;
import com.youxiang.shiro.util.Destroyable;
import com.youxiang.shiro.util.LifecycleUtils;

/**
 * Author: RiversLau
 * Date: 2018/1/11 11:42
 */
public abstract class CachingSecurityManager implements SecurityManager, Destroyable, CacheManagerAware, EventBusAware {

    private CacheManager cacheManager;

    private EventBus eventBus;

    public CachingSecurityManager() {
        setEventBus(new DefaultEventBus());
    }

    public CacheManager getCacheManager() {
        return cacheManager;
    }

    public void setCacheManager(CacheManager cacheManager) {
        this.cacheManager = cacheManager;
        afterCacheManagerSet();
    }

    protected void afterCacheManagerSet() {
        applyEventBusToCacheManager();
    }

    public EventBus getEventBus() {
         return eventBus;
    }

    public void setEventBus(EventBus eventBus) {
        this.eventBus = eventBus;
        afterEventBusSet();
    }

    protected void applyEventBusToCacheManager() {
        if (this.eventBus != null && this.cacheManager != null && this.cacheManager instanceof EventBusAware) {
            ((EventBusAware) this.cacheManager).setEventBus(this.eventBus);
        }
    }

    protected void afterEventBusSet() {
        applyEventBusToCacheManager();
    }

    public void destroy() {
        LifecycleUtils.destroy(getCacheManager());
        this.cacheManager = null;
        LifecycleUtils.destroy(getEventBus());
        this.eventBus = null;
    }
}
