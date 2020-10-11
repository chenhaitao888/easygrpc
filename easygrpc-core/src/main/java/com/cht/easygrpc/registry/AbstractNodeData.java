package com.cht.easygrpc.registry;

/**
 * @author : chenhaitao934
 * @date : 11:26 下午 2020/10/11
 */
public abstract class AbstractNodeData<T extends AbstractNodeData>{
    private String ip;
    private int port;

    public AbstractNodeData(String ip, int port) {
        this.ip = ip;
        this.port = port;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }
}
