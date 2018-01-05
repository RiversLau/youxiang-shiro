package com.youxiang.shiro.authc;

import com.youxiang.shiro.subject.PrincipalCollection;

import java.io.Serializable;

/**
 * Author: RiversLau
 * Date: 2018/1/3 17:56
 */
public interface AuthenticationInfo extends Serializable {

    PrincipalCollection getPrincipals();

    Object getCredentials();
}
