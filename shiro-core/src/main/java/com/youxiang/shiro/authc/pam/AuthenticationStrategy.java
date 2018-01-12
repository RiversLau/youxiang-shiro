package com.youxiang.shiro.authc.pam;

import com.youxiang.shiro.authc.AuthenticationException;
import com.youxiang.shiro.authc.AuthenticationInfo;
import com.youxiang.shiro.authc.AuthenticationToken;
import com.youxiang.shiro.realm.Realm;

import java.util.Collection;

/**
 * Author: RiversLau
 * Date: 2018/1/12 12:00
 */
public interface AuthenticationStrategy {

    AuthenticationInfo beforeAllAttempts(Collection<? extends Realm> realms, AuthenticationToken token)
            throws AuthenticationException;

    AuthenticationInfo beforeAttempt(Realm realm, AuthenticationToken token, AuthenticationInfo aggregate)
            throws AuthenticationException;

    AuthenticationInfo afterAttempt(Realm realm, AuthenticationToken token, AuthenticationInfo singleRealmInfo, AuthenticationInfo aggregateInfo, Throwable t)
            throws AuthenticationException;

    AuthenticationInfo afterAllAttempts(AuthenticationToken token, AuthenticationInfo aggregate)
            throws AuthenticationException;
}
