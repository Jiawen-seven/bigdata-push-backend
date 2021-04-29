package com.ruoyi.quartz.request;

import cn.hutool.http.Header;
import cn.hutool.http.HttpRequest;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ruoyi.common.constant.RequestConstants;
import com.ruoyi.common.core.redis.RedisCache;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.quartz.domain.SysQuote;
import com.ruoyi.quartz.domain.SysStockDay;
import com.ruoyi.quartz.domain.VolumeRatioEps;
import com.ruoyi.quartz.service.ISysStockDayService;
import com.ruoyi.system.mapper.SysUserMapper;
import com.ruoyi.system.service.ISysUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Component
public class XueQiuRequest {
    private final static String quoteURL = "https://stock.xueqiu.com/v5/stock/batch/quote.json?symbol=SH000001,SZ399001,SZ399006,SH000688,HKHSI,HKHSCEI,HKHSCCI,.DJI,.IXIC,.INX";

    private static final Logger log = LoggerFactory.getLogger(XueQiuRequest.class);

    private final static String BASE_URL="https://xueqiu.com/service/v5/stock/screener/quote/list?";

    @Autowired
    private RedisCache redisCache;

    @Autowired
    private ISysStockDayService sysStockDayService;

    @Autowired
    private ISysUserService sysUserService;

    public String getApiJson(String url){
        return (HttpRequest.get(url)
                .header(Header.USER_AGENT, "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/88.0.4324.150 Safari/537.36")//头信息，多个头信息多次调用此方法即可
                .header("authority","stock.xueqiu.com")
                .header("accept","application/json, text/plain, */*")
                .header("origin","https://xueqiu.com")
                .header("cookie","xq_a_token=cc6a2aedef8a96868eb7257aef4a2ba6e222d2c6; xqat=cc6a2aedef8a96868eb7257aef4a2ba6e222d2c6; xq_r_token=3e168659e8b7d1863aff7a493cfc3398f438abe3; xq_id_token=eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiJ9.eyJ1aWQiOi0xLCJpc3MiOiJ1YyIsImV4cCI6MTYxOTkyMzQ2NiwiY3RtIjoxNjE4MTM1MTUwMDMzLCJjaWQiOiJkOWQwbjRBWnVwIn0.dBBkrHufqFD7BbgRJLIdZJ7Udu0F-rzME6Eizy5Dki9MGmNqWg6vALuJ8EH5PNXN_pVXJJBpNZx45Vnnn3M0SVYetv32dy4_ayf3pk2qSkCzuEDaSoFEl0AMs_3gShTtz6rx_5A19qQzp4ul2laOP5_xxP_GYQGf1GkNQ_A-gutTz6KZ0nF9zmBU2_Nsj82e5_42RWfn7u2C2FBCybif-RMKmght546wO0yqMwesBMBvlADJV8LkbQnHsSKI3kbFBXKsoXWvmZVpQEBxCdRSCiNDVGCSZJDbefo36CNbayLwwdyW6GBqtxocf5wzlvwkdpHH9A8U5F1OdGDt9u_A6Q; u=441618135173095; device_id=24700f9f1986800ab4fcc880530dd0ed; Hm_lvt_1db88642e346389874251b5a1eded6e3=1616988967,1617073971,1618135169,1618136501; Hm_lpvt_1db88642e346389874251b5a1eded6e3=1618136501")
                .timeout(3000)//超时，毫秒
                .execute().body());
    }

    private String getXueQiuListUrl(int page,int size,String market,String type){
        StringBuilder builder = new StringBuilder(BASE_URL);
        Map<String,Object> map = new HashMap<>();
        map.put("page",page);
        map.put("size",size);
        map.put("order","desc");
        map.put("orderby","percent");
        map.put("order_by","percent");
        map.put("market",market);
        map.put("type",type);
        map.put("_",System.currentTimeMillis());
        for(String key:map.keySet()){
            builder.append(key).append("=").append(map.get(key)).append("&");
        }
        return builder.toString().substring(0,builder.toString().length()-1);
    }
    private int getCounts(String market,String type){
        String spiderURL = getXueQiuListUrl(1,90,market,type);
        String jsonData = getApiJson(spiderURL);
        JSONObject jsonObject = JSONObject.parseObject(jsonData);
        return jsonObject.getJSONObject("data").getInteger("count");
    }
    public void getStockList(String market,String type){
        int counts = getCounts(market,type);
        int pages = counts%90==0 ? counts/90 : counts/90+1;
        List<SysStockDay> sysStockDayList = new ArrayList<>();
        for(int page=1;page<=pages;page++){
            JSONObject obj = JSONObject.parseObject(getApiJson(getXueQiuListUrl(page,90,market,type)));
            JSONArray list = obj.getJSONObject("data").getJSONArray("list");
            for(int i =0;i<list.size();i++){
                JSONObject jsonObject = list.getJSONObject(i);
                SysStockDay sysStockDay = JSONObject.parseObject(jsonObject.toJSONString(),SysStockDay.class);
                sysStockDay.setPercent(jsonObject.getString("percent"));
                sysStockDay.setCreateTime(new Date());
                sysStockDayList.add(sysStockDay);
            }
        }
        sysStockDayService.batchInsertSysStockDay(sysStockDayList);
        redisCache.deleteObject(RequestConstants.XUE_QIU_STOCK_KEY);
        //存入redis中
        redisCache.setCacheObject(RequestConstants.XUE_QIU_STOCK_KEY, LocalDate.now().toString());
    }

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
        redisCache.deleteObject(RequestConstants.XUE_QIU_KEY);
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
    /*
    * 获取每股净资产收益率和每股收益
    * */
    public VolumeRatioEps getVolumeRatioEps(String symbol){
        String URL = "https://stock.xueqiu.com/v5/stock/quote.json?symbol="+symbol+"&extend=detail";
        String jsonStr = HttpRequest.get(URL)
                .header(Header.USER_AGENT, "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/88.0.4324.150 Safari/537.36")//头信息，多个头信息多次调用此方法即可
                .header("authority","stock.xueqiu.com")
                .header("accept","application/json, text/plain, */*")
                .header("origin","https://xueqiu.com")
                .header("cookie","xq_a_token=cc6a2aedef8a96868eb7257aef4a2ba6e222d2c6; xqat=cc6a2aedef8a96868eb7257aef4a2ba6e222d2c6; xq_r_token=3e168659e8b7d1863aff7a493cfc3398f438abe3; xq_id_token=eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiJ9.eyJ1aWQiOi0xLCJpc3MiOiJ1YyIsImV4cCI6MTYxOTkyMzQ2NiwiY3RtIjoxNjE4MTM1MTUwMDMzLCJjaWQiOiJkOWQwbjRBWnVwIn0.dBBkrHufqFD7BbgRJLIdZJ7Udu0F-rzME6Eizy5Dki9MGmNqWg6vALuJ8EH5PNXN_pVXJJBpNZx45Vnnn3M0SVYetv32dy4_ayf3pk2qSkCzuEDaSoFEl0AMs_3gShTtz6rx_5A19qQzp4ul2laOP5_xxP_GYQGf1GkNQ_A-gutTz6KZ0nF9zmBU2_Nsj82e5_42RWfn7u2C2FBCybif-RMKmght546wO0yqMwesBMBvlADJV8LkbQnHsSKI3kbFBXKsoXWvmZVpQEBxCdRSCiNDVGCSZJDbefo36CNbayLwwdyW6GBqtxocf5wzlvwkdpHH9A8U5F1OdGDt9u_A6Q; u=441618135173095; device_id=24700f9f1986800ab4fcc880530dd0ed; Hm_lvt_1db88642e346389874251b5a1eded6e3=1616988967,1617073971,1618135169,1618136501; Hm_lpvt_1db88642e346389874251b5a1eded6e3=1618136501")
                .timeout(3000)//超时，毫秒
                .execute().body();
        JSONObject jsonObject = JSONObject.parseObject(jsonStr).getJSONObject("data").getJSONObject("quote");
        VolumeRatioEps volumeRatioEps = new VolumeRatioEps();
        double eps = jsonObject.getDouble("eps");
        double navps = jsonObject.getDouble("navps");
        double volumeRatio = eps/navps;
        volumeRatioEps.setEps(jsonObject.getString("eps"));
        volumeRatioEps.setVolumeRatio(String.valueOf(volumeRatio));
        return volumeRatioEps;
    }

    /*
    * 更新每股净资产收益率和每股收益
    * */
    public void updateVolumeRatioEps(){
        String dateTime = redisCache.getCacheObject(RequestConstants.XUE_QIU_STOCK_KEY);
        LocalDate localDate = LocalDate.parse(dateTime, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        LocalDateTime startLocalDateTime = LocalDateTime.of(localDate, LocalTime.MIN);
        LocalDateTime endLocalDateTime = LocalDateTime.of(localDate, LocalTime.MAX);
        Map<String,Object> map = new HashMap<>();
        map.put("startDateTime",startLocalDateTime);
        map.put("endDateTime",endLocalDateTime);
        List<Map<String,Object>> mapList = sysStockDayService.selectSysStockMap(map);
        mapList.forEach(m->{
            VolumeRatioEps updateMap = getVolumeRatioEps(m.get("symbol").toString());
            updateMap.setId(m.get("id").toString());
            sysStockDayService.updateVolumeRatioEps(updateMap);
        });
    }
    /*
    * 获取某只股票的实时状态
    * */
    public void getStockRealData(){
        List<String> symbols = sysUserService.getUserStocks();
        symbols.forEach(s->{
            if(StringUtils.isNotEmpty(s)){
                redisCache.deleteObject(RequestConstants.XUE_QIU_REAL_TIME+s);
                redisCache.setCacheObject(
                        RequestConstants.XUE_QIU_REAL_TIME+s,SplicingURL(s).toJSONString());
            }
        });
    }
    public JSONObject SplicingURL(String symbol){
        String URL = "https://stock.xueqiu.com/v5/stock/quote.json?symbol="+symbol+"&extend=detail";
        JSONObject jsonObject = JSONObject.parseObject(getApiJson(URL));
        JSONObject quote =  jsonObject.getJSONObject("data").getJSONObject("quote");
        JSONObject others = jsonObject.getJSONObject("data").getJSONObject("others");
        JSONObject data = new JSONObject();
        data.put("quote",quote);
        data.put("others",others);
        return data;
    }
    /*
    * 获取1小时热榜数据和24小时热榜数据
    * */
    public void getHourDataList(){
        String oneHourURL = "https://stock.xueqiu.com/v5/stock/hot_stock/list.json?size=8&_type=12&type=12";
        String twentyFourURL = "https://stock.xueqiu.com/v5/stock/hot_stock/list.json?size=8&_type=12&type=22";
        redisCache.setCacheObject(RequestConstants.XUE_QIU_ONE_HOUR,getApiJson(oneHourURL));
        redisCache.setCacheObject(RequestConstants.XUE_QIU_TWENTY_FOUR,getApiJson(twentyFourURL));
    }

}
