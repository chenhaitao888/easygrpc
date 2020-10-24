package com.cht.easygrpc;

import com.cht.easygrpc.remoting.iface.EasyGrpcTest;
import com.cht.easygrpc.support.instance.Container;
import com.cht.easygrpc.support.instance.DefaultContainer;
import com.cht.easygrpc.support.instance.EasyGrpcInjector;

/**
 * @author : chenhaitao934
 */
public class ClientTest {

    public static void main(String[] args) {
        Container container = EasyGrpcInjector.getInstance(Container.class);
        EasyGrpcTest instance = container.createInstance(EasyGrpcTest.class);
        String result = instance.hello("ada");

    }
}
