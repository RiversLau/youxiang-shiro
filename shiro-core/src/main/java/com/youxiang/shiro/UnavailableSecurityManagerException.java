package com.youxiang.shiro;

/**
 * Author: RiversLau
 * Date: 2018/1/5 10:35
 */
public class UnavailableSecurityManagerException extends ShiroException {

    public UnavailableSecurityManagerException(String message) {
        super(message);
    }

    public UnavailableSecurityManagerException(String message, Throwable cause) {
        super(message, cause);
    }
}
