package com.candidxd.justchat.service;

import com.candidxd.justchat.bean.Customer;
import com.candidxd.justchat.bean.CustomerStatus;
import com.candidxd.justchat.bean.Match;

import java.util.List;

/**
 * @author yzk
 * @Title: CustomerService
 * @ProjectName justchat
 * @Description: TODO
 * @date 2018/9/193:56 PM
 */
public interface MatchService extends BaseService<Match> {
    Match match(Customer customer);
}
