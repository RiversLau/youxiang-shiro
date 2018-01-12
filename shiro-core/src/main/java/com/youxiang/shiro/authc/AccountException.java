package com.youxiang.shiro.authc;

/**
 * Author: RiversLau
 * Date: 2018/1/12 17:09
 */
public class AccountException extends AuthenticationException {
    public AccountException() {
        super();
    }

    public AccountException(String message) {
        super(message);
    }

    public AccountException(Throwable cause) {
        super(cause);
    }

    public AccountException(String message, Throwable cause) {
        super(message, cause);
    }
}
