package com.ruoyi.web.controller.system;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;
import com.ruoyi.web.controller.socket.WebSocketServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.system.domain.SysNotice;
import com.ruoyi.system.service.ISysNoticeService;

/**
 * 公告 信息操作处理
 * 
 * @author ruoyi
 */
@RestController
@RequestMapping("/system/notice")
public class SysNoticeController extends BaseController
{
    private static final Logger log = LoggerFactory.getLogger(SysNoticeController.class);
    @Autowired
    private WebSocketServer webSocketServer;
    @Autowired
    private ISysNoticeService noticeService;

    /**
     * 获取通知公告列表
     */
    /*@PreAuthorize("@ss.hasPermi('system:notice:list')")
    @GetMapping("/list")
    public TableDataInfo list(SysNotice notice)
    {
        startPage();
        List<SysNotice> list = noticeService.selectNoticeList(notice);
        return getDataTable(list);
    }*/

    @GetMapping("/list")
    public AjaxResult list(SysNotice notice){
        List<SysNotice> list = noticeService.selectNoticeList(notice);
        Map<String,Object> map = new HashMap<>();
        map.put("list",list);
        map.put("total",list.size());
        return AjaxResult.success(map);
    }

    /**
     * 根据通知公告编号获取详细信息
     */
    @PreAuthorize("@ss.hasPermi('system:notice:query')")
    @GetMapping(value = "/{noticeId}")
    public AjaxResult getInfo(@PathVariable Long noticeId)
    {
        return AjaxResult.success(noticeService.selectNoticeById(noticeId));
    }

    /**
     * 新增通知公告
     */
    @PreAuthorize("@ss.hasPermi('system:notice:add')")
    @Log(title = "通知公告", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody SysNotice notice)
    {
        notice.setCreateBy(SecurityUtils.getUsername());
        AjaxResult ajaxResult = toAjax(noticeService.insertNotice(notice));
        webSocketServer.sendInfo(JSONObject.toJSONString(AjaxResult.success("有新消息")));
        log.info("发送websocket信号");
        return ajaxResult;
    }

    /**
     * 修改通知公告
     */
    @PreAuthorize("@ss.hasPermi('system:notice:edit')")
    @Log(title = "通知公告", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@Validated @RequestBody SysNotice notice)
    {
        notice.setUpdateBy(SecurityUtils.getUsername());
        notice.setUpdateTime(new Date());
        AjaxResult ajaxResult = toAjax(noticeService.updateNotice(notice));
        webSocketServer.sendInfo(JSONObject.toJSONString(AjaxResult.success("有新消息")));
        log.info("发送websocket信号");
        return ajaxResult;
    }

    /**
     * 删除通知公告
     */
    @PreAuthorize("@ss.hasPermi('system:notice:remove')")
    @Log(title = "通知公告", businessType = BusinessType.DELETE)
    @DeleteMapping("/{noticeId}")
    public AjaxResult remove(@PathVariable Long noticeId)
    {
        return toAjax(noticeService.deleteNoticeById(noticeId));
    }
}
