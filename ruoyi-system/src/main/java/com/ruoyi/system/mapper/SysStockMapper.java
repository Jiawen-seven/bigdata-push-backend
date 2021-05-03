package com.ruoyi.system.mapper;

import java.util.List;
import com.ruoyi.system.domain.SysStock;

/**
 * 股票信息Mapper接口
 * 
 * @author ruoyi
 * @date 2021-03-17
 */
public interface SysStockMapper 
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
     * 删除股票信息
     * 
     * @param value 股票信息ID
     * @return 结果
     */
    public int deleteSysStockById(String value);

    /**
     * 批量删除股票信息
     * 
     * @param values 需要删除的数据ID
     * @return 结果
     */
    public int deleteSysStockByIds(String[] values);
    /**
     * @description 批量更新sys_stock表
     * @param
     * @see
     * @author jijj
     * @createTime 2021/5/3 12:40
     */
    public void updateSysStockBatch(List<SysStock> sysStockList);
    /**
     * @description 删除所有sys_stock表
     * @param
     * @see
     * @author jijj
     * @createTime 2021/5/3 17:52
     */
    public void deleteAll();
    /**
     * @description 批量插入
     * @param
     * @see
     * @author jijj
     * @createTime 2021/5/3 17:58
     */
    public void insertAll(List<SysStock> list);

    public void deleteIsNull();
}
