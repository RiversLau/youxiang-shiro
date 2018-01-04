package com.youxiang.shiro.session;

import com.youxiang.shiro.ShiroException;

/**
 * Author: RiversLau
 * Date: 2018/1/4 12:34
 */
public class SessionException extends ShiroException {

    public SessionException() {
        super();
    }

    public SessionException(String message) {
        super(message);
    }

    public SessionException(Throwable cause) {
        super(cause);
    }

    public SessionException(String message, Throwable cause) {
        super(message, cause);
    }
}
