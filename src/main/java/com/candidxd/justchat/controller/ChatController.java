package com.candidxd.justchat.controller;

import com.alibaba.fastjson.JSONObject;
import com.candidxd.justchat.bean.Customer;
import com.candidxd.justchat.bean.CustomerStatus;
import com.candidxd.justchat.enums.ReturnEnum;
import com.candidxd.justchat.logger.BussAnnotation;
import com.candidxd.justchat.service.CustomerService;
import com.candidxd.justchat.service.CustomerStatusService;
import com.candidxd.justchat.service.MatchService;
import com.candidxd.justchat.thread.MatchCallable;
import com.candidxd.justchat.thread.MatchHandler;
import com.candidxd.justchat.util.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.UUID;
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
    private final CustomerService customerService;

    private final CustomerStatusService customerStatusService;

    private final MatchService matchService;

    private final MatchHandler matchHandler;

    private static Integer COOKIE_AGE = 60 * 60 * 24;

    private final ReentrantLock lock = new ReentrantLock();

    @Autowired
    public ChatController(CustomerService customerService, CustomerStatusService customerStatusService, MatchService matchService, MatchHandler matchHandler) {
        this.customerService = customerService;
        this.customerStatusService = customerStatusService;
        this.matchService = matchService;
        this.matchHandler = matchHandler;
    }

    /**
     * 显示聊天界面
     *
     * @return java.lang.String
     * @author yaozekai
     * @date 2018/10/11 8:10 PM
     */

    @BussAnnotation(moduleName = "聊天界面", option = "显示聊天界面")
    @RequestMapping("/list")
    public String list() {
        return getUrl("list");
    }

    /**
     * 用户信息存储
     *
     * @param httpServletRequest  ：httpServletRequest
     * @param httpServletResponse ：httpServletResponse
     * @return java.lang.String
     * @author yaozekai
     * @date 2018/10/11 8:10 PM
     */
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
            //从Baidu API获取到的用户所在省市信息
            JSONObject jsonObject = IpUtil.baiduIpApi(IpUtil.getIp(httpServletRequest));
            //设置用户UID
            customer.setUid(uuid)
                    //设置用户IP
                    .setIp(IpUtil.getIp(httpServletRequest))
                    //设置用户所在省
                    .setProvince(jsonObject.getString("province"))
                    //设置用户所在市
                    .setCity(jsonObject.getString("city"))
                    //设置用户默认年龄 0.小于18
                    .setAge(0)
                    //设置用户默认性别 0.男
                    .setGender(0)
                    //设置用户创建时间
                    .setCreateTime(DateTimeUtil.now());
            //添加用户
            customerService.add(customer);
            //创建新的Cookie存储用户的UID
            Cookie cookie = new Cookie(uuid, uuid);
            cookie.setMaxAge(COOKIE_AGE);
            httpServletResponse.addCookie(cookie);
        }

        //创建新的用户状态
        CustomerStatus customerStatus = new CustomerStatus();
        //设置用户UID
        customerStatus.setCustomerUid(customer.getUid())
                //设置用户登陆时间
                .setLoginTime(DateTimeUtil.now())
                //设置状态码 1.在线
                .setStateId(1)
                //设置状态
                .setState("在线");
        //如果用户状态不存在，添加用户状态，否则更新用户状态
        if (customerStatusService.getOne(customerStatus) == null) {
            customerStatusService.add(customerStatus);
        } else {
            customerStatusService.update(customerStatus);
        }
        //返回数据给前端
        data = JsonUtil.getData(String.valueOf(ReturnEnum.SUCCESS), "登陆成功");
        // match(customer, httpServletRequest, httpServletResponse);
        return data;
    }

    /**
     * 用户匹配
     *
     * @param bean                : customer
     * @param httpServletRequest  : httpServletRequest
     * @param httpServletResponse : httpServletResponse
     * @return java.lang.String
     * @author yaozekai
     * @date 2019-01-05 20:07
     */
    @BussAnnotation(moduleName = "聊天界面", option = "用户匹配")
    @RequestMapping(value = "/match", produces = "application/json; charset=utf-8")
    @ResponseBody
    public String match(Customer bean, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws Exception {
        String data = null;
        Customer customer = bean;

        //判断用户信息是否已经存在Cookie中
        if (CookieUtil.uuidExist(httpServletRequest, customerService) != null) {
            customer = CookieUtil.uuidExist(httpServletRequest, customerService);

            customer = customerService.getOne(customer);

            System.out.println(customer.toString());
            //用户信息更新
            customer.setAge(bean.getAge())
                    .setGender(bean.getGender());
            customerService.update(customer);

            //创建匹配线程
            String result = ThreadPoolUtil.threadStartCallable(new MatchCallable(matchHandler, lock, matchService).setName(customer.getUid()));
            if (result != null) {
                data = result;
                if ("break".equals(data)) {
                    data = JsonUtil.getData(String.valueOf(ReturnEnum.ERROR), "");
                }
            }
        }

        return data;
    }

    /**
     * 取消匹配
     *
     * @param httpServletRequest  ：httpServletRequest
     * @param httpServletResponse ：httpServletResponse
     * @return java.lang.String
     * @author yaozekai
     * @date 2018/10/10 4:51 PM
     */
    @BussAnnotation(moduleName = "聊天界面", option = "取消匹配")
    @RequestMapping(value = "/cancelmatch", produces = "application/json; charset=utf-8")
    @ResponseBody
    public String cancelmatch(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
        String data = null;

        if (CookieUtil.uuidExist(httpServletRequest, customerService) != null) {
            Customer c = CookieUtil.uuidExist(httpServletRequest, customerService);
            CustomerStatus cs = new CustomerStatus();
            cs.setCustomerUid(c.getUid());
            //用户状态更新
            CustomerStatus customerStatus = customerStatusService.getOne(cs);
            customerStatus.setStateId(4)
                    .setState("取消匹配");
            synchronized (lock) {
                System.out.println("=======CANCEL LOCK======");
                customerStatusService.update(customerStatus);
                System.out.println("=======CANCEL END======");
            }
            data = JsonUtil.getData(String.valueOf(ReturnEnum.SUCCESS), "匹配已取消！");
        }

        return data;
    }

    /**
     * URL
     *
     * @param string ：string
     * @return java.lang.String
     * @author yaozekai
     * @date 2018/10/11 8:09 PM
     */
    private String getUrl(String string) {
        return "chat/" + string;
    }
}
