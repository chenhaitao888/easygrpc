package com.cht.easygrpc.support.stub;

import com.cht.easygrpc.support.Invocation;
import io.grpc.Channel;
import io.grpc.ManagedChannel;
import io.grpc.stub.AbstractStub;

import java.util.concurrent.TimeUnit;
import java.util.function.Function;

import static com.cht.easygrpc.constant.EasyGrpcOption.CALL_PARAMS_KEY;
import static com.cht.easygrpc.constant.EasyGrpcOption.IFACE_METHOD_KEY;

/**
 * @author : chenhaitao934
 */
public abstract class AbstractStubBuilder<T extends AbstractStub<T>> {

    private final long timeout;
    private final Function<Channel, T> stubFunction;
    private final Invocation invocation;
    private final Channel manageChannel;

    public AbstractStubBuilder(Function<Channel, T> stubChannelFunction,
                               Invocation invocation, long timeout, Channel manageChannel) {
        this.stubFunction = stubChannelFunction;
        this.invocation = invocation;
        this.timeout = timeout;
        this.manageChannel = manageChannel;
    }

    public T buildStub(){
        T stub = stubFunction.apply(manageChannel)
                .withDeadlineAfter(timeout, TimeUnit.MILLISECONDS)
                .withOption(IFACE_METHOD_KEY, invocation.getIfaceMethodKey())
                .withOption(CALL_PARAMS_KEY, invocation.getArguments() == null ? new Object[]{} :
                        invocation.getArguments());
        return stub;
    }
}
