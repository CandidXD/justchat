package com.candidxd.justchat.rabbitmq;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * @description: TODO
 * @auther: yaozekai
 * @date: 2019-01-06 02:57
 */
public abstract class Entity {
    protected Channel channel;
    protected Connection connection;
    protected String queueName;

    /**
     * 构造，初始化RMQ
     *
     * @return
     * @author yaozekai
     * @date 2019-01-05 02:36
     */
    public Entity(String queueName) throws IOException {
        this.queueName = queueName;
        // 创建连接工厂
        ConnectionFactory factory = new ConnectionFactory();

        // 配置rabbitmq
        factory.setHost("localhost");
        factory.setUsername("guest");
        factory.setPassword("guest");
        factory.setPort(5672);

        // 获取连接
        try {
            connection = factory.newConnection();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }

        // 创建通道
        channel = connection.createChannel();
        // 声明一个队列
        channel.queueDeclare(queueName, false, false, true, null);
    }

    /**
     * RMQ通道关闭，连接关闭
     *
     * @return void
     * @author yaozekai
     * @date 2019-01-05 02:38
     */
    public void close() throws IOException {
        try {
            this.channel.close();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }
        this.connection.close();
    }
}
