package com.ruoyi.quartz.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;

import java.io.Serializable;

public class SysQuote implements Serializable {
    private static final long serialVersionUID = 1L;
    private String name;
    private String symbol;
    private Double current;
    private Double percent;
    private Double chg;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public Double getCurrent() {
        return current;
    }

    public void setCurrent(Double current) {
        this.current = current;
    }

    public Double getPercent() {
        return percent;
    }

    public void setPercent(Double percent) {
        this.percent = percent;
    }

    public Double getChg() {
        return chg;
    }

    public void setChg(Double chg) {
        this.chg = chg;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("name", name)
                .append("symbol", symbol)
                .append("current", current)
                .append("percent", percent)
                .append("chg", chg)
                .toString();
    }
}
