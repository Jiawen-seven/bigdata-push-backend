package com.ruoyi.system.domain;

import java.math.BigDecimal;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 每分钟股票详细数据对象 sys_stock_min
 * 
 * @author ruoyi
 * @date 2021-05-08
 */
public class SysStockMin extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键ID */
    private Long id;

    /** 当前价 */
    @Excel(name = "当前价")
    private Double current;

    /** 涨跌幅 */
    @Excel(name = "涨跌幅")
    private Double percent;

    /** 涨跌额 */
    @Excel(name = "涨跌额")
    private Double chg;

    /** 平均价格 */
    @Excel(name = "平均价格")
    private Double avgPrice;

    /** 每股收益 */
    @Excel(name = "每股收益")
    private Double volume;

    /** 插入时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "插入时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date insertTime;

    /** 是否删除，Y为是，N为否 */
    @Excel(name = "是否删除，Y为是，N为否")
    private String isDelete;

    /** 股票代码 */
    @Excel(name = "股票代码")
    private String symbol;

    /** 股票名称 */
    @Excel(name = "股票名称")
    private String name;

    public void setId(Long id) 
    {
        this.id = id;
    }

    public Long getId() 
    {
        return id;
    }
    public void setCurrent(Double current)
    {
        this.current = current;
    }

    public Double getCurrent()
    {
        return current;
    }
    public void setPercent(Double percent)
    {
        this.percent = percent;
    }

    public Double getPercent()
    {
        return percent;
    }
    public void setChg(Double chg)
    {
        this.chg = chg;
    }

    public Double getChg()
    {
        return chg;
    }
    public void setAvgPrice(Double avgPrice)
    {
        this.avgPrice = avgPrice;
    }

    public Double getAvgPrice()
    {
        return avgPrice;
    }
    public void setVolume(Double volume)
    {
        this.volume = volume;
    }

    public Double getVolume()
    {
        return volume;
    }
    public void setInsertTime(Date insertTime) 
    {
        this.insertTime = insertTime;
    }

    public Date getInsertTime() 
    {
        return insertTime;
    }
    public void setIsDelete(String isDelete) 
    {
        this.isDelete = isDelete;
    }

    public String getIsDelete() 
    {
        return isDelete;
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

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("current", getCurrent())
            .append("percent", getPercent())
            .append("chg", getChg())
            .append("avgPrice", getAvgPrice())
            .append("volume", getVolume())
            .append("insertTime", getInsertTime())
            .append("isDelete", getIsDelete())
            .append("symbol", getSymbol())
            .append("name", getName())
            .toString();
    }
}
