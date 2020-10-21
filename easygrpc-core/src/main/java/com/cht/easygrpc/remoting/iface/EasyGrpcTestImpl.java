package com.cht.easygrpc.remoting.iface;

import com.cht.easygrpc.annotation.EasyGrpcService;

/**
 * @author : chenhaitao934
 */
@EasyGrpcService
public class EasyGrpcTestImpl implements EasyGrpcTest{

    @Override
    public String hello(String req) {
        System.out.println("hello: " + req);
        return "hello: " + req;
    }
}
