package com.youxiang.shiro.authc;

/**
 * Authenticator负责认证应用中的账户，它是Shiro API的主要入口之一
 * Author: RiversLau
 * Date: 2018/1/3 16:17
 */
public interface Authenticator {

    AuthenticationInfo authenticate(AuthenticationToken authenticationToken)
            throws AuthenticationException;
}
