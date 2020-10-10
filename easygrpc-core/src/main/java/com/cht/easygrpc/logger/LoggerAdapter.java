package com.cht.easygrpc.logger;

import com.cht.easygrpc.constant.ExtRpcConfig;
import com.cht.easygrpc.spi.SPI;

/**
 * @author : chenhaitao934
 * @date : 8:08 下午 2020/10/9
 */
@SPI(key = ExtRpcConfig.RPC_LOGGER, value = "slf4j")
public interface LoggerAdapter {
    Logger getLogger(Class<?> key);
    Logger getLogger(String key);
}
