package com.ruoyi.quartz.controller;

import java.util.List;
import java.util.Map;

import com.ruoyi.common.constant.RequestConstants;
import com.ruoyi.common.core.redis.RedisCache;
import com.ruoyi.quartz.domain.SysStockDay;
import com.ruoyi.quartz.entity.FundRanking;
import com.ruoyi.quartz.service.ISysStockDayService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
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

    @Autowired
    private RedisCache redisCache;

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
    /*
    * 获取当天爬取的股票数据
    * */
    @GetMapping("/getList")
    public TableDataInfo getSysStockListByRedis(){
        startPage();
        List<SysStockDay> list = sysStockDayService.getSysStockListByRedisDate();
        return getDataTable(list);
    }
    /*
    * 获取基金排行榜
    * */
    @GetMapping("/getFundRanking")
    public AjaxResult selectFundRanking(){
        List<FundRanking> fundRankingList = redisCache.getCacheList(RequestConstants.XUE_QIU_FUND_RANK_KEY);
        return AjaxResult.success(fundRankingList);
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
