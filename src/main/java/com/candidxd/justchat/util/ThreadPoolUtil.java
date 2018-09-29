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

    public static String threadStartCallable(Callable<String> callable) throws InterruptedException, ExecutionException, TimeoutException {
        BlockingQueue blockingQueue = new ArrayBlockingQueue<>(10);
        ThreadFactory threadFactory = new DefaultThreadFactory("test");
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(10, 20, 1, TimeUnit.MINUTES, blockingQueue, threadFactory);
        Future future = threadPoolExecutor.submit(callable);
        if (future.get(60, TimeUnit.MINUTES) != null) {
            future.isDone();
            return future.get(60, TimeUnit.MINUTES).toString();
        }
        return null;
    }

    public static String threadStartRunnable(Runnable runnable) throws InterruptedException, ExecutionException, TimeoutException {
        BlockingQueue blockingQueue = new ArrayBlockingQueue<>(10);
        ThreadFactory threadFactory = new DefaultThreadFactory("test");
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(10, 20, 10, TimeUnit.MINUTES, blockingQueue, threadFactory);
        threadPoolExecutor.execute(runnable);
        return null;
    }
}
