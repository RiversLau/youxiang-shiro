package com.youxiang.shiro.session.mgt;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Author: RiversLau
 * Date: 2018/1/18 16:27
 */
public class ExecutorServiceSessionValidationScheduler implements SessionValidationScheduler, Runnable {

    private static final Logger logger = LoggerFactory.getLogger(ExecutorServiceSessionValidationScheduler.class);

    ValidatingSessionManager sessionManager;
    private ScheduledExecutorService service;
    private long interval = DefaultSessionManager.DEFAULT_SESSION_VALIDATION_INTERVAL;
    private boolean enabled = false;
    private String threadNamePrefix = "SessionValidationThread-";

    public ExecutorServiceSessionValidationScheduler() {
        super();
    }

    public ExecutorServiceSessionValidationScheduler(ValidatingSessionManager sessionManager) {
        this.sessionManager = sessionManager;
    }

    public ValidatingSessionManager getSessionManager() {
        return sessionManager;
    }

    public void setSessionManager(ValidatingSessionManager sessionManager) {
        this.sessionManager = sessionManager;
    }

    public long getInterval() {
        return interval;
    }

    public void setInterval(long interval) {
        this.interval = interval;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setThreadNamePrefix(String threadNamePrefix) {
        this.threadNamePrefix = threadNamePrefix;
    }

    public String getThreadNamePrefix() {
        return threadNamePrefix;
    }

    public void enableSessionValidation() {
        if (this.interval > 0l) {
            this.service = Executors.newSingleThreadScheduledExecutor(new ThreadFactory() {
                private final AtomicInteger count = new AtomicInteger(1);
                public Thread newThread(Runnable r) {
                    Thread thread = new Thread(r);
                    thread.setDaemon(true);
                    thread.setName(threadNamePrefix + count.getAndIncrement());
                    return thread;
                }
            });
            this.service.scheduleAtFixedRate(this, interval, interval, TimeUnit.MICROSECONDS);
        }
        this.enabled = true;
    }

    public void run() {
        if (logger.isDebugEnabled()) {
            logger.debug("Executing session validation...");
        }
        long startTime = System.currentTimeMillis();
        this.sessionManager.validateSessions();
        long stopTime = System.currentTimeMillis();
        if (logger.isDebugEnabled()) {
            logger.debug("Session validation completed successfully in " + (stopTime - startTime) + " milliseconds");
        }
    }

    public void disableSessionValidation() {
        if (this.service != null) {
            this.service.shutdown();
        }
        this.enabled = false;
    }
}
