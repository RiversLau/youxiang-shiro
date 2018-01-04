package com.youxiang.shiro.session.mgt;

import java.io.Serializable;

/**
 * Author: RiversLau
 * Date: 2018/1/4 14:47
 */
public interface SessionKey {

    /**
     * 获取session id
     * 当使用Shiro自身管理的session时，通过session id 来获取session是非常适合的方式
     * @return
     */
    Serializable getSessionId();
}
