package com.ruoyi.quartz.controller;

import java.util.List;

import com.ruoyi.quartz.domain.SysStockDay;
import com.ruoyi.quartz.service.ISysStockDayService;
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
 * 定时任务爬取股票数据Controller
 * 
 * @author ruoyi
 * @date 2021-04-17
 */
@RestController
@RequestMapping("/monitor/day")
public class SysStockDayController extends BaseController
{
    @Autowired
    private ISysStockDayService sysStockDayService;

    /**
     * 查询定时任务爬取股票数据列表
     */
    @PreAuthorize("@ss.hasPermi('job:day:list')")
    @GetMapping("/list")
    public TableDataInfo list(SysStockDay sysStockDay)
    {
        startPage();
        List<SysStockDay> list = sysStockDayService.selectSysStockDayList(sysStockDay);
        return getDataTable(list);
    }

    /**
     * 导出定时任务爬取股票数据列表
     */
    @PreAuthorize("@ss.hasPermi('job:day:export')")
    @Log(title = "定时任务爬取股票数据", businessType = BusinessType.EXPORT)
    @GetMapping("/export")
    public AjaxResult export(SysStockDay sysStockDay)
    {
        List<SysStockDay> list = sysStockDayService.selectSysStockDayList(sysStockDay);
        ExcelUtil<SysStockDay> util = new ExcelUtil<SysStockDay>(SysStockDay.class);
        return util.exportExcel(list, "day");
    }

    /**
     * 获取定时任务爬取股票数据详细信息
     */
    @PreAuthorize("@ss.hasPermi('job:day:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return AjaxResult.success(sysStockDayService.selectSysStockDayById(id));
    }

    /**
     * 新增定时任务爬取股票数据
     */
    @PreAuthorize("@ss.hasPermi('job:day:add')")
    @Log(title = "定时任务爬取股票数据", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody SysStockDay sysStockDay)
    {
        return toAjax(sysStockDayService.insertSysStockDay(sysStockDay));
    }

    /**
     * 修改定时任务爬取股票数据
     */
    @PreAuthorize("@ss.hasPermi('job:day:edit')")
    @Log(title = "定时任务爬取股票数据", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody SysStockDay sysStockDay)
    {
        return toAjax(sysStockDayService.updateSysStockDay(sysStockDay));
    }

    /**
     * 删除定时任务爬取股票数据
     */
    @PreAuthorize("@ss.hasPermi('job:day:remove')")
    @Log(title = "定时任务爬取股票数据", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(sysStockDayService.deleteSysStockDayByIds(ids));
    }
}
