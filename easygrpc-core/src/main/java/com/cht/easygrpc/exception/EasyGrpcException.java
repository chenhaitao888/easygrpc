package com.cht.easygrpc.exception;

/**
 * @author : chenhaitao934
 * @date : 1:47 下午 2020/10/9
 */
public class EasyGrpcException extends RuntimeException{
    public EasyGrpcException() {
    }

    public EasyGrpcException(String message) {
        super(message);
    }

    public EasyGrpcException(String message, Throwable cause) {
        super(message, cause);
    }

    public EasyGrpcException(Throwable cause) {
        super(cause);
    }

    public EasyGrpcException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
