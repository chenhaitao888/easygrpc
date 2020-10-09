package com.cht.easygrpc.exception;

/**
 * @author : chenhaitao934
 * @date : 4:07 下午 2020/10/9
 */
public class StartupException extends RuntimeException{

    public StartupException() {
    }

    public StartupException(Throwable cause) {
        super(cause);
    }

    public StartupException(String message) {
        super(message);
    }

    public StartupException(String message, Throwable cause) {
        super(message, cause);
    }
}
