package com.cht.easygrpc.exception;

/**
 * @author : chenhaitao934
 * @date : 11:41 上午 2020/10/12
 */
public class RegistryException extends RuntimeException{

    public RegistryException() {
    }

    public RegistryException(String message) {
        super(message);
    }

    public RegistryException(String message, Throwable cause) {
        super(message, cause);
    }

    public RegistryException(Throwable cause) {
        super(cause);
    }

    public RegistryException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
