package com.youxiang.shiro.session.mgt;

import com.youxiang.shiro.session.InvalidSessionException;
import com.youxiang.shiro.session.Session;

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;

/**
 * Author: Rivers
 * Date: 2018/1/17 21:39
 */
public class DelegatingSession implements Session, Serializable {

    private final SessionKey key;

    private Date startTimestamp = null;
    private String host = null;

    private final transient NativeSessionManager sessionManager;

    public DelegatingSession(NativeSessionManager sessionManager, SessionKey key) {
        if (sessionManager == null) {
            throw new IllegalArgumentException("sessionManager argument cannot be null.");
        }
        if (key == null) {
            throw new IllegalArgumentException("sessionKey argument cannot be null.");
        }
        if (key.getSessionId() == null) {
            String msg = "The " + DelegatingSession.class.getName() + " implementation requires that the " +
                    "SessionKey argument returns a non-null sessionId to support the " +
                    "Session.getId() invocations.";
            throw new IllegalArgumentException(msg);
        }
        this.sessionManager = sessionManager;
        this.key = key;
    }

    public Serializable getId() {
        return key.getSessionId();
    }

    public Date getStartTimestamp() {
        if (startTimestamp == null) {
            startTimestamp = sessionManager.getStartTimestamp(key);
        }
        return startTimestamp;
    }

    public Date getLastAccessTime() {
        return sessionManager.getLastAccessTime(key);
    }

    public long getTimeout() throws InvalidSessionException {
        return sessionManager.getTimeout(key);
    }

    public void setTimeout(long maxIdleTimeInMillis) throws InvalidSessionException {
        sessionManager.setTimeout(key, maxIdleTimeInMillis);
    }

    public String getHost() {
        if (host == null) {
            host = sessionManager.getHost(key);
        }
        return host;
    }

    public void touch() throws InvalidSessionException {
        sessionManager.touch(key);
    }

    public void stop() throws InvalidSessionException {
        sessionManager.stop(key);
    }

    public Collection<Object> getAttributeKeys() throws InvalidSessionException {
        return sessionManager.getAttributeKeys(key);
    }

    public Object getAttribute(Object key) throws InvalidSessionException {
        return sessionManager.getAttribute(this.key, key);
    }

    public void setAttribute(Object key, Object value) throws InvalidSessionException {
        if (value == null) {
            removeAttribute(key);
        } else {
            sessionManager.setAttribute(this.key, key, value);
        }
    }

    public Object removeAttribute(Object key) throws InvalidSessionException {
        return sessionManager.removeAttribute(this.key, key);
    }
}
