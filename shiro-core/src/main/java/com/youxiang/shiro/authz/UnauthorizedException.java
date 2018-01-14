package com.youxiang.shiro.authz;

/**
 * Author: Rivers
 * Date: 2018/1/14 21:26
 */
public class UnauthorizedException extends AuthorizationException {

    public UnauthorizedException() {
        super();
    }

    public UnauthorizedException(String message) {
        super(message);
    }

    public UnauthorizedException(Throwable cause) {
        super(cause);
    }

    public UnauthorizedException(String message, Throwable cause) {
        super(message, cause);
    }
}
