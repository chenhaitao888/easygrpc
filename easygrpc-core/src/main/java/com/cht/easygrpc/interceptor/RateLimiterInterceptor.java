package com.cht.easygrpc.interceptor;

import com.cht.easygrpc.EasyGrpcContext;
import com.cht.easygrpc.support.Invocation;
import com.cht.easygrpc.support.stub.AbstractGrpcStub;
import com.google.common.util.concurrent.RateLimiter;

import java.util.concurrent.atomic.AtomicLong;

/**
 * @author : chenhaitao934
 */
public class RateLimiterInterceptor extends AbstractInterceptor{

    public RateLimiterInterceptor(EasyGrpcContext context) {
        super(context);
    }
    static volatile boolean stop = false;

    @Override
    public Object interceptCall(Invocation invocation, AbstractGrpcStub nextStub) throws Exception {

        return null;
    }

    public static void main(String[] args) throws Exception {
        final RateLimiter rateLimiter = RateLimiter.create(10.0);

        long start = System.nanoTime();
        final AtomicLong okNum = new AtomicLong(0);
        final AtomicLong blockNum = new AtomicLong(0);

        for (int i = 0; i < 1; i++) {

            new Thread(new Runnable() {
                @Override
                public void run() {
                    while (!stop) {
                        if (rateLimiter.tryAcquire()) {
                            okNum.incrementAndGet();
                        } else {
                            blockNum.incrementAndGet();
                        }
                    }
                }
            }).start();
        }

        Thread.sleep(10 * 1000L);

        stop = true;

        System.out.println(System.nanoTime() - start);
        System.out.println("ok=" + okNum.get());
        System.out.println("block=" + blockNum.get());
    }
}
