package com.youxiang.shiro.authc;

/**
 * Author: RiversLau
 * Date: 2018/1/12 14:07
 */
public interface MergableAuthenticationInfo extends AuthenticationInfo {
    void merge(AuthenticationInfo info);
}
