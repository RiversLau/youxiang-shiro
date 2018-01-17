package com.youxiang.shiro.session.mgt;

import java.io.Serializable;

/**
 * Author: Rivers
 * Date: 2018/1/17 21:37
 */
public class DefaultSessionKey implements SessionKey, Serializable {

    private Serializable sessionId;

    public DefaultSessionKey() {

    }

    public DefaultSessionKey(Serializable sessionId) {
        this.sessionId = sessionId;
    }

    public Serializable getSessionId() {
        return sessionId;
    }

    public void setSessionId(Serializable sessionId) {
        this.sessionId = sessionId;
    }
}
