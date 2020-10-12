package com.cht.easygrpc.registry;

import com.cht.easygrpc.EasyGrpcContext;

/**
 * @author : chenhaitao934
 * @date : 12:51 上午 2020/10/12
 */
public abstract class AbstractRegistry implements Registry{

    protected EasyGrpcContext context;

    protected String appId;

    protected String ROOT_PATH = "/easy-grpc";

    public AbstractRegistry(EasyGrpcContext context) {
        this.context = context;
    }

    @Override
    public void register() {
        doRegister();
    }

    protected abstract void doRegister();

    protected String basePath(){
        return ROOT_PATH + "/" + appId;
    }

    protected String getServerPath(){
        return basePath() + "/server/nodes";
    }

    protected String getConsumePath(){
        return basePath() + "/consume";
    }

    protected String getSelectorPath(){
        return basePath() + "/selector";
    }

    protected String getFullPath(AbstractNode node){
        StringBuilder path = new StringBuilder();

        path.append(basePath())
                .append("/")
                .append(node.getNodeType())
                .append("/")
                .append(node.getIp());

        if (node.getPort() != 0) {
            path.append(":").append(node.getPort());
        }

        return path.toString();
    }

}
