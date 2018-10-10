package com.candidxd.justchat.socket;

import com.candidxd.justchat.bean.Customer;
import com.candidxd.justchat.bean.CustomerStatus;
import com.candidxd.justchat.bean.Match;
import com.candidxd.justchat.service.CustomerStatusService;
import com.candidxd.justchat.service.MatchService;
import com.candidxd.justchat.util.DateTimeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.AbstractWebSocketHandler;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import javax.servlet.http.HttpServletRequest;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

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
    public Map<String, WebSocketSession> map = new ConcurrentHashMap<>();
    private String uid1;
    private String uid2;

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        super.afterConnectionClosed(session, status);
        String string = session.getUri().toString();
        Customer customer = new Customer();
        customer.setUid(uid1);
        Match match = matchService.match(customer);
        if (match != null) {
            match.setEndTime(DateTimeUtil.now());
            matchService.update(match);
            CustomerStatus o = new CustomerStatus();
            o.setCustomerUid(string.substring(11));
            CustomerStatus customerStatus = customerStatusService.getOne(o);
            customerStatus.setStateId(1);
            customerStatus.setState("在线");
            customerStatusService.update(customerStatus);
            WebSocketSession s = map.get(string.substring(11));
            s.close();
            o.setCustomerUid(s.getUri().toString().substring(11));
            customerStatus = customerStatusService.getOne(o);
            customerStatus.setStateId(1);
            customerStatus.setState("在线");
            customerStatusService.update(customerStatus);
            System.out.println("close....");
        }
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        super.afterConnectionEstablished(session);
        System.out.println("建立新的会话s");
        String s = session.getUri().toString();
        synchronized (this) {
            uid1 = s.substring(11);
            Customer customer = new Customer();
            customer.setUid(uid1);
            Match match = matchService.match(customer);
            if (match.getCustomerUid1().equals(uid1)) {
                uid2 = match.getCustomerUid2();
            } else {
                uid2 = match.getCustomerUid1();
            }
            map.put(uid2, session);
        }
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        TextMessage msg = new TextMessage(message.getPayload());
        String string = session.getUri().toString();
        WebSocketSession s = map.get(string.substring(11));
        System.out.println(message.getPayload());
//        WebSocketSession s2 = map.get(uid1);
        System.out.println(s.getUri());
        s.sendMessage(msg);
//        s2.sendMessage(msg);
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