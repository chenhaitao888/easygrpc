package com.cht.easygrpc.registry;

import com.cht.easygrpc.EasyGrpcContext;

/**
 * @author : chenhaitao934
 * @date : 11:05 上午 2020/10/12
 */
public class EasyGrpcRegistry extends ZookeeperRegistry {

    public EasyGrpcRegistry(EasyGrpcContext context) {
        super(context);
    }


    @Override
    protected void doRegister(Node node) {

    }

}
