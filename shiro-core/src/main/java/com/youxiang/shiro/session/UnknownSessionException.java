package com.youxiang.shiro.session;

/**
 * Author: Rivers
 * Date: 2018/1/17 21:53
 */
public class UnknownSessionException extends InvalidSessionException {

    public UnknownSessionException() {

    }

    public UnknownSessionException(String message) {
        super(message);
    }

    public UnknownSessionException(Throwable cause) {
        super(cause);
    }

    public UnknownSessionException(String message, Throwable cause) {
        super(message, cause);
    }
}
