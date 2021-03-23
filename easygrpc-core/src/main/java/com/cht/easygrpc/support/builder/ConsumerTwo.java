package com.cht.easygrpc.support.builder;

/**
 * @author : chenhaitao934
 */
@FunctionalInterface
public interface ConsumerTwo<T, P1, P2> {
    void accept(T t, P1 p1, P2 p2);
}
