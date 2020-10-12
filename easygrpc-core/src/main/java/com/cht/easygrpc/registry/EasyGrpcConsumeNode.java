package com.cht.easygrpc.registry;

import org.apache.curator.framework.recipes.cache.ChildData;

/**
 * @author : chenhaitao934
 * @date : 12:35 上午 2020/10/12
 */
public class EasyGrpcConsumeNode extends AbstractGenericNode<EasyGrpcConsumeNode, EasyGrpcConsumeNode.Data>{
    public EasyGrpcConsumeNode(ChildData childData) {
        super(childData);
    }

    public EasyGrpcConsumeNode(String path, byte[] bytes) {
        super(path, bytes);
    }

    public EasyGrpcConsumeNode(String path, EasyGrpcConsumeNode.Data nodeData) {
        super(path, nodeData);
    }

    public static class Data extends AbstractNodeData<EasyGrpcConsumeNode.Data>{

        public Data(String ip, int port, String nodeType) {
            super(ip, port, nodeType);
        }
    }
}
