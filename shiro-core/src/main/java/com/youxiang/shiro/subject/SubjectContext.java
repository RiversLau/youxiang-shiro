package com.youxiang.shiro.subject;

import com.youxiang.shiro.authc.AuthenticationInfo;
import com.youxiang.shiro.authc.AuthenticationToken;
import com.youxiang.shiro.mgt.SecurityManager;
import com.youxiang.shiro.session.Session;

import java.io.Serializable;
import java.util.Map;

/**
 * Author: RiversLau
 * Date: 2018/1/4 17:52
 */
public interface SubjectContext extends Map<String, Object> {

    SecurityManager getSecurityManager();

    void setSecurityManager(SecurityManager securityManger);

    SecurityManager resolveSecurityManager();

    Serializable getSessionId();

    void setSessionId(Serializable sessionId);

    Subject getSubject();

    void setSubject(Subject subject);

    PrincipalCollection getPrincipals();

    PrincipalCollection resolvePrincipals();

    void setPrincipals(PrincipalCollection principals);

    Session getSession();

    void setSession(Session session);

    Session resolveSession();

    boolean isAuthenticated();

    void setAuthenticated(boolean authenticated);

    boolean isSessionCreationEnabled();

    void setSessionCreationEnabled(boolean enabled);

    boolean resolveAuthenticated();

    AuthenticationInfo getAuthenticationInfo();

    void setAuthenticationInfo(AuthenticationInfo authenticationInfo);

    AuthenticationToken getAuthenticationToken();

    void setAuthenticationToken(AuthenticationToken token);

    String getHost();

    void setHost(String host);

    String resolveHost();
}