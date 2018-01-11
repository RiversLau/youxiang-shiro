package com.youxiang.shiro.cache;

import com.youxiang.shiro.ShiroException;

/**
 * Author: RiversLau
 * Date: 2018/1/11 11:48
 */
public class CacheException extends ShiroException {

    public CacheException() {
        super();
    }

    public CacheException(String message) {
        super(message);
    }

    public CacheException(Throwable cause) {
        super(cause);
    }

    public CacheException(String message, Throwable cause) {
        super(message, cause);
    }
}
