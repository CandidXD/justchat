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

    public Customer setId(Integer id) {
        this.id = id;
        return this;
    }

    public String getUid() {
        return uid;
    }

    public Customer setUid(String uid) {
        this.uid = uid;
        return this;
    }

    public String getIp() {
        return ip;
    }

    public Customer setIp(String ip) {
        this.ip = ip;
        return this;
    }

    public String getProvince() {
        return province;
    }

    public Customer setProvince(String province) {
        this.province = province;
        return this;
    }

    public String getCity() {
        return city;
    }

    public Customer setCity(String city) {
        this.city = city;
        return this;
    }

    public Integer getGender() {
        return gender;
    }

    public Customer setGender(Integer gender) {
        this.gender = gender;
        return this;
    }

    public Integer getAge() {
        return age;
    }

    public Customer setAge(Integer age) {
        this.age = age;
        return this;
    }

    public String getCreateTime() {
        return createTime;
    }

    public Customer setCreateTime(String createTime) {
        this.createTime = createTime;
        return this;
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
