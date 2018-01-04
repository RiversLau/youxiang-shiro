package com.youxiang.shiro.authz;

import com.youxiang.shiro.ShiroException;

/**
 * Author: RiversLau
 * Date: 2018/1/4 11:55
 */
public class AuthorizationException extends ShiroException {

    public AuthorizationException() {
        super();
    }

    public AuthorizationException(String message) {
        super(message);
    }

    public AuthorizationException(Throwable cause) {
        super(cause);
    }

    public AuthorizationException(String message, Throwable cause) {
        super(message, cause);
    }
}
