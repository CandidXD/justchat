package com.candidxd.justchat.thread;

import com.candidxd.justchat.service.MatchService;

import java.util.concurrent.Callable;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author yzk
 * @Title: MatchCallable
 * @ProjectName justchat
 * @Description: TODO
 * @date 2018/9/3012:41 PM
 */

public class MatchCallable implements Callable {

    private MatchService matchService;

    private MatchHandler matchHandler;

    private String name;

    private ReentrantLock lock;

    public MatchCallable setName(String name) {
        this.name = name;
        return this;
    }

    public MatchCallable(MatchHandler matchHandler, ReentrantLock lock, MatchService matchService) {
        this.matchHandler = matchHandler;
        this.lock = lock;
        this.matchService = matchService;
    }

    @Override
    public String call() throws Exception {
        System.out.println(Thread.currentThread().getName());
        long before = System.currentTimeMillis();
        String data = null;
        while (data == null) {
            data = matchHandler.run(name, lock);
            // if (data == null) {
            //     while (matchService.match(new Customer().setUid(name)) == null) {
            //         try {
            //             System.out.println("线程:" + name + "     sleep");
            //             Thread.sleep(10);
            //         } catch (InterruptedException e) {
            //             e.printStackTrace();
            //         }
            //     }
            // }
            try {
                System.out.println("线程:" + name + "     sleep");
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        long after = System.currentTimeMillis();
        System.out.println("线程:" + name + "     执行了  ：   " + String.valueOf((after - before) / 1000) + " s");
        System.out.println("线程:" + name + "     跳出循环");
        return data;
    }
}
