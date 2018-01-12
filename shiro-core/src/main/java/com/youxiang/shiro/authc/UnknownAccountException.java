package com.youxiang.shiro.authc;

/**
 * Author: RiversLau
 * Date: 2018/1/12 17:08
 */
public class UnknownAccountException extends AccountException {

    public UnknownAccountException() {
        super();
    }

    public UnknownAccountException(String message) {
        super(message);
    }

    public UnknownAccountException(Throwable cause) {
        super(cause);
    }

    public UnknownAccountException(String message, Throwable cause) {
        super(message, cause);
    }
}
