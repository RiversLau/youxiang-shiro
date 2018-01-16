package com.youxiang.shiro.session;

/**
 * Author: Rivers
 * Date: 2018/1/16 21:15
 */
public interface SessionListener {

    void onStart(Session session);

    void onStop(Session session);

    void onExpiration(Session session);
}
