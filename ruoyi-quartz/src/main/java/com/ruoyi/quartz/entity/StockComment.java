package com.ruoyi.quartz.entity;

import lombok.Data;

import java.io.Serializable;

/**
 * @description:
 * @projectName:ruoyi
 * @see:com.ruoyi.quartz.entity
 * @author:
 * @createTime:2021/5/1
 */
@Data
public class StockComment implements Serializable {
    private String avatar;
    private String text;
    private String screenName;
    //0,1,2代表低质、正常、资深股评
    private int flag;
    private String desc;
    //股票代码
    private String symbol;
}
