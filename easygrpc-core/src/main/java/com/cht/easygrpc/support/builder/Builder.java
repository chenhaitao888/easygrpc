package com.cht.easygrpc.support.builder;

import com.cht.easygrpc.registry.WarmUp;
import com.google.common.collect.Lists;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * @author : chenhaitao934
 */
public class Builder<T> {
    private final Supplier<T> instance;

    private List<Consumer<T>> modifiers = new ArrayList<>();

    public Builder(Supplier<T> instance) {
        this.instance = instance;
    }

    public static <T> Builder<T> of(Supplier<T> instance){
        return new Builder<>(instance);
    }

    public <P1> Builder<T> with(ConsumerOne<T, P1> consumer, P1 p1){
        Consumer<T> c = instance -> consumer.accept(instance, p1);
        modifiers.add(c);
        return this;
    }


    public <P1, P2> Builder<T> with(ConsumerTwo<T, P1, P2> consumer, P1 p1, P2 p2){
        Consumer<T> c = instance -> consumer.accept(instance, p1, p2);
        modifiers.add(c);
        return this;
    }

    public T build(){
        T v = instance.get();
        modifiers.forEach(e -> e.accept(v));
        modifiers.clear();
        return v;
    }

}
