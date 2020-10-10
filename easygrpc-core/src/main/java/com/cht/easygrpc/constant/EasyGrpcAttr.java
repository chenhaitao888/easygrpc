package com.cht.easygrpc.constant;

import io.grpc.Attributes;
import io.grpc.CallOptions;
import io.grpc.EquivalentAddressGroup;

import java.util.List;

/**
 * @author : chenhaitao934
 * @date : 3:42 下午 2020/10/10
 */
public interface EasyGrpcAttr {
    //负载均衡策略
    Attributes.Key<Integer> GROUP_LBSTRATEGY = Attributes.Key.create("groupLbStrategy");

    //serverNode 权重
    Attributes.Key<Integer> SERVER_WEIGHT = Attributes.Key.create("serverWeight");
    Attributes.Key<String> SERVER_TAG = Attributes.Key.create("serverTag");

}
