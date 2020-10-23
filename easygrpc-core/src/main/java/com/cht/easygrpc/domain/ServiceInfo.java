package com.cht.easygrpc.domain;

import com.cht.easygrpc.exception.EasyGrpcException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author : chenhaitao934
 * @date : 2:02 下午 2020/10/9
 */
public class ServiceInfo {

    private final String name;
    private final Map<String, InterfaceInfo> interfaceInfos = new HashMap<>();


    public ServiceInfo(String name, List<Class<?>> interfaces) {
        this.name = name;
        try {
            interfaces.stream().filter(Objects::nonNull).forEach(iface -> interfaceInfos.put(iface.getName(), new InterfaceInfo(iface)));
        } catch (Exception e) {
            throw new EasyGrpcException(String.format("serviceInfo error, check your easygrpc conf. serviceName:%s, " +
                    "ifaces:%s", name, interfaces), e);
        }
    }

    public String getName() {
        return name;
    }

    public InterfaceInfo getInterfaceInfo(String ifaceName) {
        InterfaceInfo interfaceInfo = interfaceInfos.get(ifaceName);
        if (interfaceInfo == null) {
            throw new EasyGrpcException("Interface(" + ifaceName + ") doesn't Exsit in Service(" + name + ")!");
        }
        return interfaceInfo;
    }
}
