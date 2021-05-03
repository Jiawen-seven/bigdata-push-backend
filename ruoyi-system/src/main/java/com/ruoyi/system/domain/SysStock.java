package com.ruoyi.system.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 股票信息对象 sys_stock
 * 
 * @author ruoyi
 * @date 2021-03-17
 */
public class SysStock extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 股票代码 */
    private String value;

    /** 股票名称 */
    @Excel(name = "股票名称")
    private String name;
    /*股票属于哪种行业*/
    private String type;

    private String encode;

    public String getEncode() {
        return encode;
    }

    public void setEncode(String encode) {
        this.encode = encode;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setValue(String value)
    {
        this.value = value;
    }

    public String getValue() 
    {
        return value;
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
        return new ToStringBuilder(this)
                .append("value", value)
                .append("name", name)
                .append("type", type)
                .append("encode", encode)
                .toString();
    }
}
