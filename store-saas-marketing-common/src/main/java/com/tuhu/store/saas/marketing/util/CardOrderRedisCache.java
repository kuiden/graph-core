/*
 * Copyright 2018 tuhu.cn All right reserved. This software is the
 * confidential and proprietary information of tuhu.cn ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with Tuhu.cn
 */
package com.tuhu.store.saas.marketing.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * @author sunkuo
 * @date 2018/12/19  14:09
 */
@Slf4j
@Component
public class CardOrderRedisCache {

    private static String prefix = "CARDORDER:KKD:NO:";

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Resource(name = "stringRedisTemplate")
    private ValueOperations<String, String> valueOperations;

    /**
     * 订单前缀（采购单分为采购单和订购单）
     * @param orderPrefix
     * @return
     */
    public String getCode(String orderPrefix, Long storeId) {
        SimpleDateFormat format = new SimpleDateFormat("yyMMdd");
        String dateStr = format.format(new Date());
        String key = orderPrefix + storeId + dateStr;
        increment(key);
        String s = get(key);
        if(org.apache.commons.lang.StringUtils.isBlank(s) || "1".equals(s)) {
            // 设置超时时间1天
            redisTemplate.opsForValue().set(getRedisKey(key), s, 1, TimeUnit.DAYS);
        }
        return convertToOrderCode(s);
    }

    /**
     * 自增数值转换为三字符字符串（以0填充空位）
     * @param s
     * @return
     */
    private String convertToOrderCode(String s){
        int codeSize = 3;
        StringBuilder sb = new StringBuilder(s);
        for (int i = 0 ;i < codeSize - s.trim().length(); i++){
            sb.insert(0, "0");
        }
        return sb.toString();
    }

    public String get(String key) {
        long start = System.currentTimeMillis();
        String str = valueOperations.get(getRedisKey(key));
        long end = System.currentTimeMillis();
        log.info("查询key:{},执行时间:{},返回值:{}",key,end - start, str);
        return str;
    }

    public void increment(String key) {
        long start = System.currentTimeMillis();
        valueOperations.increment(getRedisKey(key), 1);
        long end = System.currentTimeMillis();
        log.info("新增key:{},执行时间:{}",key,end - start);
    }

    public void del(String key) {
        long start = System.currentTimeMillis();
        redisTemplate.delete(getRedisKey(key));
        long end = System.currentTimeMillis();
        log.info("删除key:{},执行时间:{}",key, end - start);
    }

    public String getRedisKey(String key) {
        if (org.apache.commons.lang.StringUtils.isNotBlank(prefix)) {
            return this.prefix + key;
        } else {
            return key;
        }
    }
}
