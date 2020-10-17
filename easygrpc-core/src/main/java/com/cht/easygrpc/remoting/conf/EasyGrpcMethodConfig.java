package com.cht.easygrpc.remoting.conf;

/**
 * @author : chenhaitao934
 */
public class EasyGrpcMethodConfig {
    private String iface;
    private String method;
    private int timeoutInMillis;

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

    public int getTimeoutInMillis() {
        return timeoutInMillis;
    }

    public void setTimeoutInMillis(int timeoutInMillis) {
        this.timeoutInMillis = timeoutInMillis;
    }

    public static EasyGrpcMethodConfig newInstance(String iface, String method, EasyGrpcClientConfig clientConfig) {
        EasyGrpcMethodConfig methodConfig = new EasyGrpcMethodConfig();
        methodConfig.setIface(iface);
        methodConfig.setMethod(method);
        methodConfig.setTimeoutInMillis(clientConfig.getTimeoutInMillis());
        return methodConfig;
    }
}
