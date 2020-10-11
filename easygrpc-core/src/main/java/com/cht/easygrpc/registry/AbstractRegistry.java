package com.cht.easygrpc.registry;

import com.cht.easygrpc.EasyGrpcContext;

/**
 * @author : chenhaitao934
 * @date : 12:51 上午 2020/10/12
 */
public abstract class AbstractRegistry implements Registry{

    private EasyGrpcContext context;

    public AbstractRegistry(EasyGrpcContext context) {
        this.context = context;
    }

    @Override
    public void register(Node node) {
        doRegister(node);
    }

    protected abstract void doRegister(Node node);
}
