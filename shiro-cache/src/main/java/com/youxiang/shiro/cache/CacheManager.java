package com.youxiang.shiro.cache;

/**
 * Author: RiversLau
 * Date: 2018/1/11 11:47
 */
public interface CacheManager {

    <K, V> Cache<K, V> getCache(String name) throws CacheException;
}
