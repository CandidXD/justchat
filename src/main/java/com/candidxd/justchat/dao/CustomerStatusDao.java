package com.candidxd.justchat.dao;

import com.candidxd.justchat.bean.Customer;
import com.candidxd.justchat.bean.CustomerStatus;

import java.util.List;

/**
 * @author yzk
 * @Title: CustomerStatusDao
 * @ProjectName justchat
 * @Description: TODO
 * @date 2018/9/193:49 PM
 */
public interface CustomerStatusDao extends BaseDao<CustomerStatus> {
    List<CustomerStatus> matching(CustomerStatus customerStatus);
    CustomerStatus getOne(CustomerStatus customerStatus);
}
