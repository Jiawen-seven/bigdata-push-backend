package com.ruoyi.quartz.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 定时任务爬取股票数据对象 sys_stock_day
 * 
 * @author ruoyi
 * @date 2021-04-17
 */
public class SysStockDay extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** $column.columnComment */
    private Long id;

    /** 股票代码 */
    @Excel(name = "股票代码")
    private String symbol;

    /** 股票名称 */
    @Excel(name = "股票名称")
    private String name;

    /** 当前价 */
    @Excel(name = "当前价")
    private String current;

    /** 涨跌额 */
    @Excel(name = "涨跌额")
    private String chg;

    /** 涨跌幅 */
    @Excel(name = "涨跌幅")
    private String firstPercent;

    /** 年初至今涨跌 */
    @Excel(name = "年初至今涨跌")
    private String currentYearPercent;

    /** 成交量 */
    @Excel(name = "成交量")
    private String volume;

    /** 成交额 */
    @Excel(name = "成交额")
    private String amount;

    /** 换手率 */
    @Excel(name = "换手率")
    private String turnoverRate;

    /** 市盈率 */
    @Excel(name = "市盈率")
    private String peTtm;

    /** 市值 */
    @Excel(name = "市值")
    private String marketCapital;

    public void setId(Long id) 
    {
        this.id = id;
    }

    public Long getId() 
    {
        return id;
    }
    public void setSymbol(String symbol) 
    {
        this.symbol = symbol;
    }

    public String getSymbol() 
    {
        return symbol;
    }
    public void setName(String name) 
    {
        this.name = name;
    }

    public String getName() 
    {
        return name;
    }
    public void setCurrent(String current) 
    {
        this.current = current;
    }

    public String getCurrent() 
    {
        return current;
    }
    public void setChg(String chg) 
    {
        this.chg = chg;
    }

    public String getChg() 
    {
        return chg;
    }
    public void setFirstPercent(String firstPercent) 
    {
        this.firstPercent = firstPercent;
    }

    public String getFirstPercent() 
    {
        return firstPercent;
    }
    public void setCurrentYearPercent(String currentYearPercent) 
    {
        this.currentYearPercent = currentYearPercent;
    }

    public String getCurrentYearPercent() 
    {
        return currentYearPercent;
    }
    public void setVolume(String volume) 
    {
        this.volume = volume;
    }

    public String getVolume() 
    {
        return volume;
    }
    public void setAmount(String amount) 
    {
        this.amount = amount;
    }

    public String getAmount() 
    {
        return amount;
    }
    public void setTurnoverRate(String turnoverRate) 
    {
        this.turnoverRate = turnoverRate;
    }

    public String getTurnoverRate() 
    {
        return turnoverRate;
    }
    public void setPeTtm(String peTtm) 
    {
        this.peTtm = peTtm;
    }

    public String getPeTtm() 
    {
        return peTtm;
    }
    public void setMarketCapital(String marketCapital) 
    {
        this.marketCapital = marketCapital;
    }

    public String getMarketCapital() 
    {
        return marketCapital;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("symbol", getSymbol())
            .append("name", getName())
            .append("current", getCurrent())
            .append("chg", getChg())
            .append("firstPercent", getFirstPercent())
            .append("currentYearPercent", getCurrentYearPercent())
            .append("volume", getVolume())
            .append("amount", getAmount())
            .append("turnoverRate", getTurnoverRate())
            .append("peTtm", getPeTtm())
            .append("marketCapital", getMarketCapital())
            .append("createTime", getCreateTime())
            .toString();
    }
}
