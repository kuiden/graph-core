package com.tuhu.store.saas.marketing.constant;

/**
 * <p>
 * 秒杀常量
 * </p>
 *
 * @author yangshengyong
 * @since 2020-12-02
 */
public class SeckillConstant {
    public static final Integer PAY_SUCCESS_STATUS = 1;//支付状态 成功

    public static final Integer PAY_FAIL_STATUS = 2;//支付状态 失败

    public static final Integer STATUS = -1;//查询未开始或进行中

    public static final Integer TYPE = 0;

    public static final Integer TYPE_1 = 1;

    public static final Integer SCALE = 4;//4位小数

    public static final Integer NEW_SCALE = 1;

    public static final Integer CANCEL_STATUS = 0; //作废、取消


    public static final Integer CARD_EXPIRY_DATE_TYPE_1 = 1;//有效期类型   1永久有效

    public static final String REGISTERED = "registered";//注册标识


    //秒杀活动总库存
    public static final String SECKILL_ACTIVITY_ZKC = "seckill_activity_zkc:";

    //秒杀活动预占库存
    public static final String SECKILL_ACTIVITY_YZKC = "seckill_activity_yzkc:";

    //秒杀活动已下单库存
    public static final String SECKILL_ACTIVITY_YXDKC = "seckill_activity_yxdkc:";


    //秒杀活动
    public static final String SECKILL_ACTIVITY = "seckill_activity:";
}
