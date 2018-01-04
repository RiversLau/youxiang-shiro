package com.youxiang.shiro.subject;

import com.youxiang.shiro.ShiroException;

/**
 * Author: RiversLau
 * Date: 2018/1/4 17:28
 */
public class ExecutionException extends ShiroException {

    public ExecutionException(String message) {
        super(message);
    }

    public ExecutionException(String message, Throwable cause) {
        super(message, cause);
    }
}
