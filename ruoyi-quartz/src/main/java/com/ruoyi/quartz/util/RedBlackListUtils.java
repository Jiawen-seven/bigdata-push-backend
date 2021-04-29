package com.ruoyi.quartz.util;

import com.ruoyi.common.utils.StringUtils;

/**
 * @description:
 * @projectName:ruoyi
 * @see:com.ruoyi.quartz.util
 * @author:
 * @createTime:2021/4/29
 */
public class RedBlackListUtils {
    public static int computedEps(String eps){
        if(StringUtils.isEmpty(eps)){
            return 10;
        }else{
            double  e = Double.parseDouble(eps);
            if ( e < 0) {
                return 10;
            }else if(e>=0&&e<0.2){
                return 30;
            }else if(e>=0.2&&e<0.4){
                return 50;
            }else if(e>=0.4&&e<0.6){
                return 70;
            }else{
                return 90;
            }
        }
    }
    //每股净资产收益率volume_ratio
    public static int computedVolumeRatio(String volumeRatio){
        if(StringUtils.isEmpty(volumeRatio)){
            return 10;
        }else{
            double  e = Double.parseDouble(volumeRatio);
            if ( e < 0.05 || e>0.39) {
                return 10;
            }else if(e>=0.05&&e<0.07){
                return 30;
            }else if(e>=0.07&&e<0.1){
                return 50;
            }else if(e>=0.1&&e<0.15){
                return 70;
            }else if(e>=0.15&&e<0.39){
                return 90;
            }
        }
        return 0;
    }
    //计算市盈率评分pe_ttm
    public static int computedPeTtm(String peTtm){
        if(StringUtils.isEmpty(peTtm)){
            return 10;
        }else{
            double  e = Double.parseDouble(peTtm);
            if ( e < 0) {
                return 10;
            }else if(e>28){
                return 30;
            }else if(e>=21&&e<=28){
                return 50;
            }else if(e>=0&&e<=13){
                return 70;
            }else if(e>=14&&e<=20){
                return 90;
            }
        }
        return 0;
    }
    //计算市净率评分
    public static int computedPb(String pb){
        if(StringUtils.isEmpty(pb)){
            return 10;
        }else{
            double  e = Double.parseDouble(pb);
            if ( e < 0) {
                return 10;
            }else if(e>10){
                return 30;
            }else if(e>=5&&e<=10){
                return 50;
            }else if(e>=3&&e<5){
                return 70;
            }else if(e>=0&&e<3){
                return 90;
            }
        }
        return 0;
    }
}
