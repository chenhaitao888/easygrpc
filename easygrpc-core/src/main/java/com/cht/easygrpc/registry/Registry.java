package com.cht.easygrpc.registry;

import org.apache.curator.framework.recipes.cache.ChildData;

import java.util.List;
import java.util.Map;

/**
 * @author : chenhaitao934
 * @date : 2:06 下午 2020/10/10
 */
public interface Registry {

    void register();

    List<Map<String, Object>> assembleServers(EasyGrpcServiceNode.Data serverData);

    ChildData getServerData();

    ChildData getServiceData(String serviceName);
}
