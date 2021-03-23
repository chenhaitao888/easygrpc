package com.cht.easygrpc.registry;

import com.cht.easygrpc.support.builder.Builder;
import com.google.common.collect.Lists;

import java.util.List;

/**
 * @author : chenhaitao934
 */

public class WarmUp {

    private boolean enableWarmUp;
    private int targetWeight;
    private int warmupIntervalSeconds;
    private List<Integer> warmupStages;

    public static final WarmUp defaultWarmUp = Builder.of(WarmUp::new)
            .with(WarmUp::setEnableWarmUp, true)
            .with(WarmUp::setTargetWeight, 100)
            .with(WarmUp::setWarmupIntervalSeconds, 10)
            .with(WarmUp::setWarmupStages, Lists.newArrayList(1, 10, 30, 70, 100))
            .build();

    public boolean isEnableWarmUp() {
        return enableWarmUp;
    }

    public void setEnableWarmUp(boolean enableWarmUp) {
        this.enableWarmUp = enableWarmUp;
    }

    public int getTargetWeight() {
        return targetWeight;
    }

    public void setTargetWeight(int targetWeight) {
        this.targetWeight = targetWeight;
    }

    public int getWarmupIntervalSeconds() {
        return warmupIntervalSeconds;
    }

    public void setWarmupIntervalSeconds(int warmupIntervalSeconds) {
        this.warmupIntervalSeconds = warmupIntervalSeconds;
    }

    public List<Integer> getWarmupStages() {
        return warmupStages;
    }

    public void setWarmupStages(List<Integer> warmupStages) {
        this.warmupStages = warmupStages;
    }
}
