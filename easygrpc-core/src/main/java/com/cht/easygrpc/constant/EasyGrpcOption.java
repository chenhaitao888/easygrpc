package com.cht.easygrpc.constant;

import com.cht.easygrpc.loadbalance.EasyGrpcSubchannel;
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
    CallOptions.Key<String> IFACE_METHOD_KEY = CallOptions.Key.createWithDefault("ifaceMethodKey", null);
    CallOptions.Key<Object[]> CALL_PARAMS_KEY = CallOptions.Key.createWithDefault("callParamsKey", null);

    CallOptions.Key<EasyGrpcSubchannel> SELECTED_SUBCHANNEL_KEY = CallOptions.Key.create("selectedSubchannelKey");
    CallOptions.Key<String> REGION_KEY = CallOptions.Key.create("regionKey");
}
