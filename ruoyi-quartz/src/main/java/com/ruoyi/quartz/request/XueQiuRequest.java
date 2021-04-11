package com.ruoyi.quartz.request;

import cn.hutool.http.Header;
import cn.hutool.http.HttpRequest;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ruoyi.common.constant.RequestConstants;
import com.ruoyi.common.core.redis.RedisCache;
import com.ruoyi.quartz.domain.SysQuote;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
public class XueQiuRequest {
    private final static String quoteURL = "https://stock.xueqiu.com/v5/stock/batch/quote.json?symbol=SH000001,SZ399001,SZ399006,SH000688,HKHSI,HKHSCEI,HKHSCCI,.DJI,.IXIC,.INX";

    private static final Logger log = LoggerFactory.getLogger(XueQiuRequest.class);

    @Autowired
    private RedisCache redisCache;

    public void getQuote(){
        String jsonStr = HttpRequest.get(quoteURL)
                .header(Header.USER_AGENT, "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/88.0.4324.150 Safari/537.36")//头信息，多个头信息多次调用此方法即可
                .header("authority","stock.xueqiu.com")
                .header("accept","application/json, text/plain, */*")
                .header("origin","https://xueqiu.com")
                .header("cookie","xq_a_token=cc6a2aedef8a96868eb7257aef4a2ba6e222d2c6; xqat=cc6a2aedef8a96868eb7257aef4a2ba6e222d2c6; xq_r_token=3e168659e8b7d1863aff7a493cfc3398f438abe3; xq_id_token=eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiJ9.eyJ1aWQiOi0xLCJpc3MiOiJ1YyIsImV4cCI6MTYxOTkyMzQ2NiwiY3RtIjoxNjE4MTM1MTUwMDMzLCJjaWQiOiJkOWQwbjRBWnVwIn0.dBBkrHufqFD7BbgRJLIdZJ7Udu0F-rzME6Eizy5Dki9MGmNqWg6vALuJ8EH5PNXN_pVXJJBpNZx45Vnnn3M0SVYetv32dy4_ayf3pk2qSkCzuEDaSoFEl0AMs_3gShTtz6rx_5A19qQzp4ul2laOP5_xxP_GYQGf1GkNQ_A-gutTz6KZ0nF9zmBU2_Nsj82e5_42RWfn7u2C2FBCybif-RMKmght546wO0yqMwesBMBvlADJV8LkbQnHsSKI3kbFBXKsoXWvmZVpQEBxCdRSCiNDVGCSZJDbefo36CNbayLwwdyW6GBqtxocf5wzlvwkdpHH9A8U5F1OdGDt9u_A6Q; u=441618135173095; device_id=24700f9f1986800ab4fcc880530dd0ed; Hm_lvt_1db88642e346389874251b5a1eded6e3=1616988967,1617073971,1618135169,1618136501; Hm_lpvt_1db88642e346389874251b5a1eded6e3=1618136501")
                .timeout(3000)//超时，毫秒
                .execute().body();
        JSONObject jsonObject = JSONObject.parseObject(jsonStr).getJSONObject("data");
        JSONArray items = jsonObject.getJSONArray("items");
        //获取上证指数、深证成指、创业板指、科创50
        List<SysQuote> sysQuoteList = new ArrayList<>();
        for(int i =0;i<4;i++){
            JSONObject quote = items.getJSONObject(i).getJSONObject("quote");
            sysQuoteList.add(getQuoteMap(quote));
        }
        redisCache.setCacheList(RequestConstants.XUE_QIU_KEY,sysQuoteList);
    }
    public SysQuote getQuoteMap(JSONObject quote){
        SysQuote sysQuote = new SysQuote();
        sysQuote.setName(quote.getString("name"));
        sysQuote.setSymbol(quote.getString("symbol"));
        sysQuote.setChg(quote.getDouble("chg"));
        sysQuote.setCurrent(quote.getDouble("current"));
        sysQuote.setPercent(quote.getDouble("percent"));
        return sysQuote;
    }
    public void mainGetQuote(){
        LocalDateTime localDateTime = LocalDateTime.now();
        int weekDay = localDateTime.getDayOfWeek().getValue();
        int hour = localDateTime.getHour();
        if(weekDay<=5 && hour>=9 && hour<=18){
            getQuote();
        }else{
            log.info("不在工作时间内");
        }
    }
}
