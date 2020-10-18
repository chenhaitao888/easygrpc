package com.cht.easygrpc.registry;

import org.apache.curator.framework.recipes.cache.ChildData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author : chenhaitao934
 * @date : 12:22 上午 2020/10/12
 */
public class EasyGrpcServiceNode extends AbstractGenericNode<EasyGrpcServiceNode, EasyGrpcServiceNode.Data>{

    public EasyGrpcServiceNode(ChildData childData) {
        super(childData);
    }

    public EasyGrpcServiceNode(String path, byte[] bytes) {
        super(path, bytes);
    }

    public EasyGrpcServiceNode(String path, Data data) {
        super(path, data);
    }

    public static class Data extends AbstractNode<Data>{

        private List<String> address;

        private List<String> ifaces;

        private int workThreads;
        private int weight;
        private int queueCapacity;
        private String tag;
        private String serviceName;

        public Data() {
        }

        public Data(String ip, int port, String nodeType) {
            super(ip, port, nodeType);
        }



        public void addAdress(String adress) {
            if (address == null) {
                address = new CopyOnWriteArrayList<>();
            }
            address.add(adress);
        }

        public void removeAdress(String adress) {
            if (address == null) {
                return;
            }
            address.remove(adress);
        }

        public void addIfaces(String iface) {
            if (ifaces == null) {
                ifaces = new CopyOnWriteArrayList<>();
            }
            ifaces.add(iface);
        }

        public void removeIfaces(String iface) {
            if (ifaces == null) {
                return;
            }
            ifaces.remove(iface);
        }

        public Map<String, Object> buildMap(String ip, int port) {
            Map<String, Object> result = new HashMap<>();
            result.put("host", ip);
            result.put("port", port);
            result.put("weight", this.getWeight());
            result.put("tag", this.getTag());
            return result;
        }


        public List<String> getAddress() {
            return address;
        }

        public void setAddress(List<String> address) {
            this.address = address;
        }

        public int getWorkThreads() {
            return workThreads;
        }

        public void setWorkThreads(int workThreads) {
            this.workThreads = workThreads;
        }

        public int getWeight() {
            return weight;
        }

        public void setWeight(int weight) {
            this.weight = weight;
        }

        public int getQueueCapacity() {
            return queueCapacity;
        }

        public void setQueueCapacity(int queueCapacity) {
            this.queueCapacity = queueCapacity;
        }

        public List<String> getIfaces() {
            return ifaces;
        }

        public void setIfaces(List<String> ifaces) {
            this.ifaces = ifaces;
        }

        public String getTag() {
            return tag;
        }

        public void setTag(String tag) {
            this.tag = tag;
        }

        public String getServiceName() {
            return serviceName;
        }

        public void setServiceName(String serviceName) {
            this.serviceName = serviceName;
        }
    }
}
