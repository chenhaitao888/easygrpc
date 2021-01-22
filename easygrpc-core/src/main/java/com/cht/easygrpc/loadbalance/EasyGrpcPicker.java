package com.cht.easygrpc.loadbalance;

import java.util.List;

/**
 * @author : chenhaitao934
 */
public interface EasyGrpcPicker {

    List<EasyGrpcSubchannel> getSubchannels(String tag, String region);
}
