package com.cht.easygrpc.remoting.iface;

/**
 * @author : chenhaitao934
 */
public class EasyGrpcTestImpl implements EasyGrpcTest{

    @Override
    public String hello(String req) {
        System.out.println("hello: " + req);
        return "hello: " + req;
    }
}
