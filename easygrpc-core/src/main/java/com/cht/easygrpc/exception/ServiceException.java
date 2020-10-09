package com.cht.easygrpc.exception;

/**
 * @author : chenhaitao934
 * @date : 2:36 下午 2020/10/9
 */
public class ServiceException extends Exception{

    public ServiceException(String message) {
        super(message);
    }

    public ServiceException(String message, Throwable cause) {
        super(message, cause);
    }

    public ServiceException(Throwable cause) {
        super(cause);
    }
}
