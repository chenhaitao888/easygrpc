package com.cht.easygrpc.domain;

/**
 * @author : chenhaitao934
 */
public class CircuitBreakerInfo {
    private String clientName;
    private String mockResult;
    private String iface;
    private String method;
    private boolean openCircuitBreaker;

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public String getMockResult() {
        return mockResult;
    }

    public void setMockResult(String mockResult) {
        this.mockResult = mockResult;
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

    public boolean isOpenCircuitBreaker() {
        return openCircuitBreaker;
    }

    public void setOpenCircuitBreaker(boolean openCircuitBreaker) {
        this.openCircuitBreaker = openCircuitBreaker;
    }

    public String connectKey(){
        return iface + "." + method;
    }


}
