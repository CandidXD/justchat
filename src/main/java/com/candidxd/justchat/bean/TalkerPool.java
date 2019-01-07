package com.candidxd.justchat.bean;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @description: TODO
 * @auther: yaozekai
 * @date: 2019-01-06 04:52
 */
public class TalkerPool {
    private Map<String, Talker> map = new ConcurrentHashMap<>();
    private static TalkerPool talkerPool = null;

    public static TalkerPool getTalkerPool() {
        if (talkerPool == null) {
            talkerPool = new TalkerPool();
        }
        return talkerPool;
    }

    public void put(Talker talker) {
        map.put(talker.getUid1(), talker);
    }

    public Talker get(String uuid) {
        return map.get(uuid);
    }

    public void del(String uuid) {
        map.remove(uuid);
    }
}
