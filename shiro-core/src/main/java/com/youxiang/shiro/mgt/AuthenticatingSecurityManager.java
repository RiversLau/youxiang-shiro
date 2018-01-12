package com.youxiang.shiro.mgt;

import com.youxiang.shiro.authc.Authenticator;
import com.youxiang.shiro.authc.pam.ModularRealmAuthenticator;

/**
 * Author: RiversLau
 * Date: 2018/1/12 11:18
 */
public abstract class AuthenticatingSecurityManager extends RealmSecurityManager {

    private Authenticator authenticator;

    public AuthenticatingSecurityManager() {
        super();
        this.authenticator = new ModularRealmAuthenticator();
    }
}
