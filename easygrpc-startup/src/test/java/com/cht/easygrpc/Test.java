package com.cht.easygrpc;

import com.cht.easygrpc.remoting.iface.EasyGrpcTest;
import com.cht.easygrpc.support.instance.Container;
import com.cht.easygrpc.support.instance.EasyGrpcInjector;

/**
 * @author : chenhaitao934
 */
public class Test {

    public static void main(String[] args) {
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.start();

        Container container = EasyGrpcInjector.getInstance(Container.class);
        EasyGrpcTest instance = container.createInstance(EasyGrpcTest.class);
        String result = instance.hello("ada");
        System.out.println(result);
    }
}
