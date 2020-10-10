package com.cht.easygrpc.constant;

import io.grpc.CallOptions;
import io.grpc.EquivalentAddressGroup;

import java.util.List;

/**
 * @author : chenhaitao934
 * @date : 4:09 下午 2020/10/10
 */
public interface EasyGrpcOption {

    CallOptions.Key<List<EquivalentAddressGroup>> SELECTED_SUBCHANNELS_KEY = CallOptions.Key.createWithDefault("selectedSubchannelsKey", null);
    CallOptions.Key<String> TAG_KEY = CallOptions.Key.createWithDefault("tagKey", null);
}
