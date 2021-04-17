package com.ruoyi.quartz.util;


import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class PageUtils {
    public static Map<String,Object> getPage(List<?> list,int pageNum,int pageSize){
        Map<String,Object> map = new HashMap<>();
        map.put("pageNum",pageNum);
        map.put("pageSize",pageSize);
        map.put("total",list.size());
        int pages = list.size()%pageSize==0?list.size()/pageSize:list.size()/pageSize+1;
        map.put("pages",pages);
        //List分页
        list = list.stream().skip(pageSize * (pageNum - 1)).limit(pageSize).collect(Collectors.toList());
        map.put("list",list);
        return map;
    }
}
