package com.youxiang.shiro.authc;

import com.youxiang.shiro.subject.PrincipalCollection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Author: RiversLau
 * Date: 2018/1/12 11:22
 */
public abstract class AbstractAuthenticator implements Authenticator, LogoutAware {

    private static final transient Logger logger = LoggerFactory.getLogger(AbstractAuthenticator.class);

    private Collection<AuthenticationListener> listeners;

    public AbstractAuthenticator() {
        this.listeners = new ArrayList<AuthenticationListener>();
    }

    public void setAuthenticationListeners(Collection<AuthenticationListener> listeners) {
        if (listeners == null) {
            this.listeners = new ArrayList<AuthenticationListener>();
        } else {
            this.listeners = listeners;
        }
    }

    public Collection<AuthenticationListener> getAuthenticationListeners() {
        return this.listeners;
    }

    protected void notifySuccess(AuthenticationToken token, AuthenticationInfo info) {
        for (AuthenticationListener listener : this.listeners) {
            listener.onSuccess(token, info);
        }
    }

    protected void notifyFailure(AuthenticationToken token, AuthenticationException ae) {
        for (AuthenticationListener listener : this.listeners) {
            listener.onFailure(token, ae);
        }
    }

    protected void notifyLogout(PrincipalCollection principals) {
        for (AuthenticationListener listener : this.listeners) {
            listener.onLogout(principals);
        }
    }

    public void onLogout(PrincipalCollection principals) {
        notifyLogout(principals);
    }

    public final AuthenticationInfo authenticate(AuthenticationToken token) {

        if (token == null) {
            throw new IllegalArgumentException("Method argument (authentication token) cannot be null.");
        }
        logger.trace("Authentication attempt received for token [{}]", token);

        AuthenticationInfo info;
        try {
            info = doAuthenticate(token);
            if (info == null) {
                String msg = "No account information found for authentication token [" + token + "] by this " +
                        "Authenticator instance. Please check that it is configured correctly.";
                throw new AuthenticationException(msg);
            }
        } catch (Throwable t) {
            AuthenticationException ae = null;
            if (t instanceof AuthenticationException) {
                ae = (AuthenticationException) t;
            }
            if (ae == null) {
                String msg = "Authentication failed for token submission [" + token + "].  Possible unexpected " +
                        "error? (Typical or expected login exceptions should extend from AuthenticationException).";
                ae = new AuthenticationException(msg, t);
                if (logger.isWarnEnabled()) {
                    logger.warn(msg, t);
                }
            }
            try {
                notifyFailure(token, ae);
            } catch (Throwable t2) {
                if (logger.isWarnEnabled()) {
                    String msg = "Unable to send notification for failed authentication attempt - listener error?.  " +
                            "Please check your AuthenticationListener implementation(s).  Logging sending exception " +
                            "and propagating original AuthenticationException instead...";
                    logger.warn(msg, t2);
                }
            }
            throw ae;
        }
        return info;
    }

    protected abstract AuthenticationInfo doAuthenticate(AuthenticationToken token) throws AuthenticationException;
}
