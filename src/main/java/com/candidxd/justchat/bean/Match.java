package com.candidxd.justchat.bean;

/**
 * @author yzk
 * @Title: Match
 * @ProjectName justchat
 * @Description: TODO
 * @date 2018/9/236:25 PM
 */
public class Match {
    private Integer id;
    private String customerUid1;
    private String customerUid2;
    private String createTime;
    private String endTime;

    public Integer getId() {
        return id;
    }

    public Match setId(Integer id) {
        this.id = id;
        return this;
    }

    public String getCustomerUid1() {
        return customerUid1;
    }

    public Match setCustomerUid1(String customerUid1) {
        this.customerUid1 = customerUid1;
        return this;
    }

    public String getCustomerUid2() {
        return customerUid2;
    }

    public Match setCustomerUid2(String customerUid2) {
        this.customerUid2 = customerUid2;
        return this;
    }

    public String getCreateTime() {
        return createTime;
    }

    public Match setCreateTime(String createTime) {
        this.createTime = createTime;
        return this;
    }

    public String getEndTime() {
        return endTime;
    }

    public Match setEndTime(String endTime) {
        this.endTime = endTime;
        return this;
    }

    @Override
    public String toString() {
        return "Match{" +
                "id=" + id +
                ", customerUid1='" + customerUid1 + '\'' +
                ", customerUid2='" + customerUid2 + '\'' +
                ", createTime='" + createTime + '\'' +
                ", endTime='" + endTime + '\'' +
                '}';
    }
}
