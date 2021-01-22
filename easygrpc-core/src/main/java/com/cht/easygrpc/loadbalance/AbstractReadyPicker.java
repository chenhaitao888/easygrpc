package com.cht.easygrpc.loadbalance;

import com.cht.easygrpc.constant.EasyGrpcOption;
import com.cht.easygrpc.helper.StringHelper;
import com.google.common.collect.Lists;
import io.grpc.ConnectivityState;
import io.grpc.LoadBalancer;
import io.grpc.Status;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static com.cht.easygrpc.constant.EasyGrpcOption.REGION_KEY;
import static com.cht.easygrpc.constant.EasyGrpcOption.TAG_KEY;

/**
 * @author : chenhaitao934
 */
public abstract class AbstractReadyPicker extends AbstractEasyGrpcPicker implements EasyGrpcPicker{

    private static final String DEFAULT_TAG = "";
    private static final String DEFAULT_REGION = "";

    /**
     * tag->(region -> subchannels)
     */
    private final Map<String, Map<String, List<EasyGrpcSubchannel>>> readyMap;

    /**
     * tag -> is idle
     */
    private final boolean hasIdleNode;

    private final List<EasyGrpcSubchannel> list;
    AbstractReadyPicker(List<LoadBalancer.Subchannel> list) {
        // Subchannel的属性都是引用类型
        // 节点相同，但属性发生变化时，可能导致list变成一样的，无法比较两个picker是否一致
        this.list = list.stream()
                .map(EasyGrpcSubchannel::new)
                .collect(Collectors.toList());
        this.readyMap = getReadyTagSubChannelListMap(this.list);
        this.hasIdleNode = hasIdleNode();
    }


    private boolean hasIdleNode() {
        return this.list.stream()
                .anyMatch(r -> {
                    final ConnectivityState state = r.getState().getState();
                    return state == ConnectivityState.IDLE || state == ConnectivityState.CONNECTING;
                });
    }

    private Map<String, Map<String, List<EasyGrpcSubchannel>>> getReadyTagSubChannelListMap(List<EasyGrpcSubchannel> list) {
        return getTagRegionSubChannelListMap(list, (r) -> {
            final ConnectivityState state = r.getState().getState();
            return state == ConnectivityState.READY;
        });
    }

    /**
     * @return tag->region->subchannels
     */
    private Map<String, Map<String, List<EasyGrpcSubchannel>>> getTagRegionSubChannelListMap(
            List<EasyGrpcSubchannel> list,
            Predicate<EasyGrpcSubchannel> filter
    ) {
        List<EasyGrpcSubchannel> readyList = list
                .stream()
                .filter(filter)
                .collect(Collectors.toList());

        final Map<String, List<EasyGrpcSubchannel>> tagSubChannelsMap = readyList
                .stream()
                .collect(Collectors.groupingBy(r -> {
                    final String tag = r.getTag();
                    return tag == null ? DEFAULT_TAG : tag;
                }));

        Map<String, Map<String, List<EasyGrpcSubchannel>>> tagSubchannelListMap = new HashMap<>(tagSubChannelsMap.size());
        for (Map.Entry<String, List<EasyGrpcSubchannel>> entry : tagSubChannelsMap.entrySet()) {
            final Map<String, List<EasyGrpcSubchannel>> regionSubchannelsMap = entry.getValue()
                    .stream()
                    .collect(Collectors.groupingBy(r -> {
                        final String region = r.getRegion();
                        return region == null ? DEFAULT_REGION : region;
                    }));
            tagSubchannelListMap.put(entry.getKey(), regionSubchannelsMap);
        }
        return tagSubchannelListMap;
    }

    @Override
    public LoadBalancer.PickResult pickSubchannel(LoadBalancer.PickSubchannelArgs args) {
        //EasyGrpcSubchannel option = args.getCallOptions().getOption(EasyGrpcOption.SELECTED_SUBCHANNEL_KEY);
        final String tag = args.getCallOptions().getOption(TAG_KEY);
        final String region = args.getCallOptions().getOption(REGION_KEY);
        final List<EasyGrpcSubchannel> list = getSubchannels(tag, region);
        if (list.isEmpty()) {
            return getErrorPickResult(tag, region);
        }
        EasyGrpcSubchannel subchannel = pick(list);
        return subchannel == null
                ? getErrorPickResult(tag, region)
                : notifyAndReturn(args, subchannel);
    }

    private LoadBalancer.PickResult notifyAndReturn(LoadBalancer.PickSubchannelArgs args, EasyGrpcSubchannel easyGrpcSubchannel) {
        LoadBalancer.Subchannel subchannel = easyGrpcSubchannel.getSubchannel();
        return LoadBalancer.PickResult.withSubchannel(subchannel);
    }

    @Override
    boolean isEquivalentTo(AbstractEasyGrpcPicker picker) {
        if (!(picker instanceof AbstractReadyPicker)) {
            return false;
        }
        AbstractReadyPicker other = (AbstractReadyPicker) picker;
        // the lists cannot contain duplicate subchannels
        return other == this || (list.size() == other.list.size()
                && new HashSet<>(list).containsAll(other.list));
    }

    protected abstract EasyGrpcSubchannel pick(List<EasyGrpcSubchannel> list);

    @Override
    public List<EasyGrpcSubchannel> getSubchannels(String tag, String region) {
        Map<String, List<EasyGrpcSubchannel>> subChannelMap = getTagOrDefaultRegionMap(tag);
        if (subChannelMap == null) {
            return Collections.emptyList();
        }

        // 外部指定 region
        List<EasyGrpcSubchannel> subChannels = getRegionList(subChannelMap, region);
        if (!subChannels.isEmpty()) {
            return subChannels;
        }

        // 没有指定region，或者指定region不存在，使用当前机房
        region = getCurrentRegion();
        if (region == null) {
            region = DEFAULT_REGION;
        }
        subChannels = getRegionList(subChannelMap, region);
        if (!subChannels.isEmpty()) {
            return subChannels;
        }

        // 都不存在时，随机某个机房
        region = getRandomRegion(subChannelMap);
        return getRegionList(subChannelMap, region);
    }

    private Map<String, List<EasyGrpcSubchannel>> getTagOrDefaultRegionMap(String tag) {
        if (!StringHelper.isEmpty(tag)) {
            Map<String, List<EasyGrpcSubchannel>> regionMap = readyMap.get(tag.trim());
            if (regionMap != null) {
                return regionMap;
            }
        }
        return readyMap.get(DEFAULT_TAG);
    }

    private String getCurrentRegion(){
        return System.getenv("region") != null ? System.getenv("region") : System.getProperty("region", null);
    }

    private List<EasyGrpcSubchannel> getRegionList(Map<String, List<EasyGrpcSubchannel>> subChannelMap, String region) {
        final List<EasyGrpcSubchannel> subChannels = region == null ? null : subChannelMap.get(region.trim());
        return subChannels == null ? Collections.emptyList() : subChannels;
    }

    private String getRandomRegion(Map<String, List<EasyGrpcSubchannel>> subChannelMap) {
        final List<String> keys = Lists.newArrayList(subChannelMap.keySet());
        return keys.get(ThreadLocalRandom.current().nextInt(keys.size()));
    }

    private LoadBalancer.PickResult getErrorPickResult(String tag, String region) {
        if (hasIdleNode) {
            // 如果有空闲节点，可以通过返回withNoResult唤醒重连，再试一次
            return LoadBalancer.PickResult.withNoResult();
        } else {
            // 如果不存在任何节点，直接报错即可，重试是无效的
            final String envRegion = getCurrentRegion();
            return LoadBalancer.PickResult.withError(Status.UNAVAILABLE
                    .withCause(new NoSuchElementException())
                    .withDescription(String.format("当前服务无匹配节点, tag:%s, 外部指定机房:%s, 当前所属机房:%s",
                            StringHelper.isEmpty(tag) || DEFAULT_TAG.equals(tag) ? "default" : tag,
                            region == null ? "未指定" : region,
                            envRegion == null ? "default" : envRegion
                    ))
            );
        }
    }
}
