package com.candidxd.justchat.thread;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.candidxd.justchat.bean.Customer;
import com.candidxd.justchat.bean.CustomerStatus;
import com.candidxd.justchat.bean.Match;
import com.candidxd.justchat.enums.ReturnEnum;
import com.candidxd.justchat.util.DateTimeUtil;
import com.candidxd.justchat.util.JsonUtil;

import java.util.List;
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
    private MatchHandler matchHandler;

    private String name;

    private ReentrantLock lock;

    public void setName(String name) {
        this.name = name;
    }

    public MatchCallable(MatchHandler matchHandler, ReentrantLock lock) {
        this.matchHandler = matchHandler;
        this.lock = lock;
    }

    @Override
    public String call() throws Exception {
        System.out.println(Thread.currentThread().getName());
        String data = null;
        while (data == null) {
            data = matchHandler.run(name, lock);
            try {
                System.out.println("线程:" + name + "     sleep");
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        if (data != null) {
            System.out.println("线程:" + name + "     跳出循环");
            return data;
        }
        return null;
    }
}
