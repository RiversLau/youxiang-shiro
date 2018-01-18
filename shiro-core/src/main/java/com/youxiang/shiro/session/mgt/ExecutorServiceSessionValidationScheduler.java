package com.youxiang.shiro.session.mgt;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ScheduledExecutorService;

/**
 * Author: RiversLau
 * Date: 2018/1/18 16:27
 */
public class ExecutorServiceSessionValidationScheduler implements SessionValidationScheduler, Runnable {

    private static final Logger logger = LoggerFactory.getLogger(ExecutorServiceSessionValidationScheduler.class);

    ValidatingSessionManager sessionManager;
    private ScheduledExecutorService service;

}
