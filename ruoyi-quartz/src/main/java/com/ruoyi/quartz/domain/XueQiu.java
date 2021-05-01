package com.ruoyi.quartz.domain;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;

/**
 * @description:
 * @projectName:ruoyi
 * @see:com.ruoyi.quartz.domain
 * @author: jijiajin
 * @createTime:2021/5/1
 */
@Document("seven_xueqiu")
@Data
public class XueQiu implements Serializable {
    private Long fav_count;
    private Long like_count;
    private Long reply_count;
    private Long retweet_count;
    private Long view_count;
    private Long followers_count;
    private Long friends_count;
    private Long status_count;
    private Boolean verified_info;
    private String screen_name;
    private String profile_image_url;
    private String text;
    private String spiderDate;
    private String symbol;
}
