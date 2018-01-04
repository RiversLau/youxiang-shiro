package com.youxiang.shiro;

import com.youxiang.shiro.subject.Subject;
import com.youxiang.shiro.util.ThreadContext;

/**
 * Author: RiversLau
 * Date: 2018/1/4 17:54
 */
public abstract class SecurityUtils {

    private static SecurityManager securityManager;

    public static Subject getSubject() {
        Subject subject = ThreadContext.getSubject();
    }
}
