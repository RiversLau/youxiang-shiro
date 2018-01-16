package com.youxiang.shiro.session.mgt;

import com.youxiang.shiro.authz.AuthorizationException;
import com.youxiang.shiro.event.EventBus;
import com.youxiang.shiro.event.EventBusAware;
import com.youxiang.shiro.session.InvalidSessionException;
import com.youxiang.shiro.session.Session;
import com.youxiang.shiro.session.SessionException;
import com.youxiang.shiro.session.SessionListener;
import com.youxiang.shiro.util.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;

/**
 * Author: Rivers
 * Date: 2018/1/16 21:12
 */
public abstract class AbstractNativeSessionManager extends AbstractSessionManager implements NativeSessionManager, EventBusAware {

    private static final Logger logger = LoggerFactory.getLogger(AbstractNativeSessionManager.class);

    private EventBus eventBus;
    private Collection<SessionListener> listeners;

    public AbstractNativeSessionManager() {
        this.listeners = new ArrayList<SessionListener>();
    }

    public void setListeners(Collection<SessionListener> listeners) {
        this.listeners = listeners != null ? listeners : new ArrayList<SessionListener>();
    }

    public Collection<SessionListener> getListeners() {
        return listeners;
    }

    public void setEventBus(EventBus eventBus) {
        this.eventBus = eventBus;
    }

    public EventBus getEventBus() {
        return eventBus;
    }

    protected void publishEvent(Object event) {
        if (this.eventBus != null) {
            this.eventBus.publish(event);
        }
    }

    public Session start(SessionContext context) {
        Session session = createSession(context);
        applyGlobalSessionTimeout(session);
        onStart(session, context);
        notifyStart(session);
        return createExposedSession(session, context);
    }

    protected abstract Session createSession(SessionContext context) throws AuthorizationException;

    protected void applyGlobalSessionTimeout(Session session) {
        session.setTimeout(getGlobalSessionTimeout());
        onChange(session);
    }

    protected void onStart(Session session, SessionContext context) {
        // do nothing
    }

    public Session getSession(SessionKey key) throws SessionException {
        Session session = lookupSession(key);
        return session != null ? createExposedSession(session, key) : null;
    }

    private Session lookupSession(SessionKey key) throws SessionException {
        if (key == null) {
            throw new NullPointerException("SessionKey cannot be null.");
        }
        return doGetSession(key);
    }

    private Session lookupRequiredSession(SessionKey key) throws SessionException {
        Session session = lookupSession(key);
        if (session == null) {
            String msg = "Unable to locate required Session instance based on SessionKey [" + key + "].";
            throw new UnknownSessionException(msg);
        }
        return session;
    }

    protected abstract Session doGetSession(SessionKey key) throws InvalidSessionException;

    protected Session createExposedSession(Session session, SessionContext context) {
        return new DelegatingSession(this, new DefaultSessionKey(session.getId()));
    }

    protected Session createExposedSession(Session session, SessionKey key) {
        return new DelegatingSession(this, new DefaultSessionKey(session.getId()));
    }

    protected Session beforeInvalidNotification(Session session) {
        return new ImmutableProxiedSession(session);
    }

    protected void notifyStart(Session session) {
        for (SessionListener listener : this.listeners) {
            listener.onStart(session);
        }
    }

    protected void notifyStop(Session session) {
        Session forNotification = beforeInvalidNotification(session);
        for (SessionListener listener : this.listeners) {
            listener.onStop(forNotification);
        }
    }

    protected void notifyExpiration(Session session) {
        Session forNotification = beforeInvalidNotification(session);
        for (SessionListener listener : this.listeners) {
            listener.onExpiration(forNotification);
        }
    }

    public Date getStartTimestamp(SessionKey key) {
        return lookupRequiredSession(key).getStartTimestamp();
    }

    public Date getLastAccessTime(SessionKey key) {
        return lookupRequiredSession(key).getStartTimestamp();
    }

    public long getTimeout(SessionKey key) throws InvalidSessionException {
        return lookupRequiredSession(key).getTimeout();
    }

    public void setTimeout(SessionKey key, long maxIdleTimeInMillis) throws InvalidSessionException {
        Session s = lookupRequiredSession(key);
        s.setTimeout(maxIdleTimeInMillis);
        onChange(s);
    }

    public void touch(SessionKey key) throws InvalidSessionException {
        Session s = lookupRequiredSession(key);
        s.touch();
        onChange(s);
    }

    public String getHost(SessionKey key) {
        return lookupRequiredSession(key).getHost();
    }

    public Collection<Object> getAttributeKeys(SessionKey key) {
        Collection<Object> c = lookupRequiredSession(key).getAttributeKeys();
        if (!CollectionUtils.isEmpty(c)) {
            return Collections.unmodifiableCollection(c);
        }
        return Collections.EMPTY_SET;
    }

    public Object getAttribute(SessionKey sessionKey, Object objectKey) throws InvalidSessionException {
        return lookupRequiredSession(sessionKey).getAttribute(objectKey);
    }

    public void setAttribute(SessionKey sessionKey, Object objectKey, Object value) throws InvalidSessionException {

    }

    protected void onChange(Session s) {
        // do nothing
    }
}
