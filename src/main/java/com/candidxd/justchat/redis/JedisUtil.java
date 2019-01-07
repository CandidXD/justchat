package com.candidxd.justchat.redis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.Transaction;

import java.util.Set;

/**
 * @author yzk
 * @Title: RedisCache
 * @ProjectName justchat
 * @Description: TODO
 * @date 2018/9/264:03 PM
 */
@Component
public class JedisUtil {
    @Autowired
    private JedisPool jedisPool;

    public void set(String key, String value) {
        Jedis jedis = jedisPool.getResource();
        try {
            Transaction ts = jedis.multi();
            ts.set(key, value);
            ts.exec();
        } finally {
            jedis.close();
        }
    }

    public String get(String key) {
        Jedis jedis = jedisPool.getResource();
        try {
            String result = jedis.get(key);
            return result;
        } finally {
            jedis.close();
        }
    }

    public boolean exists(String key) {
        Jedis jedis = jedisPool.getResource();
        try {
            boolean bool = jedis.exists(key);
            return bool;
        } finally {
            jedis.close();
        }
    }

    public Set<String> keys(String key) {
        Jedis jedis = jedisPool.getResource();
        try {
            Set<String> set = jedis.keys(key);
            return set;
        } finally {
            jedis.close();
        }
    }

    public void delete(String key) {
        Jedis jedis = jedisPool.getResource();
        try {
            jedis.del(key);
        } finally {
            jedis.close();
        }
    }
}
