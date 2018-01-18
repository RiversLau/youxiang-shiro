package com.youxiang.shiro.session;

/**
 * Author: RiversLau
 * Date: 2018/1/18 16:04
 */
public class StoppedSessionException extends InvalidSessionException {

    public StoppedSessionException() {
        super();
    }

    public StoppedSessionException(String message) {
        super(message);
    }

    public StoppedSessionException(Throwable cause) {
        super(cause);
    }

    public StoppedSessionException(String message, Throwable cause) {
        super(message, cause);
    }
}
