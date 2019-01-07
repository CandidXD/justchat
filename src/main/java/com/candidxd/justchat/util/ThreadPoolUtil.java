package com.candidxd.justchat.util;

import io.netty.util.concurrent.DefaultThreadFactory;

import java.util.concurrent.*;

/**
 * @author yzk
 * @Title: ThreadPoolUtil
 * @ProjectName justchat
 * @Description: TODO
 * @date 2018/9/2611:41 AM
 */
public class ThreadPoolUtil {
    private static BlockingQueue blockingQueue = new ArrayBlockingQueue<>(5000);
    private static ThreadFactory threadFactory = new DefaultThreadFactory("test");
    private static ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(5000, 20000, 100, TimeUnit.MINUTES, blockingQueue, threadFactory);

    /**
     * 回调线程
     *
     * @param callable ：callable
     * @return java.lang.String
     * @author yaozekai
     * @date 2018/10/12 9:48 AM
     */
    public static String threadStartCallable(Callable<String> callable) throws InterruptedException, ExecutionException, TimeoutException {
        Future future = threadPoolExecutor.submit(callable);
        if (future.get(60, TimeUnit.MINUTES) != null) {
            future.isDone();
            return future.get(60, TimeUnit.MINUTES).toString();
        }
        return null;
    }

    /**
     * 普通线程
     *
     * @param runnable ：runnable
     * @return java.lang.String
     * @author yaozekai
     * @date 2018/10/12 9:49 AM
     */
    public static String threadStartRunnable(Runnable runnable) throws InterruptedException, ExecutionException, TimeoutException {
        BlockingQueue blockingQueue = new ArrayBlockingQueue<>(10);
        ThreadFactory threadFactory = new DefaultThreadFactory("test");
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(10, 20, 10, TimeUnit.MINUTES, blockingQueue, threadFactory);
        threadPoolExecutor.execute(runnable);
        return null;
    }
}
