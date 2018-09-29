package com.candidxd.justchat.service;

import com.candidxd.justchat.bean.Customer;
import com.candidxd.justchat.bean.CustomerStatus;

import java.util.List;

/**
 * @author yzk
 * @Title: CustomerService
 * @ProjectName justchat
 * @Description: TODO
 * @date 2018/9/193:56 PM
 */
public interface CustomerStatusService extends BaseService<CustomerStatus> {
    List<CustomerStatus> matching(CustomerStatus customerStatus);
    CustomerStatus getOne(CustomerStatus customerStatus);
}
