package com.cht.easygrpc.exception;

/**
 * @author : chenhaitao934
 * @date : 2:36 下午 2020/10/9
 */
public class ServiceException extends Exception {

    private int code;

    private String serverStack;

    public ServiceException(String message) {
        super(message);
    }

    public ServiceException(String message, Throwable cause) {
        super(message, cause);
    }

    public ServiceException(Throwable cause) {
        super(cause);
    }

    public ServiceException(int code, String message, String serverStack) {
        super(message);
        this.code = code;
        this.serverStack = serverStack;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getServerStack() {
        return serverStack;
    }

    public void setServerStack(String serverStack) {
        this.serverStack = serverStack;
    }
}
