package com.ruoyi.quartz.request;

import cn.hutool.http.Header;
import cn.hutool.http.HttpRequest;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.constant.RequestConstants;
import com.ruoyi.common.core.redis.RedisCache;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.quartz.dao.XueQiuDao;
import com.ruoyi.quartz.domain.SysQuote;
import com.ruoyi.quartz.domain.SysStockDay;
import com.ruoyi.quartz.domain.VolumeRatioEps;
import com.ruoyi.quartz.domain.XueQiu;
import com.ruoyi.quartz.entity.SysIndustry;
import com.ruoyi.quartz.service.ISysStockDayService;
import com.ruoyi.system.domain.SysStock;
import com.ruoyi.system.domain.SysStockMin;
import com.ruoyi.system.mapper.SysStockMapper;
import com.ruoyi.system.mapper.SysUserMapper;
import com.ruoyi.system.service.ISysStockMinService;
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
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

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

    @Autowired
    private SysStockMapper sysStockMapper;

    @Autowired
    private XueQiuDao xueQiuDao;

    @Autowired
    private ISysStockMinService sysStockMinService;

    public String getApiJson(String url){
        return (HttpRequest.get(url)
                .header(Header.USER_AGENT, "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/88.0.4324.150 Safari/537.36")//头信息，多个头信息多次调用此方法即可
                .header("authority","stock.xueqiu.com")
                .header("accept","application/json, text/plain, */*")
                .header("origin","https://xueqiu.com")
                .header("cookie","device_id=24700f9f1986800ab4fcc880530dd0ed; s=ds16j26q5y; xq_a_token=4b4d3f5b97e67b975f4e1518dc4c417ebf0ad4c4; xqat=4b4d3f5b97e67b975f4e1518dc4c417ebf0ad4c4; xq_r_token=960e1d453ab676f85fa80d2d41b80edebfde8cc0; xq_id_token=eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiJ9.eyJ1aWQiOi0xLCJpc3MiOiJ1YyIsImV4cCI6MTYyMjUxNTc5MiwiY3RtIjoxNjE5OTUxODY4MjI5LCJjaWQiOiJkOWQwbjRBWnVwIn0.BiW3-k89W_ig7a_7Q2M0ch0YreGQdETnKU0SOnTJ3h1vJ1A7kn6hE_nJ_ZL6THU-5aM_qWGZI6lF_K97EArnaW5fylvPZoXkspaOe-MMdl_FmKppQaGmY17_BNflgoRuYtQdrS04z8D3spGg0VutfQiFr8UMCF7rjs0H83NJBYOUmxUWLo5r3VxuqDLyqFAHoLqhzv24lRluWso61CcbDmQrZU9rkSYOgnQhLNNIq45EfMWTwTwmMn-Cmz6QsrePLsw0dxTJ9xnhedi0KktKh5rt_gZ81WKCQ8pZiwaWNRBDP61LSvT8EzEXW5duOAq9sOQoK-AvejXc1BJyFZmfiw; u=781619951916831; Hm_lvt_1db88642e346389874251b5a1eded6e3=1619763471,1619836834,1619923738,1619951917; Hm_lpvt_1db88642e346389874251b5a1eded6e3=1619952005")
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
        String jsonStr =getApiJson(quoteURL);
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
        String attentionUrl = "https://xueqiu.com/recommend/pofriends.json?type=1&code="+symbol+"&start=0&count=14";
        int attentionCount = JSONObject.parseObject(getApiJson(attentionUrl)).getInteger("totalcount");
        String jsonStr = getApiJson(URL);
        JSONObject jsonObject = JSONObject.parseObject(jsonStr).getJSONObject("data").getJSONObject("quote");
        VolumeRatioEps volumeRatioEps = new VolumeRatioEps();
        double eps = jsonObject.getDouble("eps");
        double navps = jsonObject.getDouble("navps");
        double volumeRatio = eps/navps;
        volumeRatioEps.setEps(jsonObject.getString("eps"));
        volumeRatioEps.setVolumeRatio(String.valueOf(volumeRatio));
        volumeRatioEps.setAttentionCount(attentionCount);
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
                JSONObject jsonObject = SplicingURL(s);
                redisCache.setCacheObject(
                        RequestConstants.XUE_QIU_REAL_TIME+s,jsonObject.toJSONString());
                insertStockMin(s,jsonObject.getJSONObject("quote"));
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
    /*获取SysStockMin对象，根据时间段执行9:30-11:30 13:00-15:00*/
    public void insertStockMin(String symbol,JSONObject jsonObject){
        LocalDateTime localDateTime = LocalDateTime.now();
        int hour = localDateTime.getHour();
        int min = localDateTime.getMinute();
        if((hour==9&&min>=30)||(hour==10)||(hour==11&&min<=30) || (hour>=13&&hour<=15)){
            SysStockMin sysStockMin = new SysStockMin();
            sysStockMin.setInsertTime(new Date());
            sysStockMin.setSymbol(symbol);
            sysStockMin.setCurrent(jsonObject.getDouble("current"));
            sysStockMin.setName(jsonObject.getString("name"));
            sysStockMin.setPercent(jsonObject.getDouble("percent"));
            sysStockMin.setChg(jsonObject.getDouble("chg"));
            sysStockMin.setAvgPrice(jsonObject.getDouble("avg_price"));
            sysStockMin.setVolume(jsonObject.getDouble("volume"));
            sysStockMin.setIsDelete("N");
            sysStockMinService.insertSysStockMin(sysStockMin);
        }else{
            log.info("当前不在工作时间，不插入sys_stock_min表");
        }

    }
    /*
    * 定时删除(软删除)
    * */
    @Log(title = "定时软删除sys_stock_min")
    public void updateSysStockMin(){
        LocalDateTime localDateTime = LocalDateTime.now();
        int weekDay = localDateTime.getDayOfWeek().getValue();
        if(weekDay<=5){
            sysStockMinService.updateAllSysStockMin();
        }
    }
    /*
    * 获取1小时热榜数据和24小时热榜数据
    * */
    public void getHourDataList(){
        String oneHourURL = "https://stock.xueqiu.com/v5/stock/hot_stock/list.json?size=8&_type=10&type=10";
        String twentyFourURL = "https://stock.xueqiu.com/v5/stock/hot_stock/list.json?size=8&_type=12&type=22";
        redisCache.setCacheObject(RequestConstants.XUE_QIU_ONE_HOUR,getApiJson(oneHourURL));
        redisCache.setCacheObject(RequestConstants.XUE_QIU_TWENTY_FOUR,getApiJson(twentyFourURL));
    }
    /*
    * 获取日K线图
    * */
    public void getDayK(){
        List<String> symbols = sysUserService.getUserStocks();
        symbols.forEach(s->{
            if(StringUtils.isNotEmpty(s)){
                List<JSONArray> arrays = new ArrayList<>();
                JSONObject jsonObject = getDayKUrl(s);
                setList(jsonObject,arrays,null);
                redisCache.deleteObject(RequestConstants.XUE_QIU_DAY_K+s);
                redisCache.setCacheList(RequestConstants.XUE_QIU_DAY_K+s,arrays);
            }
        });
    }
    /*
    * 获取分K线图
    * */
    public void getMinK(){
        List<String> symbols = sysUserService.getUserStocks();
        symbols.forEach(s->{
            if(StringUtils.isNotEmpty(s)){
                List<JSONArray> arrays = new ArrayList<>();
                JSONObject jsonObject = getMinKUrl(s);
                setList(jsonObject,arrays,DateUtils.YYYY_MM_DD_HH_MM_SS);
                redisCache.deleteObject(RequestConstants.XUE_QIU_MIN_K+s);
                redisCache.setCacheList(RequestConstants.XUE_QIU_MIN_K+s,arrays);
            }
        });
    }

    /*
    * 循环设定List,flag true为日K，false为分K
    * */
    public void setList(JSONObject jsonObject,List<JSONArray> arrays,String format){
        JSONArray array = jsonObject.getJSONArray("item");
        for(int i=0;i<array.size();i++){
            JSONArray arr = array.getJSONArray(i);
            long timestamp = arr.getLong(0);
            JSONArray list = new JSONArray();
            String date = "";
            if(format==null){
                date = DateUtils.dateTime(new Date(timestamp));
            }else{
                date = DateUtils.parseDateToStr(DateUtils.YYYY_MM_DD_HH_MM_SS,new Date(timestamp));
            }
            Double open = arr.getDouble(2);
            Double close = arr.getDouble(5);
            Double low = arr.getDouble(4);
            Double high = arr.getDouble(3);
            Double volume = arr.getDouble(1);
            list.add(date);
            list.add(open);
            list.add(close);
            list.add(low);
            list.add(high);
            list.add(String.valueOf(volume));
            arrays.add(list);
        }
    }
    /*
    * 获取日K线图爬取链接
    * */
    public JSONObject getDayKUrl(String symbol){
        long timestamp = System.currentTimeMillis();
        String URL="https://stock.xueqiu.com/v5/stock/chart/kline.json?symbol="+symbol+"&begin="+timestamp+"&period=day&type=before&count=-284&indicator=kline,pe,pb,ps,pcf,market_capital,agt,ggt,balance";
        return JSONObject.parseObject(getApiJson(URL)).getJSONObject("data");
    }
    /*
    * 获取1分钟K线图爬取对象
    * */
    public JSONObject getMinKUrl(String symbol){
        long timestamp = System.currentTimeMillis();
        String URL="https://stock.xueqiu.com/v5/stock/chart/kline.json?symbol="+symbol+"&begin="+timestamp+"&period=1m&type=before&count=-284&indicator=kline,pe,pb,ps,pcf,market_capital,agt,ggt,balance";
        return JSONObject.parseObject(getApiJson(URL)).getJSONObject("data");
    }
    /**
     * @description 爬取第一页股评
     * @param symbol
     * @see JSONObject
     * @author jijj
     * @createTime 2021/5/1 10:54
     */
    public JSONArray getFirstStockComment(String symbol){
        String URL = "https://xueqiu.com/query/v1/symbol/search/status?u=351619836834806&uuid=1388322554107805696&count=10&comment=0&symbol="+symbol+
                "&hl=0&source=all&sort=&page=1&q=&type=11&session_token=null&access_token=520e7bca78673752ed71e19b8820b5eb854123af";
        return JSONObject.parseObject(getApiJson(URL)).getJSONArray("list");
    }
    /**
     * @description 爬取除了第一页外的股评
     * @param symbol, page, lastId]
     * @see JSONObject
     * @author jijj
     * @createTime 2021/5/1 10:54
     */
    public JSONArray getStockComment(String symbol,int page,String lastId){
        String URL="https://xueqiu.com/query/v1/symbol/search/status?u=351619836834806&uuid=1388322554107805696&count=10&comment=0&symbol="+symbol+
                "&hl=0&source=all&sort=&page="+page+"&q=&type=11&session_token=null&access_token=520e7bca78673752ed71e19b8820b5eb854123af&last_id="+lastId;
        return JSONObject.parseObject(getApiJson(URL)).getJSONArray("list");
    }
    /**
     * @description 爬取总股评
     * @param
     * @see
     * @author jijj
     * @createTime 2021/5/1 10:55
     */
    public void spiderStockComment(){
        List<String> symbols = sysUserService.getUserStocks();
        symbols  =symbols.stream().filter(StringUtils::isNotEmpty).collect(Collectors.toList());
        String date  = redisCache.getCacheObject(RequestConstants.XUE_QIU_STOCK_KEY);
        long timeFlag = DateUtils.dateTime(DateUtils.YYYY_MM_DD,date).getTime();
        for(String symbol:symbols){
            long spiderTime = Long.MAX_VALUE;
            AtomicInteger a = new AtomicInteger(1);
            String lastId = "";
            List<XueQiu> xueQiuList = new ArrayList<>();
            while (spiderTime>timeFlag){
                JSONArray jsonArray = null;
                if(a.get()==1){
                    jsonArray = getFirstStockComment(symbol);
                }else{
                    jsonArray = getStockComment(symbol,a.get(),lastId);
                }
                for(int i =0;i<jsonArray.size();i++){
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    spiderTime = jsonObject.getLong("created_at");
                    //只获取当天的股评
                    if(spiderTime<timeFlag){
                        break;
                    }
                    lastId = jsonObject.getString("id");
                    XueQiu xueQiu = parseStockData(jsonObject);
                    xueQiu.setSymbol(symbol);
                    xueQiuList.add(xueQiu);
                }
                a.incrementAndGet();
            }
            xueQiuDao.insertList(xueQiuList);
        }
    }

    public XueQiu parseStockData(JSONObject object){
        XueQiu xueQiu = new XueQiu();
        xueQiu.setFav_count(object.getLong("fav_count"));
        xueQiu.setLike_count(object.getLong("like_count"));
        xueQiu.setReply_count(object.getLong("reply_count"));
        xueQiu.setRetweet_count(object.getLong("retweet_count"));
        xueQiu.setView_count(object.getLong("view_count"));
        xueQiu.setFollowers_count(object.getJSONObject("user").getLong("followers_count"));
        xueQiu.setFriends_count(object.getJSONObject("user").getLong("friends_count"));
        xueQiu.setStatus_count(object.getJSONObject("user").getLong("status_count"));
        xueQiu.setVerified_info(object.getJSONObject("user").getJSONArray("verified_infos") != null);
        xueQiu.setScreen_name(object.getJSONObject("user").getString("screen_name"));
        xueQiu.setProfile_image_url(object.getJSONObject("user").getString("photo_domain")+object.getJSONObject("user").getString("profile_image_url").split(",")[0]);
        xueQiu.setText(object.getString("text"));
        xueQiu.setSpiderDate(DateUtils.getDate());
        return xueQiu;
    }
    public void updateSysStockType(){
        long systemTime = System.currentTimeMillis();
        String URL="https://xueqiu.com/service/screener/screen?category=CN&exchange=sh_sz&areacode=&indcode=&order_by=symbol&order=desc&page=1&size=30&only_count=0&current=&pct=&mc=&volume=&_="+systemTime;
        int pages = getSysStockTypePages(URL);
        List<SysStock> sysStockList = new ArrayList<>();
        List<SysStock> updateStockList = sysStockMapper.selectSysStockList(null);
        List<SysIndustry> sysIndustries = getSysIndustryList();
        for(int i =1;i<=pages;i++){
            String spiderUrl = "https://xueqiu.com/service/screener/screen?category=CN&exchange=sh_sz&areacode=&indcode=&order_by=symbol&order=desc&page="+i+"&size=30&only_count=0&current=&pct=&mc=&volume=&_="+systemTime;
            JSONObject data = JSONObject.parseObject(getApiJson(spiderUrl)).getJSONObject("data");
            JSONArray list = data.getJSONArray("list");
            for(int j =0;j<list.size();j++){
                JSONObject obj = list.getJSONObject(j);
                SysStock sysStock = formatSymbol(obj.getString("symbol"),updateStockList);
                if(sysStock!=null && StringUtils.isNotEmpty(sysStock.getValue())){
                    sysStock.setEncode(obj.getString("indcode"));
                    sysStock.setType(formatEncode(obj.getString("indcode"),sysIndustries));
                    /*sysStockList.add(sysStock);
                    System.out.println(sysStock);*/
                    sysStockMapper.updateSysStock(sysStock);
                }
            }
        }
        sysStockMapper.deleteIsNull();
    }
    public int getSysStockTypePages(String URL){
        JSONObject data = JSONObject.parseObject(getApiJson(URL)).getJSONObject("data");
        int count =  data.getInteger("count");
        return count%30==0?count/30:count/30+1;
    }
    public List<SysIndustry> getSysIndustryList(){
        String URL = "https://xueqiu.com/service/screener/industries?category=CN&_="+System.currentTimeMillis();
        JSONObject obj = JSONObject.parseObject(getApiJson(URL)).getJSONObject("data");
        JSONArray array = obj.getJSONArray("industries");
        List<SysIndustry> sysIndustries = new ArrayList<>();
        for(int i =0;i<array.size();i++){
            JSONObject object = array.getJSONObject(i);
            SysIndustry sysIndustry = JSONObject.parseObject(object.toJSONString(),SysIndustry.class);
            sysIndustries.add(sysIndustry);
        }
        return sysIndustries;
    }
    /**
     * @description 筛选出相等encode的字典值
     * @param
     * @see String
     * @author jijj
     * @createTime 2021/5/3 13:12
     */
    public String formatEncode(String encode,List<SysIndustry> list){
        List<SysIndustry> sysIndustries = list.stream().filter(sysIndustry -> sysIndustry.getEncode().equals(encode)).collect(Collectors.toList());
        if(sysIndustries!=null && sysIndustries.size()>0){
            return sysIndustries.get(0).getName();
        }
        return "";
    }
    public SysStock formatSymbol(String symbol,List<SysStock> sysStockList){
        List<SysStock> list = sysStockList.stream().filter(sysStock -> sysStock.getValue().equals(symbol)).collect(Collectors.toList());
        if(list!=null && list.size()>0){
            return list.get(0);
        }
        return null;
    }
    public void updateSysStockData(){
        String dateTime = redisCache.getCacheObject(RequestConstants.XUE_QIU_STOCK_KEY);
        LocalDate localDate = LocalDate.parse(dateTime, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        LocalDateTime startLocalDateTime = LocalDateTime.of(localDate, LocalTime.MIN);
        LocalDateTime endLocalDateTime = LocalDateTime.of(localDate, LocalTime.MAX);
        Map<String,Object> map = new HashMap<>();
        map.put("startDateTime",startLocalDateTime);
        map.put("endDateTime",endLocalDateTime);
        List<Map<String,Object>> mapList = sysStockDayService.selectSysStockMap(map);
        List<SysStock> sysStockList = new ArrayList<>();
        sysStockMapper.deleteAll();
        mapList.forEach(m->{
            SysStock sysStock = new SysStock();
            sysStock.setValue(m.get("symbol").toString());
            sysStock.setName(m.get("name").toString());
            sysStockList.add(sysStock);
        });
        sysStockMapper.insertAll(sysStockList);
    }

}
