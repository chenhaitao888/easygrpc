package com.cht.easygrpc.domain;

/**
 * @author : chenhaitao934
 * @date : 3:20 下午 2020/10/9
 */
public class MethodAliasInfo {

    private String iface;
    private String method;
    private String aliasIface;
    private String aliasMethod;
    private long mTime;
    private boolean enable;

    public String fullName() {
        return iface + "." + method;
    }

    public String getIface() {
        return iface;
    }

    public void setIface(String iface) {
        this.iface = iface;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getAliasIface() {
        return aliasIface;
    }

    public void setAliasIface(String aliasIface) {
        this.aliasIface = aliasIface;
    }

    public String getAliasMethod() {
        return aliasMethod;
    }

    public void setAliasMethod(String aliasMethod) {
        this.aliasMethod = aliasMethod;
    }

    public long getmTime() {
        return mTime;
    }

    public void setmTime(long mTime) {
        this.mTime = mTime;
    }

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }
}
