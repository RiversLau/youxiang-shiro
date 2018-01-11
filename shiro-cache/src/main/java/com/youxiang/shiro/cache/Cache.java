package com.youxiang.shiro.cache;

import java.util.Collection;
import java.util.Set;

/**
 * Author: RiversLau
 * Date: 2018/1/11 11:47
 */
public interface Cache<K, V> {

    V get(K key) throws CacheException;

    V put(K key, V value) throws CacheException;

    V remove(K key) throws CacheException;

    void clear() throws CacheException;

    int size();

    Set<K> keys();

    Collection<V> values();
}
