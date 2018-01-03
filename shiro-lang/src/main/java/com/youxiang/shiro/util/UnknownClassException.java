package com.youxiang.shiro.util;

import com.youxiang.shiro.ShiroException;

/**
 * Author: RiversLau
 * Date: 2018/1/3 12:29
 */
public class UnknownClassException extends ShiroException {

    public UnknownClassException() {
        super();
    }

    public UnknownClassException(String message) {
        super(message);
    }

    public UnknownClassException(Throwable cause) {
        super(cause);
    }

    public UnknownClassException(String message, Throwable cause) {
        super(message, cause);
    }
}
