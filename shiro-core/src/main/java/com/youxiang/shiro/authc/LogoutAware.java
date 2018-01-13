package com.youxiang.shiro.authc;

import com.youxiang.shiro.subject.PrincipalCollection;

/**
 * Author: RiversLau
 * Date: 2018/1/12 11:21
 */
public interface LogoutAware {
    void onLogout(PrincipalCollection principals);
}
