package com.youxiang.shiro.mgt;

import com.youxiang.shiro.authc.Authenticator;
import com.youxiang.shiro.authz.Authorizer;
import com.youxiang.shiro.session.mgt.SessionManager;

/**
 * Author: RiversLau
 * Date: 2018/1/3 16:07
 */
public interface SecurityManager extends Authenticator, Authorizer, SessionManager {

}
