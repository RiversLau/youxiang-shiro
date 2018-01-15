package com.youxiang.shiro.mgt;

import com.youxiang.shiro.session.mgt.SessionManager;

/**
 * Author: Rivers
 * Date: 2018/1/15 21:59
 */
public abstract class SessionsSecurityManager extends AuthorizingSecurityManager {

    private SessionManager sessionManager;

    public SessionsSecurityManager() {
        super();
        this.sessionManager = new DefaultSessionManager();
        applyCacheManagerToSessionManager();
    }
}
