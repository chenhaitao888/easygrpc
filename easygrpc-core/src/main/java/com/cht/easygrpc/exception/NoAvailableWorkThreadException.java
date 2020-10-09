package com.cht.easygrpc.exception;

/**
 * @author : chenhaitao934
 * @date : 11:50 下午 2020/6/22
 */
public class NoAvailableWorkThreadException extends Exception{
    public NoAvailableWorkThreadException() {
        super();
    }

    public NoAvailableWorkThreadException(String message) {
        super(message);
    }

    public NoAvailableWorkThreadException(String message, Throwable cause) {
        super(message, cause);
    }

    public NoAvailableWorkThreadException(Throwable cause) {
        super(cause);
    }

    protected NoAvailableWorkThreadException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
