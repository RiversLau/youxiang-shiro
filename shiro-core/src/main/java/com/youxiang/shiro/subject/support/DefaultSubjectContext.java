package com.youxiang.shiro.subject.support;

import com.youxiang.shiro.SecurityUtils;
import com.youxiang.shiro.UnavailableSecurityManagerException;
import com.youxiang.shiro.authc.AuthenticationInfo;
import com.youxiang.shiro.authc.AuthenticationToken;
import com.youxiang.shiro.authc.HostAuthenticationToken;
import com.youxiang.shiro.session.Session;
import com.youxiang.shiro.subject.PrincipalCollection;
import com.youxiang.shiro.subject.Subject;
import com.youxiang.shiro.subject.SubjectContext;
import com.youxiang.shiro.util.MapContext;
import com.youxiang.shiro.mgt.SecurityManager;
import com.youxiang.shiro.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;

/**
 * Author: RiversLau
 * Date: 2018/1/5 11:13
 */
public class DefaultSubjectContext extends MapContext implements SubjectContext {

    private static final String SECURITY_MANAGER = DefaultSubjectContext.class.getName() + ".SECURITY_MANAGER";

    private static final String SESSION_ID = DefaultSubjectContext.class.getName() + ".SESSION_ID";

    private static final String AUTHENTICATION_TOKEN = DefaultSubjectContext.class.getName() + ".AUTHENTICATION_TOKEN";

    private static final String AUTHENTICATION_INFO = DefaultSubjectContext.class.getName() + ".AUTHENTICATION_INFO";

    private static final String SUBJECT = DefaultSubjectContext.class.getName() + ".SUBJECT";

    private static final String PRINCIPALS = DefaultSubjectContext.class.getName() + ".PRINCIPALS";

    private static final String SESSION = DefaultSubjectContext.class.getName() + ".SESSION";

    private static final String AUTHENTICATED = DefaultSubjectContext.class.getName() + ".AUTHENTICATED";

    private static final String HOST = DefaultSubjectContext.class.getName() + ".HOST";

    public static final String SESSION_CREATION_ENABLED = DefaultSubjectContext.class.getName() + ".SESSION_CREATION_ENABLED";

    public static final String PRINCIPALS_SESSION_KEY = DefaultSubjectContext.class.getName() + "_PRINCIPALS_SESSION_KEY";

    public static final String AUTHENTICATED_SESSION_KEY = DefaultSubjectContext.class.getName() + "_AUTHENTICATED_SESSION_KEY";

    private static final transient Logger log = LoggerFactory.getLogger(DefaultSubjectContext.class);

    public DefaultSubjectContext() {
        super();
    }

    public DefaultSubjectContext(SubjectContext ctx) {
        super(ctx);
    }

    public SecurityManager getSecurityManager() {
        return getTypedValue(SECURITY_MANAGER, SecurityManager.class);
    }

    public void setSecurityManager(SecurityManager securityManager) {
        nullSafePut(SECURITY_MANAGER, securityManager);
    }

    public SecurityManager resolveSecurityManager() {
         SecurityManager securityManager = getSecurityManager();
         if (securityManager == null) {
             log.debug("No SecurityManager available in subject context map. " +
                    "Falling back to SecurityUtils.getSecurityManger() lookup.");
         }
        try {
            securityManager = SecurityUtils.getSecurityManager();
        } catch (UnavailableSecurityManagerException e) {
            log.debug("No SecurityManager available via SecurityUtils. Heuristics exhausted.");
        }
        return securityManager;
    }

    public Serializable getSessionId() {
        return getTypedValue(SESSION_ID, Serializable.class);
    }

    public void setSessionId(Serializable sessionId) {
        nullSafePut(SESSION_ID, sessionId);
    }

    public Subject getSubject() {
        return getTypedValue(SUBJECT, Subject.class);
    }

    public void setSubject(Subject subject) {
        nullSafePut(SUBJECT, subject);
    }

    public PrincipalCollection getPrincipals() {
        return getTypedValue(PRINCIPALS, PrincipalCollection.class);
    }

    private static boolean isEmpty(PrincipalCollection principals) {
        return principals == null || principals.isEmpty();
    }

    public void setPrincipals(PrincipalCollection principals) {
        if (!isEmpty(principals)) {
            put(PRINCIPALS, principals);
        }
    }

    public PrincipalCollection resolvePrincipals() {
        PrincipalCollection principals = getPrincipals();
        if (isEmpty(principals)) {
            AuthenticationInfo info = getAuthenticationInfo();
            if (info != null) {
                principals = info.getPrincipals();
            }
        }

        if (isEmpty(principals)) {
            Subject subject = getSubject();
            if (subject != null) {
                principals = subject.getPrincipals();
            }
        }

        if (isEmpty(principals)) {
            Session session = getSession();
            if (session != null) {
                principals = (PrincipalCollection) session.getAttribute(PRINCIPALS_SESSION_KEY);
            }
        }

        return principals;
    }

    public Session getSession() {
        return getTypedValue(SESSION, Session.class);
    }

    public void setSession(Session session) {
        nullSafePut(SESSION, session);
    }

    public Session resolveSession() {
        Session session = getSession();
        if (session == null) {
            Subject existingSubject = getSubject();
            if (existingSubject != null) {
                session = existingSubject.getSession();
            }
        }
        return session;
    }

    public boolean isSessionCreationEnabled() {
        Boolean val = getTypedValue(SESSION_CREATION_ENABLED, Boolean.class);
        return val == null || val;
    }

    public void setSessionCreationEnabled(boolean enabled) {
        nullSafePut(SESSION_CREATION_ENABLED, enabled);
    }

    public boolean isAuthenticated() {
        Boolean authc = getTypedValue(AUTHENTICATED, Boolean.class);
        return authc != null && authc;
    }

    public void setAuthenticated(boolean authenticated) {
        nullSafePut(AUTHENTICATED, authenticated);
    }

    public boolean resolveAuthenticated() {
        Boolean authc = getTypedValue(AUTHENTICATED, Boolean.class);
        if (authc == null) {
            AuthenticationInfo info = getAuthenticationInfo();
            authc = (info != null);
        }
        if (!authc) {
            Session session = resolveSession();
            if (session != null) {
                Boolean sessionAuthc = (Boolean) session.getAttribute(AUTHENTICATED_SESSION_KEY);
                authc = (sessionAuthc != null && sessionAuthc);
            }
        }
        return authc;
    }

    public AuthenticationInfo getAuthenticationInfo() {
        return getTypedValue(AUTHENTICATION_INFO, AuthenticationInfo.class);
    }

    public void setAuthenticationInfo(AuthenticationInfo info) {
        nullSafePut(AUTHENTICATION_INFO, info);
    }

    public AuthenticationToken getAuthenticationToken() {
        return getTypedValue(AUTHENTICATION_TOKEN, AuthenticationToken.class);
    }

    public void setAuthenticationToken(AuthenticationToken token) {
        nullSafePut(AUTHENTICATION_TOKEN, token);
    }

    public String getHost() {
        return getTypedValue(HOST, String.class);
    }

    public void setHost(String host) {
        if (StringUtils.hasText(host)) {
            nullSafePut(HOST, host);
        }
    }

    public String resolveHost() {
        String host = getHost();
        if (host == null) {
            AuthenticationToken token = getAuthenticationToken();
            if (token instanceof HostAuthenticationToken) {
                host = ((HostAuthenticationToken)token).getHost();
            }
        }
        if (host == null) {
            Session session = resolveSession();
            if (session != null) {
                host = session.getHost();
            }
        }
        return host;
    }
}
