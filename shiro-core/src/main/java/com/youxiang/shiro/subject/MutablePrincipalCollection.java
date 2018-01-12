package com.youxiang.shiro.subject;

import java.util.Collection;

/**
 * Author: RiversLau
 * Date: 2018/1/12 14:12
 */
public interface MutablePrincipalCollection extends PrincipalCollection {

    void add(Object principal, String realmName);

    void addAll(Collection principals, String realmName);

    void addAll(PrincipalCollection principals);

    void clear();
}
