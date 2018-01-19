package com.youxiang.shiro.session.mgt.eis;

import com.youxiang.shiro.session.Session;
import com.youxiang.shiro.session.UnknownSessionException;

import java.io.Serializable;
import java.util.Collection;

/**
 * Author: RiversLau
 * Date: 2018/1/19 11:14
 */
public interface SessionDAO {

    Serializable createSession(Session session);

    Session readSession(Serializable sessionId) throws UnknownSessionException;

    void update(Session session) throws UnknownSessionException;

    void delete(Session session);

    Collection<Session> getActiveSessions();
}
