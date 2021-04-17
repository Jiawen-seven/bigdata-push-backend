package com.ruoyi.quartz.entity;

import lombok.Data;

import java.io.Serializable;

@Data
public class FundRanking implements Serializable,Comparable<FundRanking> {
    private String symbol;
    private String name;
    private String time;
    /** 涨跌幅 */
    private String firstPercent;

    @Override
    public int compareTo(FundRanking o) {
        return (int) (Double.parseDouble(this.getFirstPercent())-Double.parseDouble(o.getFirstPercent()));
    }

}
