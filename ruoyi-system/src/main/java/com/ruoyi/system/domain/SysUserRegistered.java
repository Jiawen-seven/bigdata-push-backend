package com.ruoyi.system.domain;

import java.io.Serializable;
import java.util.Arrays;
import java.util.StringJoiner;

/*
* 注册表
* */
public class SysUserRegistered implements Serializable{
    private String name;
    private String phone;
    private String password;
    private String email;
    private Long userId;
    private String stockType;
    private String stockMessages;
    private String stockTime;
    private String stockCount;
    private String stockReminds;
    private Long[] stockMessage;
    private Long[] stockRemind;
    private String remark;
    private String avatar;

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getStockType() {
        return stockType;
    }

    public void setStockType(String stockType) {
        this.stockType = stockType;
    }

    public String getStockMessages() {
        return stockMessages;
    }

    public void setStockMessages(String stockMessages) {
        this.stockMessages = stockMessages;
    }

    public String getStockTime() {
        return stockTime;
    }

    public void setStockTime(String stockTime) {
        this.stockTime = stockTime;
    }

    public String getStockCount() {
        return stockCount;
    }

    public void setStockCount(String stockCount) {
        this.stockCount = stockCount;
    }

    public String getStockReminds() {
        return stockReminds;
    }

    public void setStockReminds(String stockReminds) {
        this.stockReminds = stockReminds;
    }

    public Long[] getStockMessage() {
        return stockMessage;
    }

    public void setStockMessage(Long[] stockMessage) {
        this.stockMessage = stockMessage;
    }

    public Long[] getStockRemind() {
        return stockRemind;
    }

    public void setStockRemind(Long[] stockRemind) {
        this.stockRemind = stockRemind;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", SysUserRegistered.class.getSimpleName() + "[", "]")
                .add("name='" + name + "'")
                .add("phone='" + phone + "'")
                .add("password='" + password + "'")
                .add("email='" + email + "'")
                .add("userId=" + userId)
                .add("stockType='" + stockType + "'")
                .add("stockMessages='" + stockMessages + "'")
                .add("stockTime='" + stockTime + "'")
                .add("stockCount='" + stockCount + "'")
                .add("stockReminds='" + stockReminds + "'")
                .add("stockMessage=" + Arrays.toString(stockMessage))
                .add("stockRemind=" + Arrays.toString(stockRemind))
                .add("remark='" + remark + "'")
                .add("avatar='" + avatar + "'")
                .toString();
    }
}
