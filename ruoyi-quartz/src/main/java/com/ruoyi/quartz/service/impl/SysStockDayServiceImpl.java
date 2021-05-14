package com.ruoyi.quartz.service.impl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ruoyi.common.constant.RequestConstants;
import com.ruoyi.common.core.redis.RedisCache;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.quartz.dao.XueQiuDao;
import com.ruoyi.quartz.domain.SysStockDay;
import com.ruoyi.quartz.domain.VolumeRatioEps;
import com.ruoyi.quartz.domain.XueQiu;
import com.ruoyi.quartz.entity.*;
import com.ruoyi.quartz.mapper.SysStockDayMapper;
import com.ruoyi.quartz.request.XueQiuRequest;
import com.ruoyi.quartz.service.ISysEmailService;
import com.ruoyi.quartz.service.ISysStockDayService;
import com.ruoyi.quartz.util.EmailTableUtils;
import com.ruoyi.quartz.util.PageUtils;
import com.ruoyi.quartz.util.RedBlackListUtils;
import com.ruoyi.system.domain.SysUserRegistered;
import com.ruoyi.system.service.ISysUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

/**
 * 定时任务爬取股票数据Service业务层处理
 * 
 * @author ruoyi
 * @date 2021-04-17
 */
@Service
public class SysStockDayServiceImpl implements ISysStockDayService
{
    private static final Logger log = LoggerFactory.getLogger(SysStockDayServiceImpl.class);
    @Autowired
    private SysStockDayMapper sysStockDayMapper;

    @Autowired
    private RedisCache redisCache;

    @Autowired
    private ISysUserService sysUserService;

    @Autowired
    private ISysEmailService sysEmailService;

    @Autowired
    private XueQiuDao xueQiuDao;

    /**
     * 查询定时任务爬取股票数据
     * 
     * @param id 定时任务爬取股票数据ID
     * @return 定时任务爬取股票数据
     */
    @Override
    public SysStockDay selectSysStockDayById(Long id)
    {
        return sysStockDayMapper.selectSysStockDayById(id);
    }

    /**
     * 查询定时任务爬取股票数据列表
     * 
     * @param sysStockDay 定时任务爬取股票数据
     * @return 定时任务爬取股票数据
     */
    @Override
    public List<SysStockDay> selectSysStockDayList(SysStockDay sysStockDay)
    {
        return sysStockDayMapper.selectSysStockDayList(sysStockDay);
    }

    /**
     * 新增定时任务爬取股票数据
     * 
     * @param sysStockDay 定时任务爬取股票数据
     * @return 结果
     */
    @Override
    public int insertSysStockDay(SysStockDay sysStockDay)
    {
        sysStockDay.setCreateTime(DateUtils.getNowDate());
        return sysStockDayMapper.insertSysStockDay(sysStockDay);
    }

    /**
     * 修改定时任务爬取股票数据
     * 
     * @param sysStockDay 定时任务爬取股票数据
     * @return 结果
     */
    @Override
    public int updateSysStockDay(SysStockDay sysStockDay)
    {
        return sysStockDayMapper.updateSysStockDay(sysStockDay);
    }

    /**
     * 批量删除定时任务爬取股票数据
     * 
     * @param ids 需要删除的定时任务爬取股票数据ID
     * @return 结果
     */
    @Override
    public int deleteSysStockDayByIds(Long[] ids)
    {
        return sysStockDayMapper.deleteSysStockDayByIds(ids);
    }

    /**
     * 删除定时任务爬取股票数据信息
     * 
     * @param id 定时任务爬取股票数据ID
     * @return 结果
     */
    @Override
    public int deleteSysStockDayById(Long id)
    {
        return sysStockDayMapper.deleteSysStockDayById(id);
    }

    @Override
    public List<SysStockDay> getSysStockListByRedisDate() {
        String dateTime = redisCache.getCacheObject(RequestConstants.XUE_QIU_STOCK_KEY);
        LocalDate localDate = LocalDate.parse(dateTime, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        LocalDateTime startLocalDateTime = LocalDateTime.of(localDate, LocalTime.MIN);
        LocalDateTime endLocalDateTime = LocalDateTime.of(localDate, LocalTime.MAX);
        Map<String,Object> map = new HashMap<>();
        map.put("startDateTime",startLocalDateTime);
        map.put("endDateTime",endLocalDateTime);
        List<SysStockDay> sysStockDayList = sysStockDayMapper.selectSysStockByDay(map);
        if(sysStockDayList==null){
            sysStockDayList = new ArrayList<>();
        }
        //分页
        return sysStockDayList;
    }

    @Override
    public void selectFundRanking() {
        List<SysStockDay> sysStockDayList = getSysStockListByRedisDate();
        if(sysStockDayList!=null && sysStockDayList.size()>0){
            Calendar calendar = Calendar.getInstance();
            Date dateBaseTime = sysStockDayList.get(0).getCreateTime();
            calendar.setTime(dateBaseTime);
            //获取过去七天
            calendar.add(Calendar.DATE, - 7);
            Date aWeekAgoDate = calendar.getTime();
            Map<String,Object> map = DateUtils.returnDateMap(aWeekAgoDate);
            List<SysStockDay> weekSysStockDayList = sysStockDayMapper.selectSysStockByDay(map);
            List<FundRanking> fundRankingList = new ArrayList<>();
            if(weekSysStockDayList==null || weekSysStockDayList.size()==0){
                sysStockDayList.forEach(s->{
                    FundRanking fundRanking  = new FundRanking();
                    fundRanking.setName(s.getName());
                    fundRanking.setSymbol(s.getSymbol());
                    fundRanking.setTime("近一周");
                    fundRanking.setPercent("0");
                    fundRankingList.add(fundRanking);
                });
            }else{
                sysStockDayList.forEach(s->{
                    FundRanking fundRanking  = new FundRanking();
                    fundRanking.setName(s.getName());
                    fundRanking.setSymbol(s.getSymbol());
                    //获取一周前的股票数据对象
                    SysStockDay filterObject = filterSysStockDay(weekSysStockDayList,s.getSymbol());
                    fundRanking.setTime("近一周");
                    fundRanking.setPercent(computedPercent(filterObject.getCurrent(),s.getCurrent()));
                    System.out.println("查看FundRanking");
                    System.out.println(fundRanking);
                    fundRankingList.add(fundRanking);
                });
            }
            Collections.sort(fundRankingList);
            Collections.reverse(fundRankingList);
            redisCache.deleteObject(RequestConstants.XUE_QIU_FUND_RANK_KEY);
            redisCache.setCacheList(RequestConstants.XUE_QIU_FUND_RANK_KEY,fundRankingList.subList(0,10));
        }
    }

    @Override
    public int batchInsertSysStockDay(List<SysStockDay> sysStockDayList) {
        return sysStockDayMapper.batchInsertSysStockDay(sysStockDayList);
    }

    @Override
    public List<Map<String, Object>> selectSysStockMap(Map<String, Object> map) {
        return sysStockDayMapper.selectSysStockMap(map);
    }

    @Override
    public void updateVolumeRatioEps(VolumeRatioEps map) {
        sysStockDayMapper.updateVolumeRatioEps(map);
    }

    @Override
    public void batchUpdateVolumeRatioEps(List<VolumeRatioEps> mapList) {
        sysStockDayMapper.batchUpdateVolumeRatioEps(mapList);
    }

    @Override
    public void updateStockRedBlack() {
        List<SysStockDay> sysStockDayList = getSysStockListByRedisDate();
        List<RedBlackList> redBlackLists = new ArrayList<>();
        sysStockDayList.forEach(sysStockDay -> {
            int epsScore = RedBlackListUtils.computedEps(sysStockDay.getEps());
            int volumeRatioScore = RedBlackListUtils.computedVolumeRatio(sysStockDay.getVolumeRatio());
            int peTtmScore = RedBlackListUtils.computedPeTtm(sysStockDay.getPeTtm());
            int pbScore = RedBlackListUtils.computedPb(sysStockDay.getPb());
            int score = (epsScore+volumeRatioScore+peTtmScore+pbScore)/4;
            RedBlackList redBlackList = new RedBlackList();
            redBlackList.setName(sysStockDay.getName());
            redBlackList.setScore(score);
            redBlackList.setSymbol(sysStockDay.getSymbol());
            redBlackLists.add(redBlackList);
        });
        Collections.sort(redBlackLists);
        List<RedBlackList> topThree = redBlackLists.subList(0,3);
        List<RedBlackList> backThree = redBlackLists.subList(redBlackLists.size()-3,redBlackLists.size());
        topThree.forEach(t->{
            t.setReason("业绩很稳定");
        });
        String[] arrays = {"业绩亏损第三","业绩亏损第二","业绩亏损第一"};
        for (int i =0;i<backThree.size();i++) {
            backThree.get(i).setReason(arrays[i]);
        }
        Map<String,Object> map = new HashMap<>();
        map.put("topThree",topThree);
        map.put("backThree",backThree);
        JSONObject jsonObject = new JSONObject(map);
        redisCache.deleteObject(RequestConstants.XUE_QIU_RED_BLACK);
        redisCache.setCacheObject(RequestConstants.XUE_QIU_RED_BLACK,jsonObject.toJSONString());
    }

    @Override
    public JSONObject getStockRedBlack() {
        String xueQiuRedBlackList = redisCache.getCacheObject(RequestConstants.XUE_QIU_RED_BLACK);
        return JSONObject.parseObject(xueQiuRedBlackList);
    }

    @Override
    public JSONObject getStockRealTimeStatus(String symbol) {
        String realStock = redisCache.getCacheObject(RequestConstants.XUE_QIU_REAL_TIME+symbol);
        return JSONObject.parseObject(realStock);
    }

    @Override
    public JSONObject getHourDataList(String hour) {
        String hourData = "";
        if("1".equals(hour)){
            hourData = redisCache.getCacheObject(RequestConstants.XUE_QIU_ONE_HOUR);
        }else if("24".equals(hour)){
            hourData = redisCache.getCacheObject(RequestConstants.XUE_QIU_TWENTY_FOUR);
        }
        return JSONObject.parseObject(hourData);
    }

    @Override
    public void sendStockInfoToMail() {
        List<String> symbols = sysUserService.getUserStocks();
        List<MailEntity> mailEntityList = new ArrayList<>();
        Map<String,String> map = new HashMap<>();
        symbols.forEach(s->{
            if(StringUtils.isNotEmpty(s)){
                JSONObject json  = JSONObject.parseObject(redisCache.getCacheObject(RequestConstants.XUE_QIU_REAL_TIME+s));
                List<StockComment> stockCommentList = redisCache.getCacheList(RequestConstants.XUE_QIU_COMMENT+s);
                if(json!=null && json.size()>0){
                    JSONObject obj = new JSONObject();
                    List<String> stockInfos = new ArrayList<>();
                    String name  = json.getJSONObject("quote").getString("name");
                    String symbol  = json.getJSONObject("quote").getString("symbol");
                    for(StockInfo stockInfo:StockInfo.values()){
                        String value = json.getJSONObject("quote").getString(stockInfo.getCode());
                        String code = stockInfo.getCode();
                        stockInfos.add(EmailTableUtils.formatValue(value,code));
                        if(value!=null){
                            obj.put(code,Double.valueOf(value));
                        }else{
                            obj.put(code,0);
                        }
                    }
                    map.put(s,obj.toJSONString());
                    mailEntityList.add(EmailTableUtils.getTableBody(stockInfos,name,symbol,stockCommentList));
                }
            }
        });
        redisCache.deleteObject(RequestConstants.XUE_QIU_LAST_DAY);
        log.info("删除Redis的键值:"+RequestConstants.XUE_QIU_LAST_DAY);
        redisCache.setCacheMap(RequestConstants.XUE_QIU_LAST_DAY,map);
        List<SysUserRegistered> userRegisteredList = sysUserService.selectAllRegisteredUser();
        sysEmailService.sendMail(mailEntityList,userRegisteredList);
    }

    @Override
    public List<JSONArray> getDayK(String symbol) {
        return redisCache.getCacheList(RequestConstants.XUE_QIU_DAY_K+symbol);
    }

    @Override
    public void computedExcellentStockComment() {
        List<String> symbols = sysUserService.getUserStocks();
        symbols = symbols.stream().filter(StringUtils::isNotEmpty).collect(Collectors.toList());
        String date = redisCache.getCacheObject(RequestConstants.XUE_QIU_STOCK_KEY);
        for(String symbol:symbols){
            List<XueQiu> xueQiuList = xueQiuDao.getXueQiuList(date,symbol);
            if(xueQiuList==null){
                continue;
            }
            List<StockComment> stockCommentList = computedComment(xueQiuList);
            if(stockCommentList==null || stockCommentList.size()==0){
                continue;
            }
            List<StockComment> commentList = stockCommentList.stream().filter(s->s.getFlag()==2).collect(Collectors.toList());
            redisCache.deleteObject(RequestConstants.XUE_QIU_COMMENT+symbol);
            redisCache.setCacheList(RequestConstants.XUE_QIU_COMMENT+symbol,commentList);
        }
    }

    @Override
    public List<StockComment> getCommentList(String symbol) {
        return redisCache.getCacheList(RequestConstants.XUE_QIU_COMMENT+symbol);
    }

    @Override
    public JSONObject getSystemStockData(String symbol) {
        Map<String,String> map = redisCache.getCacheMap(RequestConstants.XUE_QIU_LAST_DAY);
        return JSONObject.parseObject(map.get(symbol));
    }

    @Override
    public List<SysStockDay> selectRecommendStock(String symbol) {
        String dateTime = redisCache.getCacheObject(RequestConstants.XUE_QIU_STOCK_KEY);
        LocalDate localDate = LocalDate.parse(dateTime, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        LocalDateTime startLocalDateTime = LocalDateTime.of(localDate, LocalTime.MIN);
        LocalDateTime endLocalDateTime = LocalDateTime.of(localDate, LocalTime.MAX);
        Map<String,Object> map = new HashMap<>();
        map.put("startDate",startLocalDateTime);
        map.put("endDate",endLocalDateTime);
        map.put("value",symbol);
        return sysStockDayMapper.selectRecommendStock(map);
    }

    @Override
    public List<JSONArray> getMinK(String symbol) {
        return redisCache.getCacheList(RequestConstants.XUE_QIU_MIN_K+symbol);
    }

    public List<StockComment> computedComment(List<XueQiu> xueQiuList){
        List<StockComment> stockCommentList = new ArrayList<>();
        for(XueQiu xueQiu:xueQiuList){
            StockComment stockComment = new StockComment();
            stockComment.setAvatar(xueQiu.getProfile_image_url());
            stockComment.setScreenName(xueQiu.getScreen_name());
            stockComment.setText(xueQiu.getText());
            stockComment.setSymbol(xueQiu.getSymbol());
            //收藏
            long fav_count =0L;
            if(xueQiu.getFav_count()!=null){
                fav_count = xueQiu.getFav_count();
            }
            //点赞数
            long like_count = 0L;
            if(xueQiu.getLike_count()!=null){
                like_count = xueQiu.getLike_count();
            }
            //评论数
            long reply_count = 0L;
            if(xueQiu.getReply_count()!=null){
                reply_count = xueQiu.getReply_count();
            }
            //转发数
            long retweet_count=0L;
            if(xueQiu.getRetweet_count()!=null){
                retweet_count =xueQiu.getRetweet_count();
            }
            //查看数
            long view_count=0L;
            if(xueQiu.getView_count()!=null){
                view_count = xueQiu.getView_count();
            }
            //粉丝数
            long followers_count = 0L;
            if(xueQiu.getFollowers_count()!=null){
                followers_count = xueQiu.getFollowers_count();
            }
            //关注数
            long friends_count=0L;
            if(xueQiu.getFriends_count()!=null){
                friends_count = xueQiu.getFriends_count();
            }
            //帖子数
            long status_count=0L;
            if(xueQiu.getStatus_count()!=null){
                status_count=xueQiu.getStatus_count();
            }
            boolean is_verify = xueQiu.getVerified_info();
            if(fav_count>=0 &&like_count>=0 &&reply_count>=0 &&retweet_count>=0
                    &&view_count>=0 &&view_count<=5000&&followers_count>0&&followers_count<500&&friends_count<10*followers_count
                    &&status_count>0 && status_count<1000){
                stockComment.setFlag(1);
                stockComment.setDesc("正常股评");
            }else if(fav_count>=0 &&like_count>=0 &&reply_count>=0 &&retweet_count>=0
                    &&view_count>5000&&followers_count>500&&friends_count<followers_count
                    &&status_count>1000){
                stockComment.setFlag(2);
                stockComment.setDesc("资深股评");
            }
            stockCommentList.add(stockComment);
        }
        return stockCommentList;
    }


    public SysStockDay filterSysStockDay(List<SysStockDay> sysStockDayList,String symbol){
        for(SysStockDay sysStockDay:sysStockDayList){
            if(symbol.equals(sysStockDay.getSymbol())){
                return sysStockDay;
            }
        }
        return new SysStockDay();
    }
    public String computedPercent(String aWeekValue,String nowValue){
        if(StringUtils.isEmpty(aWeekValue) || StringUtils.isEmpty(nowValue)){
            return "0";
        }
        double week  = Double.parseDouble(aWeekValue);
        double now = Double.parseDouble(nowValue);
        double result = (now-week)/week*100;
        return String.format("%.2f",result);
    }
}
