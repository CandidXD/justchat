package com.candidxd.justchat.socket;

import com.candidxd.justchat.bean.*;
import com.candidxd.justchat.logger.BussAnnotation;
import com.candidxd.justchat.rabbitmq.Receiver;
import com.candidxd.justchat.rabbitmq.Sender;
import com.candidxd.justchat.service.CustomerStatusService;
import com.candidxd.justchat.service.MatchService;
import com.candidxd.justchat.util.ThreadPoolUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.AbstractWebSocketHandler;

/**
 * @author yzk
 * @Title: WsHandler
 * @ProjectName justchat
 * @Description: TODO
 * @date 2018/9/282:03 PM
 */
@Component
public class WsHandler extends AbstractWebSocketHandler {

    @Autowired
    private MatchService matchService;
    @Autowired
    private CustomerStatusService customerStatusService;

    private TalkerPool talkerPool = TalkerPool.getTalkerPool();

    /**
     * WebSocket关闭
     *
     * @param session : session
     * @param status  : status
     * @return void
     * @author yaozekai
     * @date 2019-01-08 01:35
     */
    @BussAnnotation(moduleName = "WebSocket", option = "WebSocket关闭")
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        super.afterConnectionClosed(session, status);
        String uuid = session.getUri().toString().substring(11);
        Talker talker = talkerPool.get(uuid);
        String string = session.getUri().toString();
        Customer customer = new Customer();
        customer.setUid(talkerPool.get(uuid).getUid1());
        Match match = matchService.match(customer);
        if (match != null) {
            matchService.delete(match);
            CustomerStatus customerStatus = customerStatusService.getOne(new CustomerStatus().setCustomerUid(string.substring(11)));
            customerStatus.setStateId(1).setState("在线");
            customerStatusService.update(customerStatus);
            talker.getSender().sendMessage(talker.getUid1() + "close");
            customerStatus = customerStatusService.getOne(new CustomerStatus().setCustomerUid(talker.getUid2()));
            customerStatus.setStateId(1).setState("在线");
            customerStatusService.update(customerStatus);
            // talkerPool.get(uuid).getSender().close();
            // talkerPool.get(talkerPool.get(uuid).getUid2()).getSender().close();
            talkerPool.get(uuid).getReceiver().close();
            // talkerPool.get(talkerPool.get(uuid).getUid2()).getReceiver().close();
            // talkerPool.del(talkerPool.get(uuid).getUid2());
            // talkerPool.del(uuid);
            System.out.println("close....");
        }
    }

    /**
     * WebSocket连接成功
     *
     * @param session : session
     * @return void
     * @author yaozekai
     * @date 2019-01-08 01:36
     */
    @BussAnnotation(moduleName = "WebSocket", option = "WebSocket连接成功")
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        String s = session.getUri().toString();

        Talker talker = new Talker();
        talker.setUid1(s.substring(11));
        // synchronized (this) {
        Match match = matchService.match(new Customer().setUid(s.substring(11)));
        if (match.getCustomerUid1().equals(talker.getUid1())) {
            talker.setUid2(match.getCustomerUid2());
        } else {
            talker.setUid2(match.getCustomerUid1());
        }
        talker.setSender(new Sender(talker.getUid1()));
        talker.setReceiver(new Receiver(talker.getUid2(), session));
        talker.setSession(session);
        talkerPool.put(talker);
        ThreadPoolUtil.threadStartRunnable(talker.getReceiver());
        // }
    }

    /**
     * WebSocket消息接收
     *
     * @param session : session
     * @param message : message
     * @return void
     * @author yaozekai
     * @date 2019-01-08 01:37
     */
    @BussAnnotation(moduleName = "WebSocket", option = "WebSocket消息接收")
    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String uuid = session.getUri().toString().substring(11);
        String msg = message.getPayload();
        talkerPool.get(uuid).getSender().sendMessage(msg);
    }

    @Override
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
        super.handleMessage(session, message);
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        super.handleTransportError(session, exception);
    }

}