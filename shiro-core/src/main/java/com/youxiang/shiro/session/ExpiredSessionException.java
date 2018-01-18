package com.youxiang.shiro.session;

/**
 * Author: RiversLau
 * Date: 2018/1/18 16:06
 */
public class ExpiredSessionException extends StoppedSessionException {

    public ExpiredSessionException() {
        super();
    }

    public ExpiredSessionException(String message) {
        super(message);
    }

    public ExpiredSessionException(Throwable cause) {
        super(cause);
    }

    public ExpiredSessionException(String message, Throwable cause) {
        super(message, cause);
    }
}
