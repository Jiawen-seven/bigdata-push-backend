package com.ruoyi.quartz.mapper;

import com.ruoyi.quartz.domain.SysStockDay;
import com.ruoyi.quartz.domain.VolumeRatioEps;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 定时任务爬取股票数据Mapper接口
 * 
 * @author ruoyi
 * @date 2021-04-17
 */
public interface SysStockDayMapper 
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
     * 查询定时任务爬取股票数据列表
     *
     * @param
     * @return 定时任务爬取股票数据集合
     */
    public List<SysStockDay> selectSysStockDayListByDate(Map<String,Object> map);

    /**
     * 新增定时任务爬取股票数据
     * 
     * @param sysStockDay 定时任务爬取股票数据
     * @return 结果
     */
    public int insertSysStockDay(SysStockDay sysStockDay);

    /*
    * 批量插入
    * */
    public int batchInsertSysStockDay(List<SysStockDay> sysStockDayList);

    /**
     * 修改定时任务爬取股票数据
     * 
     * @param sysStockDay 定时任务爬取股票数据
     * @return 结果
     */
    public int updateSysStockDay(SysStockDay sysStockDay);

    /**
     * 删除定时任务爬取股票数据
     * 
     * @param id 定时任务爬取股票数据ID
     * @return 结果
     */
    public int deleteSysStockDayById(Long id);

    /**
     * 批量删除定时任务爬取股票数据
     * 
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteSysStockDayByIds(Long[] ids);

    /**
    * @Description: 根据时间获取
    * @param: [startDateTime, endDateTime]
    * @return: java.util.List<com.ruoyi.quartz.domain.SysStockDay>
    * @Date: 2021/4/17
    */
    public List<SysStockDay> selectSysStockByDay(Map<String,Object> map);
    
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
}
