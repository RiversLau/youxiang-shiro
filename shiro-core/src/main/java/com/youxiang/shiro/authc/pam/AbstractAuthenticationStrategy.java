package com.youxiang.shiro.authc.pam;

import com.youxiang.shiro.authc.*;
import com.youxiang.shiro.realm.Realm;

import java.util.Collection;

/**
 * Author: RiversLau
 * Date: 2018/1/12 14:00
 */
public abstract class AbstractAuthenticationStrategy implements AuthenticationStrategy {

    public AuthenticationInfo beforeAllAttempts(Collection<? extends Realm> realms, AuthenticationToken token) throws AuthenticationException {
        return new SimpleAuthenticationInfo();
    }

    public AuthenticationInfo beforeAttempt(Realm realm, AuthenticationToken token, AuthenticationInfo aggregate) throws AuthenticationException {
        return aggregate;
    }

    public AuthenticationInfo afterAttempt(Realm realm, AuthenticationToken token, AuthenticationInfo singleRealmInfo, AuthenticationInfo aggregateInfo, Throwable t) throws AuthenticationException {
        AuthenticationInfo info;
        if (singleRealmInfo == null) {
            info = aggregateInfo;
        } else {
            if (aggregateInfo == null) {
                info = singleRealmInfo;
            } else {
                info = merge(singleRealmInfo, aggregateInfo);
            }
        }
        return info;
    }

    protected AuthenticationInfo merge(AuthenticationInfo info, AuthenticationInfo aggregate) {
        if (aggregate instanceof MergableAuthenticationInfo) {
            ((MergableAuthenticationInfo) aggregate).merge(info);
            return aggregate;
        } else {
            throw new IllegalArgumentException("Attempt to merge authentication info from multiple realms, but aggregate " +
                    "AuthenticationInfo is not of type MergableAuthenticationInfo.");
        }
    }

    public AuthenticationInfo afterAllAttempts(AuthenticationToken token, AuthenticationInfo aggregate) throws AuthenticationException {
        return aggregate;
    }
}
