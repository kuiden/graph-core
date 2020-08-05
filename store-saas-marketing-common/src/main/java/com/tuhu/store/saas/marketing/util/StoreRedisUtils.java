package com.tuhu.store.saas.marketing.util;

import com.tuhu.springcloud.common.util.RedisUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.support.atomic.RedisAtomicLong;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * Redis工具类,给所有key前面加前缀
 */
@Component
@Slf4j
public class StoreRedisUtils {

    private RedisUtils redisUtils;

    private StringRedisTemplate stringRedisTemplate;

    public StoreRedisUtils(RedisUtils redisUtils, StringRedisTemplate stringRedisTemplate) {
        this.redisUtils = redisUtils;
        this.stringRedisTemplate = stringRedisTemplate;
    }

    private String keyWithPrefix(String key) {
        return redisUtils.getRedisPrefix().concat(key);
    }

    /**
     * 获取存储的信息转化为list
     *
     * @param key
     * @param clazz
     * @param <T>
     * @return
     */
    public <T> List<T> redisGetToList(String key, Class<T> clazz) {
        key = this.keyWithPrefix(key);
        return redisUtils.redisGetToList(key, clazz);
    }

    /**
     * 存储信息
     *
     * @param key
     * @param json
     */
    public void redisSet(String key, Object json) {
        key = this.keyWithPrefix(key);
        redisUtils.redisSet(key, json);
    }

    /**
     * 存储信息
     *
     * @param key
     * @param json
     */
    public void redisSet(String key, String json) {
        key = this.keyWithPrefix(key);
        redisUtils.redisSet(key, json);
    }


    /**
     * 分布式锁
     *
     * @param key
     * @param timeoutSeconds
     * @return
     */
    public Object getAtomLock(String key, long timeoutSeconds) {
        key = this.keyWithPrefix(key);
        return redisUtils.getAtomLock(key, timeoutSeconds);
    }

    /**
     * 释放锁
     *
     * @param key
     * @param value
     */
    public void releaseLock(String key, String value) {
        key = this.keyWithPrefix(key);
        redisUtils.releaseLock(key, value);
    }

    /**
     * 尝试分布式锁
     * @param key
     * @param tryTimeOutSeconds 尝试获取锁的时间
     * @param timeoutSeconds 锁释放时间
     * @return
     */
    public Object tryLock(String key,long tryTimeOutSeconds,long timeoutSeconds) {
        long nanosTimeout = TimeUnit.SECONDS.toNanos(tryTimeOutSeconds);
        final long deadline = System.nanoTime() + nanosTimeout;
        Random random = new Random();
        while (nanosTimeout>0){
            nanosTimeout = deadline - System.nanoTime();
            Object object = getAtomLock(key,timeoutSeconds);
            if(object!=null){
                return object;
            }
            /* 随机延迟 */
            try {
                Thread.sleep(random.nextInt(1000));
            } catch (InterruptedException e) {
                log.warn("获取锁等待中断:key={}", key, e);
            }
        }
        return null;
    }

    /**
     * 删除信息
     *
     * @param key
     */
    public void redisDelete(String key) {
        key = this.keyWithPrefix(key);
        redisUtils.redisDelete(key);
    }

    /**
     * 自增长
     *
     * @param key
     * @return
     */
    public Long increment(String key) {
        key = this.keyWithPrefix(key);
        return redisUtils.increment(key);
    }

    /**
     * @param key
     * @return
     */
    public String redisGet(String key) {
        key = this.keyWithPrefix(key);
        return redisUtils.redisGet(key);
    }

    /**
     * 存储信息
     *
     * @param key
     * @param json
     * @param timeout
     * @param unit
     */
    public void redisSet(String key, Object json, long timeout, TimeUnit unit) {
        key = this.keyWithPrefix(key);
        redisUtils.redisSet(key, json, timeout, unit);
    }

    /**
     * 存储信息
     *
     * @param key
     * @param json
     * @param timeout
     * @param unit
     */
    public void redisSet(String key, String json, long timeout, TimeUnit unit) {
        key = this.keyWithPrefix(key);
        redisUtils.redisSet(key, json, timeout, unit);
    }

    /**
     * 自减
     *
     * @param key
     * @return
     */
    public Long dncrement(String key) {
        key = this.keyWithPrefix(key);
        return redisUtils.dncrement(key);
    }

    /**
     * 设置过期时间
     *
     * @param key
     * @param timeout
     * @param unit
     */
    public void setExpire(String key, long timeout, TimeUnit unit) {
        key = this.keyWithPrefix(key);
        redisUtils.setExpire(key, timeout, unit);
    }

    /**
     * 指定过期时间
     *
     * @param key
     * @param date
     * @return
     */
    public Boolean expireAt(String key, final Date date) {
        key = this.keyWithPrefix(key);
        return stringRedisTemplate.expireAt(key, date);
    }

    /**
     * 自增长
     * @author liuyukun
     */
    public KeyResult incrementAndGet(String key, Date expireDate) {

        //1、拼接前缀
        key = this.keyWithPrefix(key);

        //2、获取factory
        RedisConnectionFactory factory = stringRedisTemplate.getConnectionFactory();
        Assert.notNull(factory, "stringRedisTemplate.getConnectionFactory-结果为空");

        //3、获取atomicLong
        RedisAtomicLong redisAtomicLong = new RedisAtomicLong(key, factory);
        redisAtomicLong.compareAndSet(999,0);
        redisAtomicLong.expireAt(expireDate);

        Date time = new Date();
        return (new KeyResult(redisAtomicLong.incrementAndGet(), time));
    }
}
