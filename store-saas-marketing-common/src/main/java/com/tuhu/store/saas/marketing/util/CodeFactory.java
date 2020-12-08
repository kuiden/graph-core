package com.tuhu.store.saas.marketing.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import java.text.SimpleDateFormat;
import java.util.Date;

@Component
@Slf4j
/**
 * 优惠券编码生成工厂
 */
public class CodeFactory {

    @Autowired
    private StringRedisTemplate redisTemplate;

    //优惠券编码
    public static final String couponRedisPrefix = "COUPON_CODE:";

    public  static  final  String  customerCouponPrefix ="customerCoupon";

    //优惠券编码序号前缀
    public static final String couponCodePrefix = "YHQ";

    //编码加密秘钥
    public static final String codeSalt = "saas";

    //营销活动编码
    public static final String activityRedisPrefix = "ACTIVITY_CODE:";


    //营销活动编码序号前缀
    public static final String activityCodePrefix = "YXHD";


    //秒杀活动编码
    public static final String SECKILL_ACTIVITY_PREFIX_CODE = "SECKILL_ACTIVITY_CODE:";

    //秒杀活动编码序号前缀
    public static final String SECKILL_ACTIVITY_CODE_PREFIX = "MSHD";

    /**
     * 获取指定门店当天的活动数
     *
     * @param storeId
     * @return
     */
    public String getCodeNumber(String prefix, Long storeId) {
        Date date = DataTimeUtil.getDateStartTime(new Date());
        SimpleDateFormat sdf = new SimpleDateFormat("yyMMdd");
        String dateStr = sdf.format(date);
        String key = prefix + storeId + dateStr;
        Long currentValue = redisTemplate.opsForValue().increment(key, 1L);
        String codeNumber = formatCodeWithZero(4, currentValue);
        return dateStr.concat(codeNumber);
    }

    /**
     *
     *
     * @param storeId
     * @return
     */
    public String getCodeNumberv2(String prefix, Long storeId) {
        Date date = DataTimeUtil.getDateStartTime(new Date());
        SimpleDateFormat sdf = new SimpleDateFormat("yyMMdd");
        String dateStr = sdf.format(date);
        String key = prefix + storeId + dateStr;
        Long currentValue = redisTemplate.opsForValue().increment(key, 1L);
        String codeNumber = formatCodeWithZero(4, currentValue);
        return  dateStr.concat(codeNumber);
    }

    /**
     * 格式化数字，左补零
     *
     * @param length
     * @return
     */
    public String formatCodeWithZero(int length, Long number) {
        StringBuilder format = new StringBuilder("%0").append(length).append("d");
        return String.format(format.toString(), number);
    }

    public String generateCouponCode(Long storeId, String codeNumber) {
        String storeIdStr = formatCodeWithZero(4, storeId);
        String couponCode = couponCodePrefix.concat(storeIdStr).concat(codeNumber);
        log.info("redis生成序列:{},生成的优惠券编码:{}", codeNumber, couponCode);
        return couponCode;
    }
    public String generateCustomerCouponCode(Long storeId, String codeNumber) {
        String storeIdStr = formatCodeWithZero(4, storeId);
        String couponCode = customerCouponPrefix.concat(storeIdStr).concat(codeNumber);
        log.info("redis生成序列:{},生成的优惠券编码:{}", codeNumber, couponCode);
        return couponCode;
    }

    public String generateActivityCode(Long storeId, String codeNumber) {
        String storeIdStr = formatCodeWithZero(4, storeId);
        String activityCode = activityCodePrefix.concat(storeIdStr).concat(codeNumber);
        log.info("redis生成序列:{},生成的营销活动编码:{}", codeNumber, activityCode);
        return activityCode;
    }

    public String generateSeckillActivityCode(Long storeId, String codeNumber) {
        String storeIdStr = formatCodeWithZero(4, storeId);
        String activityCode = SECKILL_ACTIVITY_CODE_PREFIX.concat(storeIdStr).concat(codeNumber);
        log.info("redis生成序列:{},生成的营销活动编码:{}", codeNumber, activityCode);
        return activityCode;
    }
}
