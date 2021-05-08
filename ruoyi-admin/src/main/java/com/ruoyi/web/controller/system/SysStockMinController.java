package com.ruoyi.web.controller.system;

import java.util.List;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.system.domain.SysStockMin;
import com.ruoyi.system.service.ISysStockMinService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 每分钟股票详细数据Controller
 * 
 * @author ruoyi
 * @date 2021-05-08
 */
@RestController
@RequestMapping("/system/min")
public class SysStockMinController extends BaseController
{
    @Autowired
    private ISysStockMinService sysStockMinService;

    /**
     * 查询每分钟股票详细数据列表
     */
    @GetMapping("/list/{symbol}")
    public AjaxResult list(@PathVariable("symbol") String symbol)
    {
        SysStockMin sysStockMin = new SysStockMin();
        sysStockMin.setIsDelete("N");
        sysStockMin.setSymbol(symbol);
        List<SysStockMin> list = sysStockMinService.selectSysStockMinList(sysStockMin);
        return AjaxResult.success(list);
    }

    /**
     * 导出每分钟股票详细数据列表
     */
    @PreAuthorize("@ss.hasPermi('system:min:export')")
    @Log(title = "每分钟股票详细数据", businessType = BusinessType.EXPORT)
    @GetMapping("/export")
    public AjaxResult export(SysStockMin sysStockMin)
    {
        List<SysStockMin> list = sysStockMinService.selectSysStockMinList(sysStockMin);
        ExcelUtil<SysStockMin> util = new ExcelUtil<SysStockMin>(SysStockMin.class);
        return util.exportExcel(list, "min");
    }

    /**
     * 获取每分钟股票详细数据详细信息
     */
    @PreAuthorize("@ss.hasPermi('system:min:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return AjaxResult.success(sysStockMinService.selectSysStockMinById(id));
    }

    /**
     * 新增每分钟股票详细数据
     */
    @PreAuthorize("@ss.hasPermi('system:min:add')")
    @Log(title = "每分钟股票详细数据", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody SysStockMin sysStockMin)
    {
        return toAjax(sysStockMinService.insertSysStockMin(sysStockMin));
    }

    /**
     * 修改每分钟股票详细数据
     */
    @PreAuthorize("@ss.hasPermi('system:min:edit')")
    @Log(title = "每分钟股票详细数据", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody SysStockMin sysStockMin)
    {
        return toAjax(sysStockMinService.updateSysStockMin(sysStockMin));
    }

    /**
     * 删除每分钟股票详细数据
     */
    @PreAuthorize("@ss.hasPermi('system:min:remove')")
    @Log(title = "每分钟股票详细数据", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(sysStockMinService.deleteSysStockMinByIds(ids));
    }
}
