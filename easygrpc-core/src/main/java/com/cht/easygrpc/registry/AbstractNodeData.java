package com.cht.easygrpc.registry;

/**
 * @author : chenhaitao934
 * @date : 11:26 下午 2020/10/11
 */
public abstract class AbstractNodeData<T extends AbstractNodeData>{
    private String ip;
    private int port;
    private String nodeType;

    public AbstractNodeData(String ip, int port, String nodeType) {
        this.ip = ip;
        this.port = port;
        this.nodeType = nodeType;
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

    public String getNodeType() {
        return nodeType;
    }

    public void setNodeType(String nodeType) {
        this.nodeType = nodeType;
    }
}
