package com.ruoyi.quartz.service;

import com.alibaba.fastjson.JSONObject;
import com.ruoyi.quartz.domain.SysStockDay;
import com.ruoyi.quartz.domain.VolumeRatioEps;
import com.ruoyi.quartz.entity.FundRanking;

import java.util.List;
import java.util.Map;

/**
 * 定时任务爬取股票数据Service接口
 * 
 * @author ruoyi
 * @date 2021-04-17
 */
public interface ISysStockDayService 
{
    /**
     * 查询定时任务爬取股票数据
     * 
     * @param id 定时任务爬取股票数据ID
     * @return 定时任务爬取股票数据
     */
    public SysStockDay selectSysStockDayById(Long id);

    /**
     * 查询定时任务爬取股票数据列表
     * 
     * @param sysStockDay 定时任务爬取股票数据
     * @return 定时任务爬取股票数据集合
     */
    public List<SysStockDay> selectSysStockDayList(SysStockDay sysStockDay);

    /**
     * 新增定时任务爬取股票数据
     * 
     * @param sysStockDay 定时任务爬取股票数据
     * @return 结果
     */
    public int insertSysStockDay(SysStockDay sysStockDay);

    /**
     * 修改定时任务爬取股票数据
     * 
     * @param sysStockDay 定时任务爬取股票数据
     * @return 结果
     */
    public int updateSysStockDay(SysStockDay sysStockDay);

    /**
     * 批量删除定时任务爬取股票数据
     * 
     * @param ids 需要删除的定时任务爬取股票数据ID
     * @return 结果
     */
    public int deleteSysStockDayByIds(Long[] ids);

    /**
     * 删除定时任务爬取股票数据信息
     * 
     * @param id 定时任务爬取股票数据ID
     * @return 结果
     */
    public int deleteSysStockDayById(Long id);
    
    /**
    * @Description: 查询定时任务中爬取后存入的序列
    * @param: []
    * @return: java.util.List<com.ruoyi.quartz.domain.SysStockDay>
    * @Date: 2021/4/17
    */
    public List<SysStockDay> getSysStockListByRedisDate();
    
    /**
    * @Description: 获取基金排行榜
    * @param: []
    * @return: java.util.List<com.ruoyi.quartz.entity.FundRanking>
    * @Date: 2021/4/17
    */
    public void selectFundRanking();

    /*
     * 批量插入
     * */
    public int batchInsertSysStockDay(List<SysStockDay> sysStockDayList);

    /**
     * @Description: 获取id和symbol
     * @param: [map]
     * @return: java.util.List<java.util.Map<java.lang.String,java.lang.Object>>
     * @Date: 2021/4/18
     */
    public List<Map<String,Object>> selectSysStockMap(Map<String,Object> map);

    /*
     * 更新每股净资产收益率和每股收益
     *
     * */
    public void updateVolumeRatioEps(VolumeRatioEps map);

    /*
     * 批量更新每股净资产收益率和每股收益
     * */
    void batchUpdateVolumeRatioEps(List<VolumeRatioEps> mapList);

    /*
    * 股票红黑榜业务
    * */
    void updateStockRedBlack();
    /*
    * 获取股票红黑榜
    * */
    JSONObject getStockRedBlack();

    /*
    * 获取股票实时状态
    * */
    JSONObject getStockRealTimeStatus(String symbol);

    /*
    * 从redis获取1小时和24小时数据
    * */
    JSONObject getHourDataList(String hour);
}
