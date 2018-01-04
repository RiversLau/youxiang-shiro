package com.youxiang.shiro.subject;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * Author: RiversLau
 * Date: 2018/1/4 8:59
 */
public interface PrincipalCollection extends Iterable, Serializable {

    /**
     * 返回应用中唯一标识用户的primary principal
     * @return
     */
    Object getPrimaryPrincipal();

    /**
     * 返回第一个principal为type类型的，如果没有与type对应的principal，则返回null
     * 注意：如果用户未登录则返回null
     * @param type
     * @param <T>
     * @return
     */
    <T> T oneByType(Class<T> type);

    /**
     * 返回类型为type的principal，如果没有，则返回空集合
     * 注意：如果用户尚未登录，则返回空集合
     * @param type
     * @param <T>
     * @return
     */
    <T> Collection<T> byType(Class<T> type);

    /**
     * 从所有配置的Realms中获取单个主体所有的principal，并以List返回，如果没有principal，则返回空List
     * 注意：如果用户未登录，则返回空List
     * @return
     */
    List asList();

    /**
     * 从所有配置的Realms中获取单个主体所有的principal，并以Set返回，如果没有principal，则返回空Set
     * @return
     */
    Set asSet();

    /**
     * 获取单个主体在指定Realm中的principal collection，如果没有，则返回空Collection
     * @param realmName
     * @return
     */
    Collection fromRealm(String realmName);

    /**
     * 返回所有存在principals的Realm名字集合
     * @return
     */
    Set<String> getRealmNames();

    /**
     * 如果集合为空，返回true，否则返回false
     * @return
     */
    boolean isEmpty();
}
