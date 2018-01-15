package com.youxiang.shiro.session.mgt;

import com.youxiang.shiro.session.InvalidSessionException;

import java.util.Collection;
import java.util.Date;

/**
 * Author: Rivers
 * Date: 2018/1/15 22:06
 */
public interface NativeSessionManager extends SessionManager {

    Date getStartTimestamp(SessionKey key);

    Date getLastAccessTime(SessionKey key);

    boolean isValid(SessionKey key);

    void checkValid(SessionKey key) throws InvalidSessionException;

    long getTimeout(SessionKey key) throws InvalidSessionException;

    void setTimeout(SessionKey key, long maxIdleTimeInMillis) throws InvalidSessionException;

    void touch(SessionKey key) throws InvalidSessionException;

    String getHost(SessionKey key);

    void stop(SessionKey key) throws InvalidSessionException;

    Collection<Object> getAttributeKeys(SessionKey key);

    Object getAttribute(SessionKey sessionKey, Object objectKey) throws InvalidSessionException;

    void setAttribute(SessionKey sessionKey, Object objectKey, Object value) throws InvalidSessionException;

    Object removeAttribute(SessionKey sessionKey, Object objectKey) throws InvalidSessionException;
}
