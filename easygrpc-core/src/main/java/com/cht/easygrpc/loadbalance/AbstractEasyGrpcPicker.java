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
    abstract boolean isEquivalentTo(AbstractEasyGrpcPicker picker);


}
