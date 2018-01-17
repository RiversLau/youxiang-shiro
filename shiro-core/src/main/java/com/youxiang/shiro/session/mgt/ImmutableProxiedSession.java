package com.youxiang.shiro.session.mgt;

import com.youxiang.shiro.session.InvalidSessionException;
import com.youxiang.shiro.session.ProxiedSession;
import com.youxiang.shiro.session.Session;

/**
 * Author: Rivers
 * Date: 2018/1/17 22:00
 */
public class ImmutableProxiedSession extends ProxiedSession {

    public ImmutableProxiedSession(Session target) {
        super(target);
    }

    public void throwImmutableException() {
        String msg = "This session is immutable and read-only - it cannot be altered.  This is usually because " +
                "the session has been stopped or expired already.";
        throw new InvalidSessionException(msg);
    }

    @Override
    public void setTimeout(long maxIdleTimeInMillis) throws InvalidSessionException {
        throwImmutableException();
    }

    @Override
    public void touch() throws InvalidSessionException {
        throwImmutableException();
    }

    @Override
    public void stop() throws InvalidSessionException {
        throwImmutableException();
    }

    @Override
    public void setAttribute(Object key, Object value) throws InvalidSessionException {
        throwImmutableException();
    }

    @Override
    public Object removeAttribute(Object key) throws InvalidSessionException {
        throwImmutableException();
        throw new InternalError("This code should never execute - please report this as a bug!");
    }
}
