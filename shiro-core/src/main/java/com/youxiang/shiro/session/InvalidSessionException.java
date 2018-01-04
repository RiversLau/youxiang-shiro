package com.youxiang.shiro.session;

/**
 * Author: RiversLau
 * Date: 2018/1/4 12:33
 */
public class InvalidSessionException extends SessionException {

    public InvalidSessionException() {
        super();
    }

    public InvalidSessionException(String message) {
        super(message);
    }

    public InvalidSessionException(Throwable cause) {
        super(cause);
    }

    public InvalidSessionException(String message, Throwable cause) {
        super(message, cause);
    }
}
