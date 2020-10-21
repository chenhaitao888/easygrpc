package com.cht.easygrpc.remoting.conf;

import java.util.List;

/**
 * @author : chenhaitao934
 * @date : 2:14 下午 2020/10/9
 */
public class EasyGrpcServerConfig {

    private String serviceName;

    private String ip;

    private int port;

    private int workThreads = 500;

    private int queueCapacity = 1000;

    private Class<?> initializer;

    private String[] servicePackages;

    private String[] serviceImplPackages;


    private List<Class<?>> interfaces;




    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public int getWorkThreads() {
        return workThreads;
    }

    public void setWorkThreads(int workThreads) {
        this.workThreads = workThreads;
    }

    public Class<?> getInitializer() {
        return initializer;
    }

    public void setInitializer(Class<?> initializer) {
        this.initializer = initializer;
    }

    public List<Class<?>> getInterfaces() {
        return interfaces;
    }

    public void setInterfaces(List<Class<?>> interfaces) {
        this.interfaces = interfaces;
    }

    public int getQueueCapacity() {
        return queueCapacity;
    }

    public void setQueueCapacity(int queueCapacity) {
        this.queueCapacity = queueCapacity;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String[] getServicePackages() {
        return servicePackages;
    }

    public void setServicePackages(String[] servicePackages) {
        this.servicePackages = servicePackages;
    }

    public String[] getServiceImplPackages() {
        return serviceImplPackages;
    }

    public void setServiceImplPackages(String[] serviceImplPackages) {
        this.serviceImplPackages = serviceImplPackages;
    }
}
