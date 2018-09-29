package com.candidxd.justchat.service;

import com.candidxd.justchat.bean.Customer;

/**
 * @author yzk
 * @Title: CustomerService
 * @ProjectName justchat
 * @Description: TODO
 * @date 2018/9/193:56 PM
 */
public interface CustomerService extends BaseService<Customer> {
    Customer getOne(Customer customer);
}
