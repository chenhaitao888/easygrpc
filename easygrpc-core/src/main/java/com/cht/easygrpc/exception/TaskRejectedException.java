package com.cht.easygrpc.exception;

/**
 * @author : chenhaitao934
 * @date : 11:13 下午 2020/6/20
 */
public class TaskRejectedException extends RuntimeException{
    public TaskRejectedException() {
        super();
    }

    public TaskRejectedException(String message) {
        super(message);
    }

    public TaskRejectedException(String message, Throwable cause) {
        super(message, cause);
    }

    public TaskRejectedException(Throwable cause) {
        super(cause);
    }

    protected TaskRejectedException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
