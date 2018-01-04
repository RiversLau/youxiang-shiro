package com.youxiang.shiro.session.mgt;

import com.youxiang.shiro.session.Session;
import com.youxiang.shiro.session.SessionException;

/**
 * 管理应用session的创建、获取以及清除
 * Author: RiversLau
 * Date: 2018/1/3 16:19
 */
public interface SessionManager {

    Session start(SessionContext context);

    Session getSession(SessionKey key) throws SessionException;
}
