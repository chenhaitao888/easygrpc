package com.cht.easygrpc.loadbalance;

import com.cht.easygrpc.helper.CollectionHelper;
import io.grpc.Status;

import javax.annotation.Nullable;
import javax.annotation.concurrent.GuardedBy;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * @author : chenhaitao934
 * @date : 4:15 下午 2020/10/10
 */
public class RoundRobinPicker extends AbstractEasyGrpcPicker{
    @GuardedBy("this")
    private int index = 0;

    public RoundRobinPicker(List<SubchannelGroup> list, @Nullable Status status, String serviceName) {
        super(list, status, serviceName);
    }

    @Override
    protected SubchannelGroup pick(List<SubchannelGroup> list) {
        if (CollectionHelper.isEmpty(list)) {
            throw new NoSuchElementException();
        }
        if (list.size() == 1) {
            return list.get(0);
        }

        SubchannelGroup val;
        synchronized (this) {
            val = list.get(index);
            index++;
            if (index >= list.size()) {
                index = 0;
            }
        }
        return val;
    }
}
