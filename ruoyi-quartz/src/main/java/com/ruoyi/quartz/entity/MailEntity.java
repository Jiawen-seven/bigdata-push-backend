package com.ruoyi.quartz.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @description:
 * @projectName:ruoyi
 * @see:com.ruoyi.quartz.entity
 * @author:
 * @createTime:2021/4/29
 */
@Data
public class MailEntity implements Serializable {
    //股票代码
    private String symbol;
    //股票信息
    private String content;
}
