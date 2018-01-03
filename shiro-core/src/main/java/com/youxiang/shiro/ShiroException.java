package com.youxiang.shiro;

/**
 * Author: RiversLau
 * Date: 2018/1/3 12:30
 */
public class ShiroException extends RuntimeException {

    public ShiroException() {
        super();
    }

    public ShiroException(String message) {
        super(message);
    }

    public ShiroException(Throwable cause) {
        super(cause);
    }

    public ShiroException(String message, Throwable cause) {
        super(message, cause);
    }
}
