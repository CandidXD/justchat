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

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCustomerUid() {
        return customerUid;
    }

    public void setCustomerUid(String customerUid) {
        this.customerUid = customerUid;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public Integer getStateId() {
        return stateId;
    }

    public void setStateId(Integer stateId) {
        this.stateId = stateId;
    }

    public String getLoginTime() {
        return loginTime;
    }

    public void setLoginTime(String loginTime) {
        this.loginTime = loginTime;
    }

//    public Integer getCustomerGender() {
//        return customerGender;
//    }
//
//    public void setCustomerGender(Integer customerGender) {
//        this.customerGender = customerGender;
//    }

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
