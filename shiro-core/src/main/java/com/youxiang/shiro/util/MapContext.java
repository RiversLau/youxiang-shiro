package com.youxiang.shiro.util;

import java.io.Serializable;
import java.util.*;

/**
 * Author: RiversLau
 * Date: 2018/1/5 11:20
 */
public class MapContext implements Map<String, Object>, Serializable {

    private final Map<String, Object> backingMap;

    public MapContext() {
        this.backingMap = new HashMap<String, Object>();
    }

    public MapContext(Map<String, Object> map) {
        this();
        if (!CollectionUtils.isEmpty(map)) {
            this.backingMap.putAll(map);
        }
    }

    protected <E> E getTypedValue(String key, Class<E> type) {
        E found = null;
        Object o = backingMap.get(key);
        if (o != null) {
            if (!type.isAssignableFrom(o.getClass())) {
                String msg = "Invalid object found in SubjectContext Map under key [" + key + "]. Excepted type " +
                        "was [" + type.getName() + "], but the object under that key is of type [" + o.getClass().getName() + "].";
                throw new IllegalArgumentException(msg);
            }
            found = (E) o;
        }
        return found;
    }

    protected void nullSafePut(String key, Object value) {
        if (value != null) {
            put(key, value);
        }
    }

    public int size() {
        return this.backingMap.size();
    }

    public boolean isEmpty() {
        return this.backingMap.isEmpty();
    }

    public boolean containsKey(Object key) {
        return this.backingMap.containsKey(key);
    }

    public boolean containsValue(Object value) {
        return this.backingMap.containsValue(value);
    }

    public Object get(Object key) {
        return this.backingMap.get(key);
    }

    public Object put(String key, Object value) {
        return this.backingMap.put(key, value);
    }

    public Object remove(Object key) {
        return this.backingMap.remove(key);
    }

    public void putAll(Map<? extends String, ?> m) {
        this.backingMap.putAll(m);
    }

    public void clear() {
        this.backingMap.clear();
    }

    public Set<String> keySet() {
        return Collections.unmodifiableSet(this.backingMap.keySet());
    }

    public Collection<Object> values() {
        return Collections.unmodifiableCollection(this.backingMap.values());
    }

    public Set<Entry<String, Object>> entrySet() {
        return Collections.unmodifiableSet(this.backingMap.entrySet());
    }
}
