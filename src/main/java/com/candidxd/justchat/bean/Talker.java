package com.candidxd.justchat.bean;

import com.candidxd.justchat.rabbitmq.Receiver;
import com.candidxd.justchat.rabbitmq.Sender;
import org.springframework.web.socket.WebSocketSession;

/**
 * @description: TODO
 * @auther: yaozekai
 * @date: 2019-01-06 04:48
 */
public class Talker {
    // 自己的UUID
    private String uid1;
    // 对方的UUID
    private String uid2;
    private Sender sender;
    private Receiver receiver;
    private WebSocketSession session;

    public String getUid1() {
        return uid1;
    }

    public void setUid1(String uid1) {
        this.uid1 = uid1;
    }

    public String getUid2() {
        return uid2;
    }

    public void setUid2(String uid2) {
        this.uid2 = uid2;
    }

    public Sender getSender() {
        return sender;
    }

    public void setSender(Sender sender) {
        this.sender = sender;
    }

    public Receiver getReceiver() {
        return receiver;
    }

    public void setReceiver(Receiver receiver) {
        this.receiver = receiver;
    }

    public WebSocketSession getSession() {
        return session;
    }

    public void setSession(WebSocketSession session) {
        this.session = session;
    }

    @Override
    public String toString() {
        return "Talker{" +
                "uid1='" + uid1 + '\'' +
                ", uid2='" + uid2 + '\'' +
                ", sender=" + sender +
                ", receiver=" + receiver +
                ", session=" + session +
                '}';
    }
}
