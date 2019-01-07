package com.candidxd.justchat.rabbitmq;

import com.candidxd.justchat.bean.Talker;
import com.candidxd.justchat.bean.TalkerPool;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.Envelope;
import com.rabbitmq.client.ShutdownSignalException;
import org.springframework.util.SerializationUtils;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @description: TODO
 * @auther: yaozekai
 * @date: 2019-01-06 03:28
 */
public class Receiver extends Entity implements Runnable, Consumer {
    private WebSocketSession socketSession;

    /**
     * 构造，初始化RMQ
     *
     * @param queueName
     * @return
     * @author yaozekai
     * @date 2019-01-05 02:36
     */
    public Receiver(String queueName, WebSocketSession socketSession) throws IOException {
        super(queueName);
        this.socketSession = socketSession;
    }

    @Override
    public void handleConsumeOk(String consumerTag) {
        System.out.println(socketSession.getUri());
        System.out.println("Consumer " + consumerTag + " registered");
    }

    @Override
    public void handleCancelOk(String consumerTag) {

    }

    @Override
    public void handleCancel(String consumerTag) throws IOException {

    }

    @Override
    public void handleShutdownSignal(String consumerTag, ShutdownSignalException sig) {

    }

    @Override
    public void handleRecoverOk(String consumerTag) {

    }

    @Override
    public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
        // 消息反序列化
        Map map = (HashMap) SerializationUtils.deserialize(body);
        System.out.println("Message: " + map.get("message") + " received.");
        TalkerPool talkerPool = TalkerPool.getTalkerPool();
        Talker talker = talkerPool.get(socketSession.getUri().toString().substring(11));
        if (map.get("message").equals(talker.getUid2() + "close")) {
            socketSession.close();
            channel.basicAck(envelope.getDeliveryTag(), false);
            close();
        } else {
            TextMessage msg = new TextMessage(map.get("message").toString());
            socketSession.sendMessage(msg);
            channel.basicAck(envelope.getDeliveryTag(), false);
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void run() {
        try {
            channel.basicQos(0, 5, false);
            channel.basicConsume(queueName, false, this);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
