package com.cht.easygrpc.exception;

/**
 * @author : chenhaitao934
 * @date : 2:07 下午 2020/10/9
 */
public class RemotingException extends Exception{

    public RemotingException(String message) {
        super(message);
    }

    public RemotingException(String message, Throwable cause) {
        super(message, cause);
    }
}
