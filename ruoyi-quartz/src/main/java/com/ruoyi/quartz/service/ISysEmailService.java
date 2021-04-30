package com.ruoyi.quartz.service;

import com.ruoyi.quartz.entity.MailEntity;
import com.ruoyi.system.domain.SysUserRegistered;

import java.util.List;

/**
 * @description:
 * @projectName:ruoyi
 * @see:com.ruoyi.quartz.service
 * @author:
 * @createTime:2021/4/29
 */
public interface ISysEmailService {
    void sendMail(List<MailEntity> mailEntityList, List<SysUserRegistered> sysUserRegisteredList);
}
