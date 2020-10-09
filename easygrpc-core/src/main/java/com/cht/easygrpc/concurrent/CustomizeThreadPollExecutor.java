package com.cht.easygrpc.concurrent;




import com.cht.easygrpc.exception.TaskRejectedException;

import java.util.Map;
import java.util.concurrent.*;

/**
 * @author : chenhaitao934
 * @date : 8:42 下午 2020/5/24
 */
public class CustomizeThreadPollExecutor extends CustomizeThreadFactory {
    private int corePoolSize = 1;
    private int maxPoolSize = Integer.MAX_VALUE;
    private int keepAliveSeconds = 60;
    private int queueCapacity = Integer.MAX_VALUE;
    private boolean allowCoreThreadTimeOut = false;
    private ThreadDecorator threadDecorator;
    private ThreadPoolExecutor threadPoolExecutor;
    private final Map<Runnable, Object> decoratedThreadMap = new ConcurrentHashMap<>();

    public CustomizeThreadPollExecutor(String threadNamePrefix, int corePoolSize, int maxPoolSize, int keepAliveSeconds,
                                       int queueCapacity, boolean allowCoreThreadTimeOut, ThreadDecorator threadDecorator) {
        super(threadNamePrefix);
        this.corePoolSize = corePoolSize;
        this.maxPoolSize = maxPoolSize;
        this.keepAliveSeconds = keepAliveSeconds;
        this.queueCapacity = queueCapacity;
        this.allowCoreThreadTimeOut = allowCoreThreadTimeOut;
        this.threadDecorator = threadDecorator;
    }

    public CustomizeThreadPollExecutor(int corePoolSize, int maxPoolSize, int keepAliveSeconds, int queueCapacity, boolean allowCoreThreadTimeOut, ThreadDecorator threadDecorator) {
        this(null, corePoolSize, maxPoolSize, keepAliveSeconds, queueCapacity, allowCoreThreadTimeOut
                                , threadDecorator);
    }
    public CustomizeThreadPollExecutor(){
        super();
    }

    public CustomizeThreadPollExecutor(String threadNamePrefix, int corePoolSize, int maxPoolSize, int keepAliveSeconds, int queueCapacity){
        super();
        this.corePoolSize = corePoolSize;
        this.maxPoolSize = maxPoolSize;
        this.keepAliveSeconds = keepAliveSeconds;
        this.queueCapacity = queueCapacity;
    }

    public ExecutorService initializeExecutor(ThreadFactory threadFactory, RejectedExecutionHandler rejectedExecutionHandler){
        BlockingQueue<Runnable> queue = createQueue(this.queueCapacity);

        ThreadPoolExecutor executor;
        if (this.threadDecorator != null) {
            executor = new ThreadPoolExecutor(
                    this.corePoolSize, this.maxPoolSize, this.keepAliveSeconds, TimeUnit.SECONDS,
                    queue, threadFactory, rejectedExecutionHandler) {
                @Override
                public void execute(Runnable command) {
                    Runnable decorated = threadDecorator.decorate(command);
                    if (decorated != command) {
                        decoratedThreadMap.put(decorated, command);
                    }
                    super.execute(decorated);
                }
            };
        }
        else {
            executor = new ThreadPoolExecutor(
                    this.corePoolSize, this.maxPoolSize, this.keepAliveSeconds, TimeUnit.SECONDS,
                    queue, threadFactory, rejectedExecutionHandler);

        }

        if (this.allowCoreThreadTimeOut) {
            executor.allowCoreThreadTimeOut(true);
        }

        this.threadPoolExecutor = executor;
        return executor;
    }

    private BlockingQueue<Runnable> createQueue(int queueCapacity) {
        if (queueCapacity > 0) {
            return new LinkedBlockingQueue<>(queueCapacity);
        }
        else {
            return new SynchronousQueue<>();
        }
    }

    public void execute(Runnable task) {
        Executor executor = getThreadPoolExecutor();
        try {
            executor.execute(task);
        }
        catch (RejectedExecutionException ex) {
            throw new TaskRejectedException("Executor [" + executor + "] did not accept task: " + task, ex);
        }
    }

    private ExecutorService getThreadPoolExecutor() {
        return this.threadPoolExecutor;
    }

    public void execute(Runnable task, long startTimeout) {
        execute(task);
    }

    public Future<?> submit(Runnable task) {
        ExecutorService executor = getThreadPoolExecutor();
        try {
            return executor.submit(task);
        }
        catch (RejectedExecutionException ex) {
            throw new TaskRejectedException("Executor [" + executor + "] did not accept task: " + task, ex);
        }
    }

    public <T> Future<T> submit(Callable<T> task) {
        ExecutorService executor = getThreadPoolExecutor();
        try {
            return executor.submit(task);
        }
        catch (RejectedExecutionException ex) {
            throw new TaskRejectedException("Executor [" + executor + "] did not accept task: " + task, ex);
        }
    }

}
