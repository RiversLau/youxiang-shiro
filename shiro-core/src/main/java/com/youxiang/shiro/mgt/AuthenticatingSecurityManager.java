package com.youxiang.shiro.mgt;

import com.youxiang.shiro.authc.AuthenticationException;
import com.youxiang.shiro.authc.AuthenticationInfo;
import com.youxiang.shiro.authc.AuthenticationToken;
import com.youxiang.shiro.authc.Authenticator;
import com.youxiang.shiro.authc.pam.ModularRealmAuthenticator;
import com.youxiang.shiro.util.LifecycleUtils;

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

    public Authenticator getAuthenticator() {
        return authenticator;
    }

    public void setAuthenticator(Authenticator authenticator) {
        if (authenticator == null) {
            String msg = "Authenticator argument cannot be null.";
            throw new IllegalArgumentException(msg);
        }
        this.authenticator = authenticator;
    }

    protected void afterRealmsSet() {
        super.afterRealmsSet();
        if (this.authenticator instanceof ModularRealmAuthenticator) {
            ((ModularRealmAuthenticator) this.authenticator).setRealms(getRealms());
        }
    }

    public AuthenticationInfo authenticate(AuthenticationToken authenticationToken) throws AuthenticationException {
        return this.authenticator.authenticate(authenticationToken);
    }

    public void destroy() {
        LifecycleUtils.destroy(authenticator);
        this.authenticator = null;
        super.destroy();
    }
}
