package com.youxiang.shiro.session.mgt;

import com.youxiang.shiro.session.InvalidSessionException;
import com.youxiang.shiro.session.StoppedSessionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;

/**
 * Author: RiversLau
 * Date: 2018/1/19 11:42
 */
public class SimpleSession implements ValidatingSession, Serializable {

    private transient static final Logger logger = LoggerFactory.getLogger(SimpleSession.class);

    protected static final long MILLIS_PER_SECOND = 1000;
    protected static final long MILLIS_PER_MINUTE = 60 * MILLIS_PER_SECOND;
    protected static final long MILLIS_PER_HOUR = 60 * MILLIS_PER_MINUTE;

    static int bitIndexCounter = 0;
    private static final int ID_BIT_MASK = 1 << bitIndexCounter++;
    private static final int START_TIMESTAMP_BIT_MASK = 1 << bitIndexCounter++;
    private static final int STOP_TIMESTAMP_BIT_MASK = 1 << bitIndexCounter++;
    private static final int LAST_ACCESS_TIME_BIT_MASK = 1 << bitIndexCounter++;
    private static final int TIMEOUT_BIT_MASK = 1 << bitIndexCounter++;
    private static final int EXPIRED_BIT_MASK = 1 << bitIndexCounter++;
    private static final int HOST_BIT_MASK = 1 << bitIndexCounter++;
    private static final int ATTRIBUTES_BIT_MASK = 1 << bitIndexCounter++;

    private transient Serializable id;
    private transient Date startTimestamp;
    private transient Date stopTimestamp;
    private transient Date lastAccessTime;
    private transient long timeout;
    private transient boolean expired;
    private transient String host;
    private transient Map<String, Object> attributes;

    public SimpleSession() {
        this.timeout = DefaultSessionManager.DEFAULT_GLOBAL_SESSION_TIMEOUT;
        this.startTimestamp = new Date();
        this.lastAccessTime = this.startTimestamp;
    }

    public SimpleSession(String host) {
        super();
        this.host = host;
    }

    public Serializable getId() {
        return id;
    }

    public void setId(Serializable id) {
        this.id = id;
    }

    public Date getStartTimestamp() {
        return startTimestamp;
    }

    public void setStartTimestamp(Date startTimestamp) {
        this.startTimestamp = startTimestamp;
    }

    public Date getStopTimestamp() {
        return stopTimestamp;
    }

    public void setStopTimestamp(Date stopTimestamp) {
        this.stopTimestamp = stopTimestamp;
    }

    public Date getLastAccessTime() {
        return lastAccessTime;
    }

    public void setLastAccessTime(Date lastAccessTime) {
        this.lastAccessTime = lastAccessTime;
    }

    public boolean isExpired() {
        return expired;
    }

    public void setExpired(boolean expired) {
        this.expired = expired;
    }

    public long getTimeout() {
        return timeout;
    }

    public void setTimeout(long timeout) {
        this.timeout = timeout;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public Map<String, Object> getAttributes() {
        return attributes;
    }

    public void setAttributes(Map<String, Object> attributes) {
        this.attributes = attributes;
    }

    public void touch() {
        this.lastAccessTime = new Date();
    }

    public void stop() {
        if (this.stopTimestamp == null) {
            this.stopTimestamp = new Date();
        }
    }

    protected boolean isStopped() {
        return getStopTimestamp() != null;
    }

    protected void expire() {
        stop();
        this.expired = true;
    }

    public boolean isValid() {
        return !isStopped() && !isExpired();
    }

    protected boolean isTimedOut() {
        if (isExpired()) {
            return true;
        }

        long timeout = getTimeout();
        if (timeout > 0l) {
            Date lastAccessTime = getLastAccessTime();
            if (lastAccessTime == null) {
                String msg = "session.lastAccessTime for session with id [" +
                        getId() + "] is null.  This value must be set at " +
                        "least once, preferably at least upon instantiation.  Please check the " +
                        getClass().getName() + " implementation and ensure " +
                        "this value will be set (perhaps in the constructor?)";
                throw new IllegalStateException(msg);
            }

            long expireTimeMillis = System.currentTimeMillis() - timeout;
            Date expireTime = new Date(expireTimeMillis);
            return lastAccessTime.before(expireTime);
        } else {
            if (logger.isTraceEnabled()) {
                logger.trace("No timeout for session with id [" + getId() +
                        "].  Session is not considered expired.");
            }
        }
        return false;
    }

    public void validate() throws InvalidSessionException {
        if (isStopped()) {
            String msg = "Session with id [" + getId() + "] has been " +
                    "explicitly stopped.  No further interaction under this session is " +
                    "allowed.";
            throw new StoppedSessionException(msg);
        }
        if (isTimedOut()) {


        }
    }
}
