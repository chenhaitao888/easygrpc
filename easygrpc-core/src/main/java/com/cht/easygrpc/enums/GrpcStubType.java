package com.cht.easygrpc.enums;

/**
 * @author : chenhaitao934
 */
public enum GrpcStubType {
    BLOCK(0);

    private final int code;

    GrpcStubType(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
