package com.ruoyi.quartz.service;

import com.ruoyi.quartz.entity.MailEntity;

/**
 * @description:
 * @projectName:ruoyi
 * @see:com.ruoyi.quartz.service
 * @author:
 * @createTime:2021/4/29
 */
public interface ISysEmailService {
    void sendMail(MailEntity mailEntity);
}
