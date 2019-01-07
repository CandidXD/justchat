package com.candidxd.justchat.rabbitmq;

import org.springframework.util.SerializationUtils;

import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;

/**
 * @description: TODO
 * @auther: yaozekai
 * @date: 2019-01-06 03:00
 */
public class Sender extends Entity {

    /**
     * 构造，初始化RMQ
     *
     * @param queueName
     * @return
     * @author yaozekai
     * @date 2019-01-05 02:36
     */
    public Sender(String queueName) throws IOException {
        super(queueName);
    }


    public void sendMessage(String msg) throws IOException {
        HashMap message = new HashMap();
        message.put("message", msg);
        Serializable s = message;
        channel.basicPublish("", queueName, null, SerializationUtils.serialize(s));
    }
}
