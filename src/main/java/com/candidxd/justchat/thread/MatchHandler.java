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

import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author yzk
 * @Title: MatchHandler
 * @ProjectName justchat
 * @Description: 匹配线程执行任务
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

    /**
     * Run
     *
     * @param name ：用户UID
     * @param lock ：lock
     * @return java.lang.String
     * @author yaozekai
     * @date 2018/10/12 9:43 AM
     */
    public String run(String name, ReentrantLock lock) {
        System.out.println("线程:" + name + "     开启");
        String data = null;
        synchronized (lock) {
            System.out.println("=======LOCK======");
            CustomerStatus customerStatus = customerStatusService.getOne(new CustomerStatus().setCustomerUid(name));
            if (customerStatus.getStateId() == 1 || customerStatus.getStateId() == 2) {
                customerStatus.setStateId(2).setState("匹配中");
                customerStatusService.update(customerStatus);
            } else if (customerStatus.getStateId() == 3) {
                Match match = matchService.match(new Customer().setUid(name));
                if (match != null) {
                    Customer customer = new Customer().setUid(match.getCustomerUid1());
                    System.out.println("++++++++++++++++++++++++++++++++++++++++++++++++++++++");
                    System.out.println(customerService.getOne(customer).toString());
                    System.out.println("++++++++++++++++++++++++++++++++++++++++++++++++++++++");
                    data = JsonUtil.getDataJson(String.valueOf(ReturnEnum.SUCCESS), JSONObject.toJSONString(JSON.toJSON(customerService.getOne(customer))));
                    System.out.println("线程:" + name + "     结束");
                    return data;
                }
            }
            // else if (customerStatus.getStateId() == 4) {
            //     data = "break";
            //     customerStatus.setStateId(1).setState("在线");
            //     customerStatusService.update(customerStatus);
            //     System.out.println("线程:" + name + "     结束");
            //     return data;
            // }
        }

        List<CustomerStatus> list = null;
        CustomerStatus customerStatus = customerStatusService.getOne(new CustomerStatus().setCustomerUid(name));
        list = customerStatusService.matching(customerStatus);
        if (list.size() == 0) {
            Match match = matchService.match(new Customer().setUid(name));
            if (match != null) {
                //用户状态更新
                customerStatus.setStateId(3).setState("聊天中");
                customerStatusService.update(customerStatus);
                Customer customer = new Customer().setUid(match.getCustomerUid1());
                System.out.println("++++++++++++++++++++++++++++++++++++++++++++++++++++++");
                System.out.println(customerService.getOne(customer));
                System.out.println("++++++++++++++++++++++++++++++++++++++++++++++++++++++");
                data = JsonUtil.getDataJson(String.valueOf(ReturnEnum.SUCCESS), JSONObject.toJSONString(JSON.toJSON(customerService.getOne(customer))));
                System.out.println("线程:" + name + "     结束");
                return data;
            }

            if (data == null) {
                while (matchService.match(new Customer().setUid(name)) == null) {
                    try {
                        System.out.println("线程:" + name + "     sleep");
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    customerStatus = customerStatusService.getOne(new CustomerStatus().setCustomerUid(name));
                    if (customerStatus.getStateId() == 4) {
                        data = "break";
                        customerStatus.setStateId(1).setState("在线");
                        customerStatusService.update(customerStatus);
                        System.out.println("线程:" + name + "     结束");
                        return data;
                    }
                }
            }


        } else {
            Match m = new Match()
                    .setCustomerUid1(customerStatus.getCustomerUid())
                    .setCustomerUid2(list.get(0).getCustomerUid())
                    .setCreateTime(DateTimeUtil.now())
                    .setEndTime("1970-01-01 00:00:00");

            matchService.add(m);
            //用户状态更新
            customerStatus.setState("聊天中").setStateId(3);
            customerStatusService.update(customerStatus);

            CustomerStatus cs = customerStatusService.getOne(customerStatus.setCustomerUid(m.getCustomerUid2()));
            //用户状态更新
            cs.setState("聊天中").setStateId(3);
            //用户状态信息更新
            customerStatusService.update(cs);

            Customer bean = new Customer().setUid(m.getCustomerUid2());
            System.out.println("++++++++++++++++++++++++++++++++++++++++++++++++++++++");
            System.out.println(customerService.getOne(bean).toString());
            System.out.println("++++++++++++++++++++++++++++++++++++++++++++++++++++++");
            data = JsonUtil.getDataJson(String.valueOf(ReturnEnum.SUCCESS), JSONObject.toJSONString(JSON.toJSON(customerService.getOne(bean))));
            System.out.println("线程:" + name + "     结束");
            return data;
//        }
        }
        System.out.println("线程:" + name + "     结束");
        return data;
    }
}
