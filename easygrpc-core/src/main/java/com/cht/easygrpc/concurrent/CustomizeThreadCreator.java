package com.cht.easygrpc.concurrent;



import com.cht.easygrpc.helper.ClassHelper;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author : chenhaitao934
 * @date : 8:32 下午 2020/5/24
 */
public class CustomizeThreadCreator {
    private String threadNamePrefix;
    private int threadPriority = Thread.NORM_PRIORITY;
    private boolean daemon = false;
    private ThreadGroup threadGroup;
    private final AtomicInteger threadCount = new AtomicInteger(0);

    public CustomizeThreadCreator(String threadNamePrefix) {
        this.threadNamePrefix = (threadNamePrefix != null ? threadNamePrefix : getDefaultThreadNamePrefix());
    }
    public CustomizeThreadCreator() {
        this.threadNamePrefix = getDefaultThreadNamePrefix();
    }

    private String getDefaultThreadNamePrefix() {
        return ClassHelper.getShortName(getClass()) + "-";
    }


    public String getThreadNamePrefix() {
        return threadNamePrefix;
    }

    public void setThreadNamePrefix(String threadNamePrefix) {
        this.threadNamePrefix = threadNamePrefix;
    }

    public int getThreadPriority() {
        return threadPriority;
    }

    public void setThreadPriority(int threadPriority) {
        this.threadPriority = threadPriority;
    }

    public boolean isDaemon() {
        return daemon;
    }

    public void setDaemon(boolean daemon) {
        this.daemon = daemon;
    }

    public ThreadGroup getThreadGroup() {
        return threadGroup;
    }

    public void setThreadGroup(ThreadGroup threadGroup) {
        this.threadGroup = threadGroup;
    }
    public void setThreadGroupName(String name) {
        this.threadGroup = new ThreadGroup(name);
    }

    protected String nextThreadName() {
        return getThreadNamePrefix() + this.threadCount.incrementAndGet();
    }

    public Thread createThread(Runnable runnable) {
        Thread thread = new Thread(getThreadGroup(), runnable, nextThreadName());
        thread.setPriority(getThreadPriority());
        thread.setDaemon(isDaemon());
        return thread;
    }
}
