package com.ruoyi.web.controller.system;

import java.util.List;

import com.ruoyi.system.domain.SysStock;
import com.ruoyi.system.service.ISysStockService;
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
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 股票信息Controller
 * 
 * @author ruoyi
 * @date 2021-03-17
 */
@RestController
@RequestMapping("/system/stock")
public class SysStockController extends BaseController
{
    @Autowired
    private ISysStockService sysStockService;

    /**
     * 查询股票信息列表
     */
    @PreAuthorize("@ss.hasPermi('system:stock:list')")
    @GetMapping("/list")
    public TableDataInfo list(SysStock sysStock)
    {
        startPage();
        List<SysStock> list = sysStockService.selectSysStockList(sysStock);
        return getDataTable(list);
    }
    @GetMapping("/all")
    public AjaxResult all(SysStock sysStock)
    {
        List<SysStock> list = sysStockService.selectSysStockList(sysStock);
        return AjaxResult.success(list);
    }

    /**
     * 导出股票信息列表
     */
    @PreAuthorize("@ss.hasPermi('system:stock:export')")
    @Log(title = "股票信息", businessType = BusinessType.EXPORT)
    @GetMapping("/export")
    public AjaxResult export(SysStock sysStock)
    {
        List<SysStock> list = sysStockService.selectSysStockList(sysStock);
        ExcelUtil<SysStock> util = new ExcelUtil<SysStock>(SysStock.class);
        return util.exportExcel(list, "stock");
    }

    /**
     * 获取股票信息详细信息
     */
    @PreAuthorize("@ss.hasPermi('system:stock:query')")
    @GetMapping(value = "/{value}")
    public AjaxResult getInfo(@PathVariable("value") String value)
    {
        return AjaxResult.success(sysStockService.selectSysStockById(value));
    }

    /**
     * 新增股票信息
     */
    @PreAuthorize("@ss.hasPermi('system:stock:add')")
    @Log(title = "股票信息", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody SysStock sysStock)
    {
        return toAjax(sysStockService.insertSysStock(sysStock));
    }

    /**
     * 修改股票信息
     */
    @PreAuthorize("@ss.hasPermi('system:stock:edit')")
    @Log(title = "股票信息", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody SysStock sysStock)
    {
        return toAjax(sysStockService.updateSysStock(sysStock));
    }

    /**
     * 删除股票信息
     */
    @PreAuthorize("@ss.hasPermi('system:stock:remove')")
    @Log(title = "股票信息", businessType = BusinessType.DELETE)
	@DeleteMapping("/{values}")
    public AjaxResult remove(@PathVariable String[] values)
    {
        return toAjax(sysStockService.deleteSysStockByIds(values));
    }
}
