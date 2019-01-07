package com.candidxd.justchat.bean;

/**
 * @author yzk
 * @Title: CustomerStatus
 * @ProjectName justchat
 * @Description: TODO
 * @date 2018/9/227:20 PM
 */
public class CustomerStatus {
    private Integer id;
    private String customerUid;
    private String state;
    /**
     * 登录状态：0.离线 1.在线 2.匹配中 3.聊天中
     */
    private Integer stateId;
    private String loginTime;
//    private Integer customerGender;

    public Integer getId() {
        return id;
    }

    public CustomerStatus setId(Integer id) {
        this.id = id;
        return this;
    }

    public String getCustomerUid() {
        return customerUid;
    }

    public CustomerStatus setCustomerUid(String customerUid) {
        this.customerUid = customerUid;
        return this;
    }

    public String getState() {
        return state;
    }

    public CustomerStatus setState(String state) {
        this.state = state;
        return this;
    }

    public Integer getStateId() {
        return stateId;
    }

    public CustomerStatus setStateId(Integer stateId) {
        this.stateId = stateId;
        return this;
    }

    public String getLoginTime() {
        return loginTime;
    }

    public CustomerStatus setLoginTime(String loginTime) {
        this.loginTime = loginTime;
        return this;
    }

    @Override
    public String toString() {
        return "CustomerStatus{" +
                "id=" + id +
                ", customerUid='" + customerUid + '\'' +
                ", state='" + state + '\'' +
                ", stateId=" + stateId +
                ", loginTime='" + loginTime + '\'' +
                '}';
    }
}
