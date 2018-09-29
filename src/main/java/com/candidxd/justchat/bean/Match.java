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

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCustomerUid1() {
        return customerUid1;
    }

    public void setCustomerUid1(String customerUid1) {
        this.customerUid1 = customerUid1;
    }

    public String getCustomerUid2() {
        return customerUid2;
    }

    public void setCustomerUid2(String customerUid2) {
        this.customerUid2 = customerUid2;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
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
