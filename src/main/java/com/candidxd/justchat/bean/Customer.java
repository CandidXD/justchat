package com.candidxd.justchat.bean;

/**
 * @author yzk
 * @Title: Customer
 * @ProjectName justchat
 * @Description: 用户模型
 * @date 2018/9/193:33 PM
 */
public class Customer {
    private Integer id;
    private String uid;
    private String ip;
    private String province;
    private String city;
    private Integer gender;
    private Integer age;
    private String createTime;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public Integer getGender() {
        return gender;
    }

    public void setGender(Integer gender) {
        this.gender = gender;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    @Override
    public String toString() {
        return "Customer{" +
                "id=" + id +
                ", uid='" + uid + '\'' +
                ", ip='" + ip + '\'' +
                ", province='" + province + '\'' +
                ", city='" + city + '\'' +
                ", gender=" + gender +
                ", age=" + age +
                ", createTime='" + createTime + '\'' +
                '}';
    }
}
