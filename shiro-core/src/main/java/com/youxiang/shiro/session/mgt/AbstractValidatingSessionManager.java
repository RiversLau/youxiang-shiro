package com.youxiang.shiro.session.mgt;

import com.youxiang.shiro.authz.AuthorizationException;
import com.youxiang.shiro.session.ExpiredSessionException;
import com.youxiang.shiro.session.InvalidSessionException;
import com.youxiang.shiro.session.Session;
import com.youxiang.shiro.session.UnknownSessionException;
import com.youxiang.shiro.util.Destroyable;
import com.youxiang.shiro.util.LifecycleUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;

/**
 * Author: RiversLau
 * Date: 2018/1/18 15:44
 */
public abstract class AbstractValidatingSessionManager extends AbstractNativeSessionManager
        implements ValidatingSessionManager, Destroyable {

    private static final Logger logger = LoggerFactory.getLogger(AbstractValidatingSessionManager.class);

    public static final long DEFAULT_SESSION_VALIDATION_INTERVAL = MILLIS_PER_HOUR;

    private boolean sessionValidationSchedulerEnabled;

    protected SessionValidationScheduler sessionValidationScheduler;

    protected long sessionValidationInterval;

    public AbstractValidatingSessionManager() {
        this.sessionValidationSchedulerEnabled = true;
        this.sessionValidationInterval = DEFAULT_SESSION_VALIDATION_INTERVAL;
    }

    public boolean isSessionValidationSchedulerEnabled() {
        return sessionValidationSchedulerEnabled;
    }

    public void setSessionValidationSchedulerEnabled(boolean sessionValidationSchedulerEnabled) {
        this.sessionValidationSchedulerEnabled = sessionValidationSchedulerEnabled;
    }

    public void setSessionValidationScheduler(SessionValidationScheduler sessionValidationScheduler) {
        this.sessionValidationScheduler = sessionValidationScheduler;
    }

    public SessionValidationScheduler getSessionValidationScheduler() {
        return sessionValidationScheduler;
    }

    private void enableSessionValidationIfNecessary() {
        SessionValidationScheduler scheduler = getSessionValidationScheduler();
        if (isSessionValidationSchedulerEnabled() && (scheduler == null || !scheduler.isEnabled())) {
            enableSessionValidation();
        }
    }

    public void setSessionValidationInterval(long sessionValidationInterval) {
        this.sessionValidationInterval = sessionValidationInterval;
    }

    public long getSessionValidationInterval() {
        return sessionValidationInterval;
    }

    @Override
    protected final Session doGetSession(SessionKey key) throws InvalidSessionException {
        enableSessionValidationIfNecessary();

        logger.trace("Attempting to retrieve session with key {}.", key);

        Session s = retrieveSession(key);
        if (s != null) {
            validate(s, key);
        }
         return s;
    }

    protected abstract Session retrieveSession(SessionKey key) throws UnknownSessionException;

    protected Session createSession(SessionContext context) throws AuthorizationException {
        enableSessionValidationIfNecessary();
        return doGetSession(context);
    }

    protected abstract Session doGetSession(SessionContext context) throws AuthorizationException;

    protected void validate(Session session, SessionKey key) {
        try {
            doValidate(session);
        } catch (ExpiredSessionException ese) {
            onExpiration(session, ese, key);
            throw ese;
        } catch (InvalidSessionException ise) {
            onInvalidation(session, ise, key);
            throw ise;
        }
    }

    protected void onExpiration(Session s, ExpiredSessionException ese, SessionKey key) {
        logger.trace("Session with id [{}] has expired.", s.getId());
        try {
            onExpiration(s);
            notifyExpiration(s);
        } finally {
            afterExpired(s);
        }
    }

    protected void onExpiration(Session session) {
        onChange(session);
    }

    protected void afterExpired(Session session) {
        // do nothing
    }

    protected void onInvalidation(Session session, InvalidSessionException ise, SessionKey key) {
        if (ise instanceof ExpiredSessionException) {
            onExpiration(session, (ExpiredSessionException) ise, key);
            return;
        }
        logger.trace("Session with id [{}] is invalid.", session.getId());
        try {
            onStop(session);
            notifyStop(session);
        } finally {
            afterStopped(session);
        }
    }

    protected void doValidate(Session session) throws InvalidSessionException {
        if (session instanceof ValidatingSession) {
            ((ValidatingSession) session).validate();
        } else {
            String msg = "The " + getClass().getName() + " implementation only supports validating " +
                    "Session implementations of the " + ValidatingSession.class.getName() + " interface.  " +
                    "Please either implement this interface in your session implementation or override the " +
                    AbstractValidatingSessionManager.class.getName() + ".doValidate(Session) method to perform validation.";
            throw new IllegalStateException(msg);
        }
    }

    protected long getTimeout(Session session) {
        return session.getTimeout();
    }

    protected SessionValidationScheduler createSessionValidationScheduler() {
        ExecutorServiceSessionValidationScheduler scheduler;

        if (logger.isDebugEnabled()) {
            logger.debug("No sessionValidationScheduler set. Attempting to create default instance.");
        }

        scheduler = new ExecutorServiceSessionValidationScheduler(this);
        scheduler.setInterval(getSessionValidationInterval());
        if (logger.isTraceEnabled()) {
            logger.trace("Created default SessionValidationScheduler instance of type [" + scheduler.getClass().getName() + "].");
        }
        return scheduler;
    }

    protected synchronized void enableSessionValidation() {
        SessionValidationScheduler scheduler = getSessionValidationScheduler();
        if (scheduler == null) {
            scheduler = createSessionValidationScheduler();
            setSessionValidationScheduler(scheduler);
        }

        if (!scheduler.isEnabled()) {
            if (logger.isInfoEnabled()) {
                logger.info("Enabling session validation scheduler...");
            }
            scheduler.enableSessionValidation();
            afterSessionValidationEnabled();
        }
    }

    protected void afterSessionValidationEnabled() {
        // do nothing
    }

    protected synchronized void disableSessionValidation() {
        beforeSessionValidationDisabled();
        SessionValidationScheduler scheduler = getSessionValidationScheduler();
        if (scheduler != null) {
            try {
                scheduler.disableSessionValidation();
                if (logger.isInfoEnabled()) {
                    logger.info("Diabled session validation scheduler.");
                }
            } catch (Exception e) {
                if (logger.isDebugEnabled()) {
                    String msg = "Unable to disable SessionValidationScheduler.  Ignoring (shutting down)...";
                    logger.debug(msg, e);
                }
                LifecycleUtils.destroy(scheduler);
                setSessionValidationScheduler(null);
            }
        }
    }

    protected void beforeSessionValidationDisabled() {

    }

    public void destroy() throws Exception {
        disableSessionValidation();
    }

    public void validateSessions() {
        if (logger.isInfoEnabled()) {
            logger.info("Validating all active sessions...");
        }
        int invalidCount = 0;
        Collection<Session> activeSessions = getActiveSessions();
        if (activeSessions != null && !activeSessions.isEmpty()) {
            for (Session s : activeSessions) {
                try {
                    SessionKey key = new DefaultSessionKey(s.getId());
                    validate(s, key);
                } catch (InvalidSessionException ise) {
                    if (logger.isDebugEnabled()) {
                        boolean expired = (ise instanceof ExpiredSessionException);
                        String msg = "Invalidated session with id [" + s.getId() + "]" +
                                (expired ? " (expired)" : " (stopped)");
                        logger.debug(msg);
                    }
                    invalidCount++;
                }
            }
        }

        if (logger.isInfoEnabled()) {
            String msg = "Finished session validation.";
            if (invalidCount > 0) {
                msg += "  [" + invalidCount + "] sessions were stopped.";
            } else {
                msg += "  No sessions were stopped.";
            }
            logger.info(msg);
        }
    }

    protected abstract Collection<Session> getActiveSessions();
}
