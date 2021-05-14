package com.ruoyi.system.service.impl;

import java.util.List;

import com.ruoyi.system.domain.SysStockMin;
import com.ruoyi.system.mapper.SysStockMinMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.system.service.ISysStockMinService;

/**
 * 每分钟股票详细数据Service业务层处理
 * 
 * @author ruoyi
 * @date 2021-05-08
 */
@Service
public class SysStockMinServiceImpl implements ISysStockMinService 
{
    @Autowired
    private SysStockMinMapper sysStockMinMapper;

    /**
     * 查询每分钟股票详细数据
     * 
     * @param id 每分钟股票详细数据ID
     * @return 每分钟股票详细数据
     */
    @Override
    public SysStockMin selectSysStockMinById(Long id)
    {
        return sysStockMinMapper.selectSysStockMinById(id);
    }

    /**
     * 查询每分钟股票详细数据列表
     * 
     * @param sysStockMin 每分钟股票详细数据
     * @return 每分钟股票详细数据
     */
    @Override
    public List<SysStockMin> selectSysStockMinList(SysStockMin sysStockMin)
    {
        List<SysStockMin> list = sysStockMinMapper.selectSysStockMinList(sysStockMin);
        list.forEach(obj->{
            obj.setValue(obj.getCurrent());
        });
        return list;
    }

    /**
     * 新增每分钟股票详细数据
     * 
     * @param sysStockMin 每分钟股票详细数据
     * @return 结果
     */
    @Override
    public int insertSysStockMin(SysStockMin sysStockMin)
    {
        return sysStockMinMapper.insertSysStockMin(sysStockMin);
    }

    /**
     * 修改每分钟股票详细数据
     * 
     * @param sysStockMin 每分钟股票详细数据
     * @return 结果
     */
    @Override
    public int updateSysStockMin(SysStockMin sysStockMin)
    {
        return sysStockMinMapper.updateSysStockMin(sysStockMin);
    }

    /**
     * 批量删除每分钟股票详细数据
     * 
     * @param ids 需要删除的每分钟股票详细数据ID
     * @return 结果
     */
    @Override
    public int deleteSysStockMinByIds(Long[] ids)
    {
        return sysStockMinMapper.deleteSysStockMinByIds(ids);
    }

    /**
     * 删除每分钟股票详细数据信息
     * 
     * @param id 每分钟股票详细数据ID
     * @return 结果
     */
    @Override
    public int deleteSysStockMinById(Long id)
    {
        return sysStockMinMapper.deleteSysStockMinById(id);
    }

    @Override
    public void updateAllSysStockMin() {
        sysStockMinMapper.updateAllSysStockMin();
    }
}
