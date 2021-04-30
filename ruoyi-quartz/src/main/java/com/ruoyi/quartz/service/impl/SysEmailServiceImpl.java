package com.ruoyi.quartz.service.impl;

import com.ruoyi.quartz.entity.MailEntity;
import com.ruoyi.quartz.service.ISysEmailService;
import com.ruoyi.system.domain.SysUserRegistered;
import com.ruoyi.system.service.impl.SysUserServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.BodyPart;
import javax.mail.Multipart;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @description:
 * @projectName:ruoyi
 * @see:com.ruoyi.quartz.service.impl
 * @author:
 * @createTime:2021/4/29
 */
@Service
public class SysEmailServiceImpl implements ISysEmailService {
    private static final Logger log = LoggerFactory.getLogger(SysEmailServiceImpl.class);

    @Value("${spring.mail.username}")
    private String from;

    @Autowired
    private JavaMailSender javaMailSender;

    @Override
    public void sendMail(List<MailEntity> mailEntityList, List<SysUserRegistered> sysUserRegisteredList) {
        for(SysUserRegistered sysUserRegistered:sysUserRegisteredList){
            List<MailEntity> mailEntity = mailEntityList.stream()
                    .filter(m->m.getSymbol().equals(sysUserRegistered.getStockType())).collect(Collectors.toList());
            if(mailEntity!=null && mailEntity.size()>0){
                MimeMessage message = javaMailSender.createMimeMessage();
                try {
                    //true表示需要创建一个multipart message
                    MimeMessageHelper helper = new MimeMessageHelper(message, true);
                    helper.setFrom(from);
                    helper.setTo(sysUserRegistered.getEmail());
                    helper.setSubject("今日你赚了没？");
                    helper.setText(mailEntity.get(0).getContent(),true);
                    javaMailSender.send(message);
                }catch (Exception e){
                    log.error(e.getMessage());
                    log.error("发送"+sysUserRegistered.getEmail()+"邮件失败");
                }
            }else{
                log.error("找不到股票数据"+sysUserRegistered.getName());
            }

        }
    }
}
