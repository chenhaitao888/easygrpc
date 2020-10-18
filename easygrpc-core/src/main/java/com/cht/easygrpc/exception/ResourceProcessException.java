package com.cht.easygrpc.exception;

/**
 * @author : chenhaitao934
 */
public class ResourceProcessException extends RuntimeException {
    public ResourceProcessException() {
    }

    public ResourceProcessException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public ResourceProcessException(String message, Throwable cause) {
        super(message, cause);
    }

    public ResourceProcessException(String message) {
        super(message);
    }

    public ResourceProcessException(Throwable cause) {
        super(cause);
    }
}
