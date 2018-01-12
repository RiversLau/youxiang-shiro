package com.youxiang.shiro.authc.pam;

import com.youxiang.shiro.authc.AuthenticationException;

/**
 * Author: RiversLau
 * Date: 2018/1/12 16:58
 */
public class UnsupportedTokenException extends AuthenticationException {

    public UnsupportedTokenException() {
        super();
    }

    public UnsupportedTokenException(String message) {
        super(message);
    }

    public UnsupportedTokenException(Throwable cause) {
        super(cause);
    }

    public UnsupportedTokenException(String message, Throwable cause) {
        super(message, cause);
    }
}
