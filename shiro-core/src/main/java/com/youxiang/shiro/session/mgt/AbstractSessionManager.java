package com.youxiang.shiro.session.mgt;

/**
 * Author: Rivers
 * Date: 2018/1/16 21:07
 */
public abstract class AbstractSessionManager implements SessionManager {

    protected static final long MILLIS_PER_SECOND = 1000;
    protected static final long MILLIS_PER_MINUTE = 60 * MILLIS_PER_SECOND;
    protected static final long MILLIS_PER_HOUR = 60 * MILLIS_PER_MINUTE;

    public static final long DEFAULT_GLOBAL_SESSION_TIMEOUT = 30 * MILLIS_PER_HOUR;

    private long globalSessionTimeout = DEFAULT_GLOBAL_SESSION_TIMEOUT;

    public AbstractSessionManager() {
    }

    public long getGlobalSessionTimeout() {
        return globalSessionTimeout;
    }

    public void setGlobalSessionTimeout(long globalSessionTimeout) {
        this.globalSessionTimeout = globalSessionTimeout;
    }
}
