package com.cht.easygrpc.remoting;

import com.cht.easygrpc.exception.RemotingException;

/**
 * @author : chenhaitao934
 * @date : 2:07 下午 2020/10/9
 */
public interface EasyGrpcRemotingServer {
    void start() throws RemotingException;

    void shutdown() throws RemotingException;
}
