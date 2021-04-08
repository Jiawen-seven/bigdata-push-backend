package com.ruoyi.framework.config;

import com.zhenzi.sms.ZhenziSmsClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ZhenziSmsConfig {
    private final static String appId = "106121";

    private final static String appSecret = "633a2abe-c6de-4fe6-8cfb-2e2d8feb54bb";

    private final static String apiUrl = "https://sms_developer.zhenzikj.com";

    @Bean
    public ZhenziSmsClient getClient(){
        return new ZhenziSmsClient(apiUrl, appId, appSecret);
    }
}
