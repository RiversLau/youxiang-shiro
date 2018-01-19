package com.youxiang.shiro.session.mgt;

import com.youxiang.shiro.session.Session;

/**
 * Author: RiversLau
 * Date: 2018/1/19 11:25
 */
public class SimpleSessionFactory implements SessionFactory {

    public Session createSession(SessionContext initData) {
        if (initData != null) {
            String host = initData.getHost();
            if (host != null) {
                return new SimpleSession(host);
            }
        }
        return new SimpleSession();
    }
}
