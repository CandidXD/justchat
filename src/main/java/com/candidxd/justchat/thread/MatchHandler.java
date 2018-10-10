package com.candidxd.justchat.thread;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.candidxd.justchat.bean.Customer;
import com.candidxd.justchat.bean.CustomerStatus;
import com.candidxd.justchat.bean.Match;
import com.candidxd.justchat.enums.ReturnEnum;
import com.candidxd.justchat.service.CustomerService;
import com.candidxd.justchat.service.CustomerStatusService;
import com.candidxd.justchat.service.MatchService;
import com.candidxd.justchat.util.DateTimeUtil;
import com.candidxd.justchat.util.JsonUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author yzk
 * @Title: MatchHandler
 * @ProjectName justchat
 * @Description: TODO
 * @date 2018/9/301:09 PM
 */
@Component
public class MatchHandler {
    @Autowired
    private CustomerStatusService customerStatusService;

    @Autowired
    private MatchService matchService;

    @Autowired
    private CustomerService customerService;

    synchronized public String run(String name, ReentrantLock lock) {
        System.out.println("线程:" + name + "     开启");
        String data = null;
        synchronized (lock) {
            System.out.println("=======LOCK======");
            CustomerStatus cs = new CustomerStatus();
            cs.setCustomerUid(name);
            CustomerStatus cs2 = new CustomerStatus();
            cs2 = customerStatusService.getOne(cs);
            if (cs2.getStateId() == 1 || cs2.getStateId() == 2) {
                cs2.setStateId(2);
                cs2.setState("匹配中");
                customerStatusService.update(cs2);
            } else if (cs2.getStateId() == 3) {
                Customer customer = new Customer();
                customer.setUid(name);
                Match match = matchService.match(customer);
                if (match != null) {
                    customer.setUid(match.getCustomerUid1());
                    data = JsonUtil.getDataJson(String.valueOf(ReturnEnum.SUCCESS), JSONObject.toJSONString(JSON.toJSON(customerService.getOne(customer))));
                    System.out.println("线程:" + name + "     结束");
                    return data;
                }
            } else if (cs2.getStateId() == 4) {
                data = "break";
                cs2.setStateId(1);
                cs2.setState("在线");
                customerStatusService.update(cs2);
                System.out.println("线程:" + name + "     结束");
                return data;
            }
        }

        List<CustomerStatus> list = null;
        CustomerStatus customerStatus = new CustomerStatus();
        customerStatus.setCustomerUid(name);
        customerStatus = customerStatusService.getOne(customerStatus);
        list = customerStatusService.matching(customerStatus);
        if (list.size() == 0) {
            Customer customer = new Customer();
            customer.setUid(name);
            Match match = matchService.match(customer);
            if (match != null) {
                //用户状态码更新 1.在线
                customerStatus.setStateId(3);
                //用户状态更新
                customerStatus.setState("聊天中");
                customerStatusService.update(customerStatus);
                customer.setUid(match.getCustomerUid1());
                data = JsonUtil.getDataJson(String.valueOf(ReturnEnum.SUCCESS), JSONObject.toJSONString(JSON.toJSON(customerService.getOne(customer))));
                System.out.println("线程:" + name + "     结束");
                return data;
            }
        } else {
            Match m = new Match();
            m.setCustomerUid1(customerStatus.getCustomerUid());
            m.setCustomerUid2(list.get(0).getCustomerUid());
            m.setCreateTime(DateTimeUtil.now());
            m.setEndTime(null);
//            list = customerStatusService.matching(customerStatus);
//            if (list.size() > 0) {
            matchService.add(m);
            //用户状态更新
            customerStatus.setState("聊天中");
            //用户状态码更新 1.在线
            customerStatus.setStateId(3);
            //用户状态信息更新
            customerStatusService.update(customerStatus);
            customerStatus.setCustomerUid(m.getCustomerUid2());
            CustomerStatus c = customerStatusService.getOne(customerStatus);
            //用户状态更新
            c.setState("聊天中");
            //用户状态码更新 1.在线
            c.setStateId(3);
            //用户状态信息更新
            customerStatusService.update(c);

            Customer bean = new Customer();
            bean.setUid(m.getCustomerUid2());
            data = JsonUtil.getDataJson(String.valueOf(ReturnEnum.SUCCESS), JSONObject.toJSONString(JSON.toJSON(customerService.getOne(bean))));
            System.out.println("线程:" + name + "     结束");
            return data;
//        }
        }
        System.out.println("线程:" + name + "     结束");
        return data;
    }
}
