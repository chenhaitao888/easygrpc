package com.cht.easygrpc.loadbalance;

import com.cht.easygrpc.helper.CollectionHelper;
import io.grpc.LoadBalancer;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @author : chenhaitao934
 */
public class EasyGrpcRandomPicker extends AbstractReadyPicker{

    public EasyGrpcRandomPicker(List<LoadBalancer.Subchannel> activeList) {
        super(activeList);
    }

    @Override
    protected EasyGrpcSubchannel pick(List<EasyGrpcSubchannel> list) {
        if (CollectionHelper.isEmpty(list)) {
            return null;
        }
        if (list.size() == 1) {
            return list.get(0);
        }
        int index = getRandomIndexByWeight(list);
        return list.get(index);
    }

    private int getRandomIndexByWeight(List<EasyGrpcSubchannel> list) {
        final int sumWeight = list.stream().mapToInt(EasyGrpcSubchannel::getWeight).sum();

        // 若权重<=0，则不区分权重，随机一个节点
        if (sumWeight <= 0) {
            return ThreadLocalRandom.current().nextInt(list.size());
        }

        // 只取一次，避免重复取随机值，一直变化无法命中，同时避免linux随机池耗尽，影响性能
        int randomInt = ThreadLocalRandom.current().nextInt(sumWeight);

        /*
         * 将权重分成N段，比如 A[0,100] B[101,200] C[201,300]...
         * 判断随机值是否落入第I段，则表示命中I，第I段落权重范围 [ Sum(I-1), Sum(I) )
         * 按顺序取，把I之前未命中的排除掉，可以简化为 随机值 < Sum(I), 则命中当前段落
         */
        int sumI = 0;
        for (int i = 0; i < list.size(); i++) {
            // 计算Sum(I) = Sum(I-1) + I
            sumI += list.get(i).getWeight();
            if (randomInt < sumI) {
                return i;
            }
        }
        // 取最后一个
        return list.size() - 1;
    }


}
