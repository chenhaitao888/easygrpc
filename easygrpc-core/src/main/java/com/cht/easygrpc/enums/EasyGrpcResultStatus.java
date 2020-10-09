package com.cht.easygrpc.enums;

import io.grpc.StatusRuntimeException;

import java.util.NoSuchElementException;
import java.util.concurrent.TimeoutException;

/**
 * @author : chenhaitao934
 * @date : 3:41 下午 2020/10/9
 */
public enum EasyGrpcResultStatus {

    SUCCESS(200),
    NO_SERVER_NODES(401),
    TIMEOUT(402),
    PARSE_ARGS_FAILED(504),
    INVALID_REQUEST(505),

    ERROR(500);

    private int code;

    EasyGrpcResultStatus(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public static boolean isTimeoutException(Throwable e) {
        if (e instanceof TimeoutException) {
            return true;
        }
        if (e.getCause() == null) {
            return false;
        }
        Throwable cause = e.getCause();
        if ((cause instanceof StatusRuntimeException && cause.getMessage().startsWith("DEADLINE_EXCEEDED"))
                || (cause instanceof TimeoutException)) {
            return true;
        }
        return false;
    }

    public static boolean isNoServerNodesException(Throwable e) {
        return e.getCause() != null && e.getCause() instanceof NoSuchElementException;
    }
}
