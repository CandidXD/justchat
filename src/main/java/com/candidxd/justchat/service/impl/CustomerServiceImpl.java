package com.candidxd.justchat.service.impl;import com.candidxd.justchat.bean.Customer;import com.candidxd.justchat.dao.BaseDao;import com.candidxd.justchat.dao.CustomerDao;import com.candidxd.justchat.service.CustomerService;import org.slf4j.Logger;import org.slf4j.LoggerFactory;import org.springframework.beans.factory.annotation.Autowired;import org.springframework.stereotype.Service;/** * @author yzk * @Title: CustomerServiceImpl * @ProjectName demo * @Description: TODO * @date 2018/8/2111:24 */@Servicepublic class CustomerServiceImpl extends BaseServiceImpl<Customer> implements CustomerService {    @Autowired    private CustomerDao customerDao;    @Override    public Logger logger() {        return LoggerFactory.getLogger(CustomerServiceImpl.class);    }    @Override    public BaseDao<Customer> getMainDao() {        return customerDao;    }    @Override    public Customer getOne(Customer customer) {        // System.out.println("===============================================");        // System.out.println(list().toString());        // System.out.println(list().toArray().toString());        // System.out.println("===============================================");        // for (Customer bean:list()){        //     if (bean.getUid().equals(customer.getUid())){        //         return bean;        //     }        // }        return customerDao.getOne(customer);    }}