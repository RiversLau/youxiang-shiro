package com.youxiang.shiro.session.mgt;

/**
 * Author: Rivers
 * Date: 2018/1/15 22:05
 */
public interface ValidatingSessionManager extends SessionManager {

    void validateSessions();
}
