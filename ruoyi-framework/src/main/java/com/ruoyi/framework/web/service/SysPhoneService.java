package com.ruoyi.framework.web.service;

import com.alibaba.fastjson.JSONObject;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.redis.RedisCache;
import com.zhenzi.sms.ZhenziSmsClient;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Component
public class SysPhoneService {

    @Autowired
    ZhenziSmsClient zhenziSmsClient;

    @Autowired
    private RedisCache redisCache;

    private final static String appId = "106121";

    private final static String appSecret = "633a2abe-c6de-4fe6-8cfb-2e2d8feb54bb";

    private final static String apiUrl = "https://sms_developer.zhenzikj.com";

    public AjaxResult sendSms(String phoneNumber) throws Exception {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("number", phoneNumber);
        params.put("templateId", "724");
        String[] templateParams = new String[2];
        templateParams[0] = RandomStringUtils.random(4, "0123456789");
        redisCache.setCacheObject(templateParams[0],1,60, TimeUnit.SECONDS);
        templateParams[1] = "1分钟";
        params.put("templateParams", templateParams);
        String result = zhenziSmsClient.send(params);
        JSONObject jsonObject = JSONObject.parseObject(result);
        if((int)jsonObject.get("code")==0){
            return AjaxResult.success("发送成功");
        }else{
            return AjaxResult.error("发送失败");
        }
    }
}
