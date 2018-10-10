package com.candidxd.justchat.util;

import com.candidxd.justchat.bean.Customer;
import com.candidxd.justchat.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @author yzk
 * @Title: CookieUtil
 * @ProjectName justchat
 * @Description: TODO
 * @date 2018/10/104:49 PM
 */
public class CookieUtil {
    public static Customer uuidExist(HttpServletRequest httpServletRequest, CustomerService customerService) {
        Cookie[] cookies = httpServletRequest.getCookies();
        List<Customer> list = customerService.list();
        //Cookie是否存在s
        if (cookies != null) {
            //Cookie中是否已经存储了用户UID
            for (Cookie cookie : cookies) {
                for (Customer customer : list) {
                    //若存在,获取
                    if (cookie.getValue().equals(customer.getUid())) {
                        return customer;
                    }
                }
            }
        }
        return null;
    }
}
