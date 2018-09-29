package com.candidxd.justchat.dao;

import com.candidxd.justchat.bean.Customer;

/**
 * @author yzk
 * @Title: CustomerDao
 * @ProjectName justchat
 * @Description: TODO
 * @date 2018/9/193:49 PM
 */
public interface CustomerDao extends BaseDao<Customer> {
    Customer getOne(Customer customer);
}
