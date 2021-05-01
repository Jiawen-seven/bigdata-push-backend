package com.ruoyi.quartz.dao;

import com.ruoyi.quartz.domain.XueQiu;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @description: seven_xiuqiu集合操作类
 * @projectName:ruoyi
 * @see:com.ruoyi.quartz.dao
 * @author: jijiajin
 * @createTime:2021/5/1
 */
@Component
public class XueQiuDao {
    @Autowired
    private MongoTemplate mongoTemplate;

    public void insertList(List<XueQiu> xueQiuList){
        mongoTemplate.insert(xueQiuList,XueQiu.class);
    }
    public List<XueQiu> getXueQiuList(String spiderDate,String symbol){
        Criteria criteria = Criteria.where("spiderDate").is(spiderDate).and("symbol").is(symbol);
        Query query = new Query(criteria);
        return mongoTemplate.find(query,XueQiu.class,"seven_xueqiu");
    }
}
