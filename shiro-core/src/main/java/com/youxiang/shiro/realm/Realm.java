package com.youxiang.shiro.realm;

import com.youxiang.shiro.authc.AuthenticationException;
import com.youxiang.shiro.authc.AuthenticationInfo;
import com.youxiang.shiro.authc.AuthenticationToken;

/**
 * Author: RiversLau
 * Date: 2018/1/11 10:55
 */
public interface Realm {

    String getName();

    boolean supports(AuthenticationToken token);

    AuthenticationInfo getAuthenticationInfo(AuthenticationToken token) throws AuthenticationException;
}
