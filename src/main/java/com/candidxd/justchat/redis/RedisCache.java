package com.candidxd.justchat.redis;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import config.RedisConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jmx.export.naming.KeyNamingStrategy;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.Map;

/**
 * @author yzk
 * @Title: RedisCache
 * @ProjectName justchat
 * @Description: TODO
 * @date 2018/9/264:03 PM
 */
@Component
public class RedisCache {
    @Autowired
    private JedisPool jedisPool;

    private Logger logger = LoggerFactory.getLogger(RedisConfig.class);

    //给某个key设值
    public void set(String key, Object value) {
        Jedis jedis = jedisPool.getResource();
        String bean = JSONObject.toJSONString(JSON.toJSON(value));
        try {
            jedis.set(key, bean);
        } finally {
            logger.info(String.valueOf(jedis));
        }
    }

    //根据key获取value
    public Object get(String key, Class c) {
        Jedis jedis = jedisPool.getResource();
        try {
            String value = jedis.get(key);
            return JSON.parseObject(value, c);
        } finally {
            logger.info(String.valueOf(jedis));
        }
    }

    //根据键值获取value
    public String hashGet(String key, String field) {
        Jedis jedis = jedisPool.getResource();
        try {
            String value = jedis.hget(key, field);
            return value;
        } finally {
            logger.info(String.valueOf(jedis));
        }

    }

    public void hashSet(String key, String field, String value) {
        Jedis jedis = jedisPool.getResource();
        try {

            jedis.hset(key, field, value);
        } finally {
            logger.info(String.valueOf(jedis));
        }

    }


    public Map<String, String> hashAllGet(String key) {
        Jedis jedis = jedisPool.getResource();
        try {
            Map<String, String> map = jedis.hgetAll(key);
            return map;
        } finally {
            logger.info(String.valueOf(jedis));
        }

    }

    //判断key是否存在
    public boolean existKey(String key) {
        Jedis jedis = jedisPool.getResource();
        try {
            return jedis.exists(key);
        } finally {
            logger.info(String.valueOf(jedis));
        }
    }
}
