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
import com.candidxd.justchat.util.DateTimeUtil;
import com.candidxd.justchat.util.IpUtil;
import com.candidxd.justchat.util.JsonUtil;
import com.candidxd.justchat.util.ThreadPoolUtil;
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
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeoutException;

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

    private static Integer COOKIE_AGE = 60 * 60 * 3;

    private ServerSocket serverSocket = null;

    private List<CustomerStatus> list = null;


    /**
     * 修改密码的最大时效
     */
    private static Integer MAX_TIME = 3 * 60000;

    @BussAnnotation(moduleName = "聊天界面", option = "显示聊天界面")
    @RequestMapping("/list")
    public String list() {
        return getUrl("list");
    }

    @BussAnnotation(moduleName = "聊天界面", option = "用户信息存储")
    @RequestMapping(value = "/userInfo", produces = "application/json; charset=utf-8")
    @ResponseBody
    public String userInfo(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws Exception {
        Cookie[] cookies = httpServletRequest.getCookies();
        Customer customer = null;
        String data = null;
        //Cookie是否存在s
        if (cookies != null) {
            List<Customer> list = customerService.list();
            //Cookie中是否已经存储了用户UID
            for (Cookie cookie : cookies) {
                for (Customer c : list) {
                    //若存在,获取
                    if (cookie.getValue().equals(c.getUid())) {
                        customer = c;
                        break;
                    }
                }
            }
        }
        //如果用户UID不存在Cookie中
        if (customer == null) {
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
        //设置用户默认性别 0.男
        customerStatus.setCustomerGender(0);
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
        CustomerStatus customerStatus = null;
        Cookie[] cookies = httpServletRequest.getCookies();
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
                        customerStatus.setStateId(2);
                        customerStatus.setState("匹配中");
                        customerStatus.setCustomerGender(bean.getGender());
                        customerStatusService.update(customerStatus);
                        break;
                    }
                }
            }
        }
        if (customer != null) {
            //用户年龄更新
            customer.setAge(bean.getAge());
            //用户性别更新
            customer.setGender(bean.getGender());
            //用户信息更新
            customerService.update(customer);


            MatchRunnable runnable = new MatchRunnable(customerStatus);
            runnable.setName(customer.getUid());
//            FutureTask<String> result = new FutureTask<>(runnable);
            String result = ThreadPoolUtil.threadStartCallable(runnable);
            if (result != null) {
                data = result;
//                //用户状态更新
//                customerStatus.setState("在线");
//                //用户状态码更新 1.在线
//                customerStatus.setStateId(1);
//                //用户状态信息更新
//                customerStatusService.update(customerStatus);
            }
        }
        return data;
    }

    class MatchRunnable implements Callable {

        private Match match = new Match();

        private String name;

        private List<CustomerStatus> list = null;

        private CustomerStatus customerStatus = null;

        private String data = null;

        public void setName(String name) {
            this.name = name;
        }

        public MatchRunnable(CustomerStatus customerStatus) {
            this.customerStatus = customerStatus;
        }

        @Override
        public Object call() throws Exception {
            System.out.println("线程:" + name + "     开启");
//            synchronized (this) {
            list = customerStatusService.matching(customerStatus);
            while (data == null) {
                System.out.println(name + "   循环开始");
//                synchronized (this){
//                    list = customerStatusService.matching(customerStatus);
//                }
                if (list.size() == 0) {
                    Customer customer = new Customer();
                    customer.setUid(customerStatus.getCustomerUid());
                    Match match = matchService.match(customer);
                    if (match != null) {
                        customer.setUid(match.getCustomerUid1());
                        //用户状态更新
                        customerStatus.setState("聊天中");
                        //用户状态码更新 1.在线
                        customerStatus.setStateId(3);
                        //用户状态信息更新
                        customerStatusService.update(customerStatus);
                        data = JsonUtil.getDataJson(String.valueOf(ReturnEnum.SUCCESS), JSONObject.toJSONString(JSON.toJSON(customerService.getOne(customer))));
                    }
                } else {
                    match.setCustomerUid1(customerStatus.getCustomerUid());
                    match.setCustomerUid2(list.get(0).getCustomerUid());
                    match.setCreateTime(DateTimeUtil.now());
                    match.setEndTime(null);
                    synchronized (this) {
                        System.out.println("同步开始");
                        list = customerStatusService.matching(customerStatus);
                        if (list.size() > 0) {
                            matchService.add(match);
                            //用户状态更新
                            customerStatus.setState("聊天中");
                            //用户状态码更新 1.在线
                            customerStatus.setStateId(3);
                            //用户状态信息更新
                            customerStatusService.update(customerStatus);

                            Customer bean = new Customer();
                            bean.setUid(match.getCustomerUid2());
                            data = JsonUtil.getDataJson(String.valueOf(ReturnEnum.SUCCESS), JSONObject.toJSONString(JSON.toJSON(customerService.getOne(bean))));

                        }
                        System.out.println("同步结束");
                    }
                }
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println(name + "   循环结束");
            }
//            }
            System.out.println("线程:" + name + "     结束");
            return data;
        }
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
