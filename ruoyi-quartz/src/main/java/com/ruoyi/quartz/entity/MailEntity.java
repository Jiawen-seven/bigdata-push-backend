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
    private List<String> tos;
    private List<String> subjects;
    private List<String> contents;
}
