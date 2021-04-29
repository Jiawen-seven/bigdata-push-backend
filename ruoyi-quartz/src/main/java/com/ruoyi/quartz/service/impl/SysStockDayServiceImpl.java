package com.ruoyi.quartz.service.impl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import com.ruoyi.common.constant.RequestConstants;
import com.ruoyi.common.core.redis.RedisCache;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.quartz.domain.SysStockDay;
import com.ruoyi.quartz.domain.VolumeRatioEps;
import com.ruoyi.quartz.entity.FundRanking;
import com.ruoyi.quartz.mapper.SysStockDayMapper;
import com.ruoyi.quartz.service.ISysStockDayService;
import com.ruoyi.quartz.util.PageUtils;
import org.springframework.beans.factory.annotation.Autowired;
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
    @Autowired
    private SysStockDayMapper sysStockDayMapper;

    @Autowired
    private RedisCache redisCache;

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
