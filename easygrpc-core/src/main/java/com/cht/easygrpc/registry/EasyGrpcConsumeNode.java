package com.cht.easygrpc.registry;

import com.cht.easygrpc.domain.CircuitBreakerInfo;
import org.apache.curator.framework.recipes.cache.ChildData;

import java.util.List;

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

    // basepath/client/serviceName/clientName/{data}
    public static class Data extends AbstractNode<EasyGrpcConsumeNode.Data>{

        private String serviceName;
        private String clientName;

        private List<CircuitBreakerInfo> circuitBreakerInfos;

        public String getServiceName() {
            return serviceName;
        }

        public void setServiceName(String serviceName) {
            this.serviceName = serviceName;
        }

        public String getClientName() {
            return clientName;
        }

        public void setClientName(String clientName) {
            this.clientName = clientName;
        }

        public List<CircuitBreakerInfo> getCircuitBreakerInfos() {
            return circuitBreakerInfos;
        }

        public void setCircuitBreakerInfos(List<CircuitBreakerInfo> circuitBreakerInfos) {
            this.circuitBreakerInfos = circuitBreakerInfos;
        }
    }
}
