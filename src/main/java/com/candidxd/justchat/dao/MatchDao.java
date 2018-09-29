package com.candidxd.justchat.dao;

import com.candidxd.justchat.bean.Customer;
import com.candidxd.justchat.bean.CustomerStatus;
import com.candidxd.justchat.bean.Match;

/**
 * @author yzk
 * @Title: CustomerStatusDao
 * @ProjectName justchat
 * @Description: TODO
 * @date 2018/9/193:49 PM
 */
public interface MatchDao extends BaseDao<Match> {
    Match match(Customer customer);
}
