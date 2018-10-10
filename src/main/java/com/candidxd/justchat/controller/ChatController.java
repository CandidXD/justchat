package com.candidxd.justchat.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.candidxd.justchat.bean.Customer;
import com.candidxd.justchat.bean.CustomerStatus;
import com.candidxd.justchat.bean.Match;
import com.candidxd.justchat.enums.ReturnEnum;
import com.candidxd.justchat.logger.BussAnnotation;
import com.candidxd.justchat.service.CustomerService;
import com.candidxd.justchat.service.CustomerStatusService;
import com.candidxd.justchat.service.MatchService;
import com.candidxd.justchat.socket.WsHandler;
import com.candidxd.justchat.thread.MatchCallable;
import com.candidxd.justchat.thread.MatchHandler;
import com.candidxd.justchat.util.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author yzk
 * @Title: ChatController
 * @ProjectName justchat
 * @Description: TODO
 * @date 2018/9/181:40 PM
 */

@Controller
@RequestMapping("chat")
public class ChatController {
    @Autowired
    private CustomerService customerService;

    @Autowired
    private CustomerStatusService customerStatusService;

    @Autowired
    private MatchService matchService;

    @Autowired
    private MatchHandler matchHandler;

    private static Integer COOKIE_AGE = 60 * 60 * 24;

    private ServerSocket serverSocket = null;

    private List<CustomerStatus> list = null;

    private ReentrantLock lock = new ReentrantLock();

    @BussAnnotation(moduleName = "聊天界面", option = "显示聊天界面")
    @RequestMapping("/list")
    public String list() {
        return getUrl("list");
    }

    @BussAnnotation(moduleName = "聊天界面", option = "用户信息存储")
    @RequestMapping(value = "/userInfo", produces = "application/json; charset=utf-8")
    @ResponseBody
    public String userInfo(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws Exception {
        Customer customer = null;
        String data = null;

        //判断用户信息是否已经存在Cookie中
        if (CookieUtil.uuidExist(httpServletRequest, customerService) != null) {
            customer = CookieUtil.uuidExist(httpServletRequest, customerService);
        } else {
            //创建一个新的客户
            customer = new Customer();
            //生成用户的UID
            String uuid = UUID.randomUUID().toString().replace("-", "").toLowerCase();
            //设置用户UID
            customer.setUid(uuid);
            //设置用户IP
            customer.setIp(IpUtil.getIp(httpServletRequest));
            //从Baidu API获取到的用户所在省市信息
            JSONObject jsonObject = IpUtil.baiduIpApi(customer.getIp());
            //设置用户所在省
            customer.setProvince(jsonObject.getString("province"));
            //设置用户所在市
            customer.setCity(jsonObject.getString("city"));
            //设置用户默认年龄 0.小于18
            customer.setAge(0);
            //设置用户默认性别 0.男
            customer.setGender(0);
            //设置用户创建时间
            customer.setCreateTime(DateTimeUtil.now());
            //添加用户
            customerService.add(customer);
            //创建新的Cookie存储用户的UID
            Cookie cookie = new Cookie(uuid, uuid);
            cookie.setMaxAge(COOKIE_AGE);
            cookie.setPath("/");
            httpServletResponse.addCookie(cookie);
        }

        //创建新的用户状态
        CustomerStatus customerStatus = new CustomerStatus();
        //设置用户UID
        customerStatus.setCustomerUid(customer.getUid());
        //设置用户登陆时间
        customerStatus.setLoginTime(DateTimeUtil.now());
        //设置状态码 1.在线
        customerStatus.setStateId(1);
        //设置状态
        customerStatus.setState("在线");
        //如果用户状态不存在，添加用户状态，否则更新用户状态
        if (customerStatusService.getOne(customerStatus) == null) {
            customerStatusService.add(customerStatus);
        } else {
            customerStatusService.update(customerStatus);
        }
        //返回数据给前端
        data = JsonUtil.getData(String.valueOf(ReturnEnum.SUCCESS), "登陆成功");
        return data;
    }

    @BussAnnotation(moduleName = "聊天界面", option = "用户匹配")
    @RequestMapping(value = "/match", produces = "application/json; charset=utf-8")
    @ResponseBody
    public String match(Customer bean, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws Exception {
        String data = null;
        Customer customer = null;

        //判断用户信息是否已经存在Cookie中
        if (CookieUtil.uuidExist(httpServletRequest, customerService) != null) {
            customer = CookieUtil.uuidExist(httpServletRequest, customerService);

            //用户信息更新
            customer.setAge(bean.getAge());
            customer.setGender(bean.getGender());
            customerService.update(customer);

            //创建匹配线程
            MatchCallable runnable = new MatchCallable(matchHandler, lock);
            runnable.setName(customer.getUid());
            String result = ThreadPoolUtil.threadStartCallable(runnable);
            if (result != null) {
                data = result;
                if (data.equals("break")) {
                    data = JsonUtil.getData(String.valueOf(ReturnEnum.ERROR), "");
                }
            }
        } else {
//            data = JsonUtil.getData(String.valueOf(ReturnEnum.ERROR), "");
        }

        return data;
    }

    @BussAnnotation(moduleName = "聊天界面", option = "取消匹配")
    @RequestMapping(value = "/cancelmatch", produces = "application/json; charset=utf-8")
    @ResponseBody
    public String cancelmatch(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
        String data = null;
        Cookie[] cookies = httpServletRequest.getCookies();
        //用户状态更新
        if (cookies != null) {
            List<Customer> list = customerService.list();
            for (Cookie cookie : cookies) {
                for (Customer c : list) {
                    if (cookie.getValue().equals(c.getUid())) {
                        synchronized (lock) {
                            System.out.println("=======CANCEL LOCK======");
                            CustomerStatus cs = new CustomerStatus();
                            cs.setCustomerUid(c.getUid());
                            CustomerStatus customerStatus = customerStatusService.getOne(cs);
                            customerStatus.setStateId(4);
                            customerStatus.setState("取消匹配");
                            customerStatusService.update(customerStatus);
                            data = JsonUtil.getData(String.valueOf(ReturnEnum.SUCCESS), "匹配已取消！");
                            System.out.println("=======CANCEL END======");
                            break;
                        }
                    }
                }
            }
        }
        return data;
    }

    @RequestMapping(value = "/out")
    @ResponseBody
    public String test(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
        String data = null;
        Customer customer = null;
        Cookie[] cookies = httpServletRequest.getCookies();
        CustomerStatus customerStatus = null;
        //用户状态更新
        if (cookies != null) {
            List<Customer> list = customerService.list();
            for (Cookie cookie : cookies) {
                for (Customer c : list) {
                    if (cookie.getValue().equals(c.getUid())) {
                        customer = c;
                        CustomerStatus cs = new CustomerStatus();
                        cs.setCustomerUid(customer.getUid());
                        customerStatus = customerStatusService.getOne(cs);
                        customerStatus.setStateId(0);
                        customerStatus.setState("离线");
                        customerStatusService.update(customerStatus);
                        break;
                    }
                }
            }
        }
        data = JsonUtil.getData(String.valueOf(ReturnEnum.SUCCESS), "退出成功！");
        return data;
    }

    private String getUrl(String string) {
        return "chat/" + string;
    }
}
