package com.youxiang.shiro.session.mgt;

import com.youxiang.shiro.session.Session;

/**
 * Author: RiversLau
 * Date: 2018/1/19 11:13
 */
public interface SessionFactory {

    Session createSession(SessionContext context);
}
