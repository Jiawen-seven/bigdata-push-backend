package com.ruoyi.system.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;
import com.ruoyi.common.core.domain.entity.SysUser;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.Date;


public class SysUserPush extends BaseEntity {
    private static final long serialVersionUID = 1L;

    public SysUserPush(){}

    public SysUserPush(SysUser sysUser) {
        this.userId = sysUser.getUserId();
        this.loginDate = sysUser.getLoginDate();
        this.loginCount = sysUser.getLoginCount();
        this.setCreateTime(sysUser.getCreateTime());
    }

    @Excel(name = "用户序号", cellType = Excel.ColumnType.NUMERIC, prompt = "用户编号")
    private Long userId;
    /** 最后登录时间 */
    @Excel(name = "最后登录时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss", type = Excel.Type.EXPORT)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date loginDate;
    @Excel(name = "登录次数", cellType = Excel.ColumnType.NUMERIC, prompt = "登录次数")
    private Integer loginCount;
    @Excel(name = "用户活跃度")
    private Double userActivity;
    @Excel(name = "距离最后登录/天")
    private Integer days;

    private Boolean recall;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Date getLoginDate() {
        return loginDate;
    }

    public void setLoginDate(Date loginDate) {
        this.loginDate = loginDate;
    }

    public Integer getLoginCount() {
        return loginCount;
    }

    public void setLoginCount(Integer loginCount) {
        this.loginCount = loginCount;
    }

    public Double getUserActivity() {
        return userActivity;
    }

    public void setUserActivity(Double userActivity) {
        this.userActivity = userActivity;
    }

    public Integer getDays() {
        return days;
    }

    public void setDays(Integer days) {
        this.days = days;
    }

    public Boolean getRecall() {
        return recall;
    }

    public void setRecall(Boolean recall) {
        this.recall = recall;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("userId", userId)
                .append("loginDate", loginDate)
                .append("loginCount", loginCount)
                .append("userActivity", userActivity)
                .append("days", days)
                .append("recall", recall)
                .toString();
    }
}
