package com.cht.easygrpc.loadbalance;

import com.cht.easygrpc.helper.CollectionHelper;
import io.grpc.LoadBalancer;

import java.util.List;
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;

/**
 * @author : chenhaitao934
 */
public class EasyGrpcRoundRobinPicker extends AbstractReadyPicker {

    private volatile int index = 0;

    private static final AtomicIntegerFieldUpdater<EasyGrpcRoundRobinPicker> INDEX_UPDATER =
            AtomicIntegerFieldUpdater.newUpdater(EasyGrpcRoundRobinPicker.class, "index");

    EasyGrpcRoundRobinPicker(List<LoadBalancer.Subchannel> list) {
        super(list);
    }

    @Override
    protected EasyGrpcSubchannel pick(List<EasyGrpcSubchannel> list) {
        if (CollectionHelper.isEmpty(list)) {
            return null;
        }
        final int size = list.size();
        if (size == 1) {
            return list.get(0);
        }

        int i = INDEX_UPDATER.incrementAndGet(this);
        if (i >= size) {
            int oldi = i;
            i %= size;
            // 能重置成[0,size]之间的数最好，不能重置也无所谓
            INDEX_UPDATER.compareAndSet(this, oldi, i);
        }
        return list.get(i);
    }
}
