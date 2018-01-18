package com.youxiang.shiro.session.mgt;

/**
 * Author: RiversLau
 * Date: 2018/1/18 15:47
 */
public interface SessionValidationScheduler {

    boolean isEnabled();

    void enableSessionValidation();

    void disableSessionValidation();
}
