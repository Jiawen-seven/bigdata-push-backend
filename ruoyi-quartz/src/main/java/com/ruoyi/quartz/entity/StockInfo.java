package com.ruoyi.quartz.entity;

/**
 * @description:
 * @projectName:ruoyi
 * @see:com.ruoyi.quartz.entity
 * @author:
 * @createTime:2021/4/30
 */
public enum StockInfo {
    ClosingPrice("last_close", "收盘价"),
    AVG("avg_price","均价"),
    HighPrice("high", "最高价"),
    LOW("low","最低价"),
    UpsAndDowns("chg", "涨跌额"),
    QuoteChange("percent","涨跌幅"),
    Volume("volume","成交量"),
    Turnover("amount","成交额");

    StockInfo(String number, String description) {
        this.code = number;
        this.description = description;
    }
    private String code;
    private String description;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
