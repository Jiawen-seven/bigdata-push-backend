package com.ruoyi.quartz.service.impl;

import java.util.List;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.quartz.domain.SysStockDay;
import com.ruoyi.quartz.mapper.SysStockDayMapper;
import com.ruoyi.quartz.service.ISysStockDayService;
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
}
