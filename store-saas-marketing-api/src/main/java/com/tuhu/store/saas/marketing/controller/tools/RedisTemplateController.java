package com.tuhu.store.saas.marketing.controller.tools;

import com.alibaba.fastjson.JSON;
import com.beust.jcommander.internal.Lists;
import com.google.common.collect.Maps;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.connection.DataType;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.*;


/**
 * @author wangxiang2
 */
@RestController
@RequestMapping("/redis")
@Api(tags = "redis服务")
@Slf4j
public class RedisTemplateController {

    @Value("${spring.redis.prefix}")
    private String redisPrefix;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @GetMapping(value = "/get/{key}")
    @ApiOperation(value = "redis 查询")
    public Object get(@PathVariable("key") String key) {
        return this.redisGet(key);
    }

    private Object redisGet(String key) {
        Object object = null;
        DataType type = null;
        if (stringRedisTemplate.hasKey(key)) {
            type = stringRedisTemplate.type(key);
        } else {
            key = redisPrefix + key;
            if (stringRedisTemplate.hasKey(key)) {
                type = stringRedisTemplate.type(key);
            } else {
                return "找不到key,请重试！";
            }
        }
        log.info("key:{} 的类型为：{}", key, type);
        if (type.equals(DataType.STRING)) {
            //get(key)方法返回key所关联的字符串值
            object = stringRedisTemplate.opsForValue().get(key);
        } else if (type.equals(DataType.HASH)) {
            List<String> list = Lists.newArrayList();
            Map<Object, Object> entriesMap = stringRedisTemplate.opsForHash().entries(key);
            object = JSON.toJSONString(entriesMap);
        } else if (type.equals(DataType.LIST)) {
            List<String> range = (List<String>) stringRedisTemplate.boundListOps(key);
            object = JSON.toJSONString(range);
        } else if (type.equals(DataType.SET)) {
            Set<String> set = stringRedisTemplate.boundSetOps(key).members();
            object = JSON.toJSONString(set);
        }
        return object;
    }


    @GetMapping(value = "/like/get/{key}")
    @ApiOperation(value = "redis 模糊匹配")
    public Object redisLikeGet(@PathVariable("key") String key) {
        List<Object> objectList = Lists.newArrayList();
        Set<String> keys = stringRedisTemplate.keys("*" + key + "*");
        if (CollectionUtils.isNotEmpty(keys)) {
            for (String key1 : keys) {
                HashMap map = Maps.newHashMap();
                map.put(key1, this.redisGet(key1));
                objectList.add(map);
            }
        }
        return objectList;
    }


    @GetMapping(value = "/delete/{key}")
    @ApiOperation(value = "redis 删除")
    public Object redisTemplatDelete(@PathVariable("key") String key) {
        if (stringRedisTemplate.hasKey(key)) {
            stringRedisTemplate.delete(key);
        } else {
            key = redisPrefix + key;
            if (stringRedisTemplate.hasKey(key)) {
                stringRedisTemplate.delete(key);
            } else {
                return "找不到key,请重试！";
            }
        }
        return "success";
    }


}

