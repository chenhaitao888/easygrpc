package com.cht.easygrpc;

import com.cht.easygrpc.config.EasyGrpcConfig;
import com.cht.easygrpc.exception.EasyGrpcException;
import com.cht.easygrpc.registry.EasyGrpcRegistry;
import com.cht.easygrpc.remoting.conf.EasyGrpcCommonConfig;
import com.cht.easygrpc.remoting.conf.EasyGrpcServerConfig;
import com.cht.easygrpc.remoting.iface.EasyGrpcServiceInitializer;
import com.cht.easygrpc.support.AliveKeeping;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * @author : chenhaitao934
 * @date : 5:21 下午 2020/10/12
 */
public class EasyGrpcBootstrap extends AbstractEasyGrpcStarter<EasyGrpcContext>{


    public EasyGrpcBootstrap() {
        this.initializer = EasyGrpcServiceInitializer.class;
    }

    public EasyGrpcConfig loadConfig()  {
        EasyGrpcConfig grpcConfig;
        try {
            grpcConfig = new EasyGrpcConfig();
            String confPath = this.getClass().getClassLoader().getResource("easy-grpc.yml").getFile();
            String log4jPath = this.getClass().getClassLoader().getResource("log4j.properties").getFile();
            File file = new File(confPath);
            FileInputStream in = new FileInputStream(file);
            grpcConfig = grpcConfig.fromYAML(in);
            grpcConfig.setLog4jPath(log4jPath);
        } catch (IOException e) {
            throw new EasyGrpcException("loadConfig failure", e);
        }

        return grpcConfig;
    }

}
