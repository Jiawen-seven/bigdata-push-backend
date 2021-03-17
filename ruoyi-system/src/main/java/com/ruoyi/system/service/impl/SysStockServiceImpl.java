package com.ruoyi.system.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.system.mapper.SysStockMapper;
import com.ruoyi.system.domain.SysStock;
import com.ruoyi.system.service.ISysStockService;

/**
 * 股票信息Service业务层处理
 * 
 * @author ruoyi
 * @date 2021-03-17
 */
@Service
public class SysStockServiceImpl implements ISysStockService 
{
    @Autowired
    private SysStockMapper sysStockMapper;

    /**
     * 查询股票信息
     * 
     * @param value 股票信息ID
     * @return 股票信息
     */
    @Override
    public SysStock selectSysStockById(String value)
    {
        return sysStockMapper.selectSysStockById(value);
    }

    /**
     * 查询股票信息列表
     * 
     * @param sysStock 股票信息
     * @return 股票信息
     */
    @Override
    public List<SysStock> selectSysStockList(SysStock sysStock)
    {
        return sysStockMapper.selectSysStockList(sysStock);
    }

    /**
     * 新增股票信息
     * 
     * @param sysStock 股票信息
     * @return 结果
     */
    @Override
    public int insertSysStock(SysStock sysStock)
    {
        return sysStockMapper.insertSysStock(sysStock);
    }

    /**
     * 修改股票信息
     * 
     * @param sysStock 股票信息
     * @return 结果
     */
    @Override
    public int updateSysStock(SysStock sysStock)
    {
        return sysStockMapper.updateSysStock(sysStock);
    }

    /**
     * 批量删除股票信息
     * 
     * @param values 需要删除的股票信息ID
     * @return 结果
     */
    @Override
    public int deleteSysStockByIds(String[] values)
    {
        return sysStockMapper.deleteSysStockByIds(values);
    }

    /**
     * 删除股票信息信息
     * 
     * @param value 股票信息ID
     * @return 结果
     */
    @Override
    public int deleteSysStockById(String value)
    {
        return sysStockMapper.deleteSysStockById(value);
    }
}
