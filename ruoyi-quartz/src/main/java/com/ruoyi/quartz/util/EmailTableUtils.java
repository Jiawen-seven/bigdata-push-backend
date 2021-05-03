package com.ruoyi.quartz.util;

import com.ruoyi.quartz.entity.MailEntity;
import com.ruoyi.quartz.entity.StockComment;
import com.ruoyi.quartz.entity.StockInfo;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.List;

/**
 * @description:
 * @projectName:ruoyi
 * @see:com.ruoyi.quartz.util
 * @author:
 * @createTime:2021/4/30
 */
public class EmailTableUtils {

    public static MailEntity getTableBody(List<String> stocks, String name, String symbol,List<StockComment> stockCommentList){
        StringBuilder builder = new StringBuilder();
        builder.append("<table border=\"2\" cellspacing=\"0\"><caption>"+name+"("+symbol+")</caption><tr>");
        //拼接标题
        for(StockInfo tab: StockInfo.values()){
            builder.append("<th>").append(tab.getDescription()).append("</th>");
        }
        builder.append("</tr>").append("<tr>");
        for(String stock:stocks){
            builder.append("<td style='text-align:center;'>").append(stock).append("</td>");
        }
        builder.append("</tr>").append("</table>");
        MailEntity mailEntity  = new MailEntity();
        mailEntity.setContent(builder.toString());
        mailEntity.setSymbol(symbol);
        setStockComment(stockCommentList,mailEntity);
        return mailEntity;
    }
    public static void setStockComment(List<StockComment> stockCommentList,MailEntity mailEntity){
        if(stockCommentList!=null && stockCommentList.size()>0){
            StringBuilder builder = new StringBuilder();
            builder.append("<h2>资深股评</h2>");
            stockCommentList.forEach(s->{
                //添加头像和用户名
                builder.append("<div style=\"display:flex;align-items:center\">" +
                        "<img src=\""+s.getAvatar()+"\" style=\"width:60px\">" +
                        "<span style=\"margin-left:10px\">"+s.getScreenName()+"</span>" +
                        "</div>");
                //添加股评
                builder.append("<div style=\"width:500px\">" +
                        s.getText()+
                        "</div>");
            });
            //追加内容
            mailEntity.setContent(mailEntity.getContent()+builder.toString());
        }
    }
    /*
    * 四舍五入
    * */
    public static String decimalFilter(String value){
        if(value==null){
            return "-";
        }else{
            Double v = Double.valueOf(value);
            DecimalFormat df = new DecimalFormat("0.00");
            return df.format(v);
        }
    }
    /*
    * 涨跌幅和涨跌额格式化函数
    * */
    public static String UpsAndDownsFilter(String percent){
        String percentValue = decimalFilter(percent);
        if(!(percentValue.contains("-"))){
            double v = Double.parseDouble(percentValue);
            return "+"+v;
        }
        return percentValue;
    }
    /*
    * 涨跌幅添加百分号
    * */
    public static String percentFilter(String value){
        if(value.equals("-")){
            return value;
        }else{
            value = UpsAndDownsFilter(value);
            return value+"%";
        }
    }
    /*
    * 数值加单位
    * */
    public static String numFilter(String value){
        DecimalFormat df = new DecimalFormat("0.00");
        if(value==null){
            return "-";
        }else{
            double v = Double.parseDouble(value);
            if(v>10000){
                v = v/10000;
            }

            if(v>10000){
                v=v/1000;
                return df.format(v)+"亿";
            }else{
                return df.format(v)+"万";
            }
        }
    }
    public static String formatValue(String value,String code){
        if("chg".equals(code)){
            return UpsAndDownsFilter(value);
        }else if("percent".equals(code)){
            return percentFilter(value);
        }else if("volume".equals(code)|| "amount".equals(code)){
            return numFilter(value);
        }else{
            return decimalFilter(value);
        }
    }
}
