package com.youxiang.shiro.authc;

import com.youxiang.shiro.subject.PrincipalCollection;

/**
 * Author: RiversLau
 * Date: 2018/1/12 11:24
 */
public interface AuthenticationListener {

    void onSuccess(AuthenticationToken token, AuthenticationInfo info);

    void onFailure(AuthenticationToken token, AuthenticationException ae);

    void onLogout(PrincipalCollection principals);
}
