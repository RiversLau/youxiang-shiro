package com.youxiang.shiro.authc;

import com.youxiang.shiro.util.ByteSource;

/**
 * Author: RiversLau
 * Date: 2018/1/12 14:06
 */
public interface SaltedAuthenticationInfo extends AuthenticationInfo {

    ByteSource getCredentialsSalt();
}
