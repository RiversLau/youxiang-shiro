package com.youxiang.shiro.authc;

/**
 * Author: RiversLau
 * Date: 2018/1/5 14:18
 */
public interface HostAuthenticationToken extends AuthenticationToken {

    String getHost();
}
