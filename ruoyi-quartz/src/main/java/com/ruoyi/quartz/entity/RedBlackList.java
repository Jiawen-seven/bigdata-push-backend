package com.ruoyi.quartz.entity;

import lombok.Data;

import java.io.Serializable;

/**
 * @description:
 * @projectName:ruoyi
 * @see:com.ruoyi.quartz.entity
 * @author:
 * @createTime:2021/4/29
 */
@Data
public class RedBlackList implements Serializable,Comparable<RedBlackList> {
    private String name;
    private String symbol;
    private String reason;
    private Integer score;

    @Override
    public int compareTo(RedBlackList o) {
        return o.getScore().compareTo(this.score);
    }
}
