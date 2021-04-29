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
    private String percent;

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

    /*每股净资产*/
    @Excel(name = "每股净资产")
    private String volumeRatio;

    /*市净率*/
    private String pb;

    /*股息率*/
    private String dividendYield;

    /*每股收益*/
    private String eps;

    public String getEps() {
        return eps;
    }

    public void setEps(String eps) {
        this.eps = eps;
    }

    public String getDividendYield() {
        return dividendYield;
    }

    public void setDividendYield(String dividendYield) {
        this.dividendYield = dividendYield;
    }

    public String getPb() {
        return pb;
    }

    public void setPb(String pb) {
        this.pb = pb;
    }

    public String getVolumeRatio() {
        return volumeRatio;
    }

    public void setVolumeRatio(String volumeRatio) {
        this.volumeRatio = volumeRatio;
    }

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

    public String getPercent() {
        return percent;
    }

    public void setPercent(String percent) {
        this.percent = percent;
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
        return new ToStringBuilder(this)
                .append("id", id)
                .append("symbol", symbol)
                .append("name", name)
                .append("current", current)
                .append("chg", chg)
                .append("percent", percent)
                .append("currentYearPercent", currentYearPercent)
                .append("volume", volume)
                .append("amount", amount)
                .append("turnoverRate", turnoverRate)
                .append("peTtm", peTtm)
                .append("marketCapital", marketCapital)
                .append("volumeRatio", volumeRatio)
                .append("pb", pb)
                .append("dividendYield", dividendYield)
                .append("eps", eps)
                .toString();
    }
}
