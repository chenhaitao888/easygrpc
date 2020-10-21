package com.cht.easygrpc;

import com.cht.easygrpc.config.EasyGrpcConfig;
import com.cht.easygrpc.exception.EasyGrpcException;
import com.cht.easygrpc.registry.EasyGrpcRegistry;
import com.cht.easygrpc.remoting.conf.EasyGrpcCommonConfig;
import com.cht.easygrpc.remoting.conf.EasyGrpcServerConfig;
import com.cht.easygrpc.support.AliveKeeping;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * @author : chenhaitao934
 * @date : 5:21 下午 2020/10/12
 */
public class Bootstrap extends AbstractEasyGrpcStarter<EasyGrpcContext>{


    public Bootstrap(EasyGrpcContext context, Class<?> initializer) {
        super(context, initializer);
    }

    public EasyGrpcConfig loadConfig()  {
        EasyGrpcConfig grpcConfig = null;
        try {
            grpcConfig = new EasyGrpcConfig();
            String confPath = this.getClass().getClassLoader().getResource("easy-grpc.yml").getFile();
            File file = new File(confPath);
            FileInputStream in = new FileInputStream(file);
            grpcConfig = grpcConfig.fromYAML(in);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return grpcConfig;
    }

}
