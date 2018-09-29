package com.candidxd.justchat.bean;

/**
 * @author yzk
 * @Title: Message
 * @ProjectName justchat
 * @Description: TODO
 * @date 2018/9/275:24 PM
 */
public class Message {
    private String sender;
    private String receiver;
    private String message;

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
