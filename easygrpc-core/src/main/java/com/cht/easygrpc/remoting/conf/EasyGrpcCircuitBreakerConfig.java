package com.cht.easygrpc.remoting.conf;

/**
 * @author : chenhaitao934
 */
public class EasyGrpcCircuitBreakerConfig {
    // 熔断阈值
    private int breakerThreshold;
    // 熔断时间窗口
    private int breakerTimeWindow;
    // 熔断统计时间窗口
    private int breakerRateTimeWindow;
    // 熔断错误率
    private Double breakerFailRate;
    // 熔断超时率
    private Double breakerTimeoutRate;

    public int getBreakerThreshold() {
        return breakerThreshold;
    }

    public void setBreakerThreshold(int breakerThreshold) {
        this.breakerThreshold = breakerThreshold;
    }

    public int getBreakerTimeWindow() {
        return breakerTimeWindow;
    }

    public void setBreakerTimeWindow(int breakerTimeWindow) {
        this.breakerTimeWindow = breakerTimeWindow;
    }

    public int getBreakerRateTimeWindow() {
        return breakerRateTimeWindow;
    }

    public void setBreakerRateTimeWindow(int breakerRateTimeWindow) {
        this.breakerRateTimeWindow = breakerRateTimeWindow;
    }

    public Double getBreakerFailRate() {
        return breakerFailRate;
    }

    public void setBreakerFailRate(Double breakerFailRate) {
        this.breakerFailRate = breakerFailRate;
    }

    public Double getBreakerTimeoutRate() {
        return breakerTimeoutRate;
    }

    public void setBreakerTimeoutRate(Double breakerTimeoutRate) {
        this.breakerTimeoutRate = breakerTimeoutRate;
    }
}
