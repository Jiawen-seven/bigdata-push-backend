package com.ruoyi.system.service;

import java.util.List;
import com.ruoyi.system.domain.SysStock;

/**
 * 股票信息Service接口
 * 
 * @author ruoyi
 * @date 2021-03-17
 */
public interface ISysStockService 
{
    /**
     * 查询股票信息
     * 
     * @param value 股票信息ID
     * @return 股票信息
     */
    public SysStock selectSysStockById(String value);

    /**
     * 查询股票信息列表
     * 
     * @param sysStock 股票信息
     * @return 股票信息集合
     */
    public List<SysStock> selectSysStockList(SysStock sysStock);

    /**
     * 新增股票信息
     * 
     * @param sysStock 股票信息
     * @return 结果
     */
    public int insertSysStock(SysStock sysStock);

    /**
     * 修改股票信息
     * 
     * @param sysStock 股票信息
     * @return 结果
     */
    public int updateSysStock(SysStock sysStock);

    /**
     * 批量删除股票信息
     * 
     * @param values 需要删除的股票信息ID
     * @return 结果
     */
    public int deleteSysStockByIds(String[] values);

    /**
     * 删除股票信息信息
     * 
     * @param value 股票信息ID
     * @return 结果
     */
    public int deleteSysStockById(String value);
}
