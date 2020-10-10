package com.cht.easygrpc.loadbalance;

import com.cht.easygrpc.helper.CollectionHelper;
import com.cht.easygrpc.helper.StringHelper;
import io.grpc.EquivalentAddressGroup;
import io.grpc.LoadBalancer;
import io.grpc.Status;

import javax.annotation.Nullable;
import java.util.*;
import java.util.stream.Collectors;

import static com.cht.easygrpc.constant.EasyGrpcOption.SELECTED_SUBCHANNELS_KEY;
import static com.cht.easygrpc.constant.EasyGrpcOption.TAG_KEY;

/**
 * @author : chenhaitao934
 * @date : 4:04 下午 2020/10/10
 */
public abstract class AbstractEasyGrpcPicker extends LoadBalancer.SubchannelPicker{

    private static final String DEFAULT_TAG = "";
    protected final Map<String, List<SubchannelGroup>> tagSubchanneGrouplListMap;
    protected final Map<String, List<SubchannelGroup>> readyTagSubchanneGrouplListMap;

    private final Status status;
    private final List<SubchannelGroup> list;
    private final List<SubchannelGroup> readyList;

    private final String serviceName;


    protected AbstractEasyGrpcPicker(List<SubchannelGroup> list, Status status, String serviceName) {
        this.list = Collections.unmodifiableList(list);
        this.readyList = list.stream().filter(SubchannelGroup::getReadyState).collect(Collectors.toList());

        this.tagSubchanneGrouplListMap = list.stream().collect(Collectors.groupingBy(SubchannelGroup::getTag));
        this.readyTagSubchanneGrouplListMap = readyList.stream().collect(Collectors.groupingBy(SubchannelGroup::getTag));

        this.status = status;
        this.serviceName = serviceName;
    }

    @Override
    public LoadBalancer.PickResult pickSubchannel(LoadBalancer.PickSubchannelArgs args) {
        if (getList().size() <= 0) {
            throw new NoSuchElementException();
        }

        /*CustomRoute customRoute = CustomRouteConf.getCustomRoute(args.getCallOptions().getOption(IFACE_METHOD_KEY));
        if (customRoute != null) {
            // 若有配置customRoute，则使用customRoute返回的节点
            Object[] params = args.getCallOptions().getOption(CALL_PARAMS_KEY);
            ServerNodeInfo serverNodeInfo = customRoute.route(transToServerNodeList(getList()), params);
            if (serverNodeInfo == null || serverNodeInfo.getSubchannelGroup() == null) {
                throw new NoLeftProviderException(String.format("customRoute.route return null. current list:%s", getList()));
            }
            return notifyAndReturn(args, serverNodeInfo.getSubchannelGroup());
        }*/
        String tag = getTagOrDefault(args);
        SubchannelGroup subchannelGroup = getSubchannel(args.getCallOptions().getOption(SELECTED_SUBCHANNELS_KEY), tag);
        return notifyAndReturn(args, subchannelGroup);
    }

    private String getTagOrDefault(LoadBalancer.PickSubchannelArgs args) {
        String tag = args.getCallOptions().getOption(TAG_KEY);
        tag = StringHelper.isEmpty(tag) ? DEFAULT_TAG : tag;
        if (CollectionHelper.isEmpty(getTagList(tag))) {
            return DEFAULT_TAG;
        }
        return tag;
    }

    private LoadBalancer.PickResult notifyAndReturn(LoadBalancer.PickSubchannelArgs args, SubchannelGroup subchannelGroup) {
        /*PickResultListener listener = args.getCallOptions().getOption(PICK_RESULT_LISTENER_KEY);
        if (listener != null) {
            listener.notifyPickResult(subchannelGroup.pickSubchannel().getAddresses());
        }*/
        return LoadBalancer.PickResult.withSubchannel(subchannelGroup.pickSubchannel());
    }


    private SubchannelGroup getSubchannel(List<EquivalentAddressGroup> selectedAddresses, String tag) {
        List<SubchannelGroup> tagList = getTagList(tag);
        if (CollectionHelper.isEmpty(tagList)) {
            throw new NoSuchElementException();
        }

        if (CollectionHelper.isNotEmpty(selectedAddresses)) {
            // 若selectedSubchannelsKey有值，则选取剩余的subchannel
            return pickFirstLeftSubchannels(selectedAddresses, tagList);
        }

        List<SubchannelGroup> readyTagList = getReadyTagList(tag);
        if (CollectionHelper.isEmpty(readyTagList)) {
            readyTagList = tagList;
        }
        return pick(readyTagList);
    }

    private SubchannelGroup pickFirstLeftSubchannels(List<EquivalentAddressGroup> selectedAddresses, List<SubchannelGroup> tagList) {
        for (SubchannelGroup subchannelGroup : tagList) {
            if (!selectedAddresses.contains(subchannelGroup.getAddress())) {
                return subchannelGroup;
            }
        }
        return tagList.get(0);
    }

    protected abstract SubchannelGroup pick(List<SubchannelGroup> list);

    private List<SubchannelGroup> getList() {
        return list;
    }

    private List<SubchannelGroup> getTagList(String tag) {
        return tagSubchanneGrouplListMap.get(tag);
    }

    private List<SubchannelGroup> getReadyTagList(String tag) {
        return readyTagSubchanneGrouplListMap.get(tag);
    }

    private Status getStatus() {
        return status;
    }

    protected Map<String, Object> initCondition() {
        Map<String, Object> condition = new HashMap<>();
        condition.put("service", serviceName);
        return condition;
    }

}
