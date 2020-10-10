package com.cht.easygrpc.loadbalance;

import com.cht.easygrpc.helper.CollectionHelper;
import io.grpc.Status;

import javax.annotation.Nullable;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @author : chenhaitao934
 * @date : 4:13 下午 2020/10/10
 */
public class RandomPicker extends AbstractEasyGrpcPicker{

    public RandomPicker(List<SubchannelGroup> list, Status status, String serviceName) {
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
        int sumWeight = getSumWeight(list);
        if (sumWeight <= 0) {
            // 若所有节点权重和为0，则随机选一个
            return list.get(ThreadLocalRandom.current().nextInt(list.size()));
        }
        int index = getRandomSubchannelIndexByWeight(sumWeight, list);
        return list.get(index);
    }

    private int getSumWeight(List<SubchannelGroup> list) {
        return list.stream().mapToInt(SubchannelGroup::getWeight).sum();
    }

    private int getRandomSubchannelIndexByWeight(int sumWeight, List<SubchannelGroup> list) {
        int randomInt = ThreadLocalRandom.current().nextInt(sumWeight);
        int resultIndex = 0;
        int currSum = list.get(0).getWeight();
        for (SubchannelGroup subchannelGroup : list.subList(1, list.size())) {
            if (randomInt >= currSum) {
                resultIndex++;
                currSum += subchannelGroup.getWeight();
                continue;
            }
            return resultIndex;
        }
        return resultIndex;
    }
}
