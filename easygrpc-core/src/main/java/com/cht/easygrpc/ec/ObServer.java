package com.cht.easygrpc.ec;

/**
 * @author : chenhaitao934
 */
@FunctionalInterface
public interface ObServer {
    void onObserved(EventInfo eventInfo);
}
