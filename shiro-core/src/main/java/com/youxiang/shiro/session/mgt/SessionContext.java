package com.youxiang.shiro.session.mgt;

import java.io.Serializable;
import java.util.Map;

/**
 * Author: RiversLau
 * Date: 2018/1/4 14:56
 */
public interface SessionContext extends Map<String, Object> {

    void setHost(String host);

    String getHost();

    Serializable getSessionId();

    void setSessionId(Serializable sessionId);
}
