package com.ruoyi.quartz.service;

import com.ruoyi.quartz.domain.SysStockDay;

import java.util.List;

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
}
