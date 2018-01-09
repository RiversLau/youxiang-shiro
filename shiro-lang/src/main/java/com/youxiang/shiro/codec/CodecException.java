package com.youxiang.shiro.codec;

import com.youxiang.shiro.ShiroException;

/**
 * Author: RiversLau
 * Date: 2018/1/9 16:19
 */
public class CodecException extends ShiroException {

    public CodecException() {
        super();
    }

    public CodecException(String message) {
        super(message);
    }

    public CodecException(Throwable cause) {
        super(cause);
    }

    public CodecException(String message, Throwable cause) {
        super(message, cause);
    }
}
