package com.youxiang.shiro.session.mgt;

import com.youxiang.shiro.session.InvalidSessionException;
import com.youxiang.shiro.session.Session;

/**
 * Author: RiversLau
 * Date: 2018/1/18 16:22
 */
public interface ValidatingSession extends Session {

    boolean isValid();

    void validate() throws InvalidSessionException;
}
