package com.qw.study.marvenwebsocket.exceptions;

/**
 * @author : qw.r
 * @since : 19-7-13 23:06
 */
public class BusinessExceptions extends RuntimeException {
    public BusinessExceptions() {
    }

    public BusinessExceptions(String message) {
        super(message);
    }

    public BusinessExceptions(String message, Throwable cause) {
        super(message, cause);
    }

    public BusinessExceptions(Throwable cause) {
        super(cause);
    }

    public BusinessExceptions(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
