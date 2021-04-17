package com.ruoyi.quartz.task;

import com.ruoyi.quartz.request.XueQiuRequest;
import com.ruoyi.quartz.service.ISysStockDayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.ruoyi.common.utils.StringUtils;

/**
 * 定时任务调度测试
 * 
 * @author ruoyi
 */
@Component("ryTask")
public class RyTask
{
    @Autowired
    private XueQiuRequest xueQiuRequest;

    @Autowired
    private ISysStockDayService sysStockDayService;

    public void ryMultipleParams(String s, Boolean b, Long l, Double d, Integer i)
    {
        System.out.println(StringUtils.format("执行多参方法： 字符串类型{}，布尔类型{}，长整型{}，浮点型{}，整形{}", s, b, l, d, i));
    }

    public void ryParams(String params)
    {
        System.out.println("执行有参方法：" + params);
    }

    public void ryNoParams()
    {
        System.out.println("执行无参方法");
    }

    public void getXueQiuQuotes(){
        xueQiuRequest.mainGetQuote();
    }

    /*获取雪球网股票数据*/
    public void getXueQiuStock(){
        xueQiuRequest.getStockList("CN","sh_sz");
    }
    /*
    * 定时更新基金榜
    * */
    public void getFundRanking(){
        sysStockDayService.selectFundRanking();
    }
}
