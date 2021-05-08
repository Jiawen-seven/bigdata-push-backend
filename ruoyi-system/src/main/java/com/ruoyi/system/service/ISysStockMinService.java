package com.ruoyi.system.service;

import java.util.List;
import com.ruoyi.system.domain.SysStockMin;

/**
 * 每分钟股票详细数据Service接口
 * 
 * @author ruoyi
 * @date 2021-05-08
 */
public interface ISysStockMinService 
{
    /**
     * 查询每分钟股票详细数据
     * 
     * @param id 每分钟股票详细数据ID
     * @return 每分钟股票详细数据
     */
    public SysStockMin selectSysStockMinById(Long id);

    /**
     * 查询每分钟股票详细数据列表
     * 
     * @param sysStockMin 每分钟股票详细数据
     * @return 每分钟股票详细数据集合
     */
    public List<SysStockMin> selectSysStockMinList(SysStockMin sysStockMin);

    /**
     * 新增每分钟股票详细数据
     * 
     * @param sysStockMin 每分钟股票详细数据
     * @return 结果
     */
    public int insertSysStockMin(SysStockMin sysStockMin);

    /**
     * 修改每分钟股票详细数据
     * 
     * @param sysStockMin 每分钟股票详细数据
     * @return 结果
     */
    public int updateSysStockMin(SysStockMin sysStockMin);

    /**
     * 批量删除每分钟股票详细数据
     * 
     * @param ids 需要删除的每分钟股票详细数据ID
     * @return 结果
     */
    public int deleteSysStockMinByIds(Long[] ids);

    /**
     * 删除每分钟股票详细数据信息
     * 
     * @param id 每分钟股票详细数据ID
     * @return 结果
     */
    public int deleteSysStockMinById(Long id);
    /**
     * @description 更新所有数据为已删除
     * @param
     * @see
     * @author jijj
     * @date 2021/5/8 23:52
     */
    public void updateAllSysStockMin();
}
