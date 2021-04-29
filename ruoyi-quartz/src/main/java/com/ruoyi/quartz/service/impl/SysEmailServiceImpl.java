package com.ruoyi.quartz.service.impl;

import com.ruoyi.quartz.entity.MailEntity;
import com.ruoyi.quartz.service.ISysEmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

/**
 * @description:
 * @projectName:ruoyi
 * @see:com.ruoyi.quartz.service.impl
 * @author:
 * @createTime:2021/4/29
 */
@Service
public class SysEmailServiceImpl implements ISysEmailService {

    @Value("${spring.mail.username}")
    private String from;

    @Autowired
    private JavaMailSender javaMailSender;

    @Override
    public void sendMail(MailEntity mailEntity) {
        SimpleMailMessage mailMessage=new SimpleMailMessage();
        mailMessage.setFrom(from);
        mailMessage.setTo("13078163530@163.com");
        mailMessage.setSubject("测试邮件");
        mailMessage.setText("推荐股票数据");
        try {
            javaMailSender.send(mailMessage);
        }catch (Exception e){
            e.printStackTrace();
            System.out.println("发送简单邮件失败");
        }
    }
}
