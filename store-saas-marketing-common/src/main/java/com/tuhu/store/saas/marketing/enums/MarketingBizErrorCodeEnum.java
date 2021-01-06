package com.tuhu.store.saas.marketing.enums;

import com.tuhu.boot.common.enums.BizEnum;
import io.swagger.annotations.ApiParam;

/**
 * 错误代码枚举 供自定义错误类型使用
 */
public enum MarketingBizErrorCodeEnum implements BizEnum {

    OPERATION_FAILED(4000, "OPERATION_FAILED", "操作失败"),
    DATA_NOT_EXIST(4001, "DATA_NOT_EXIST", "数据不存在"),
    DATA_OFF_SALE(4002, "DATA_OFF_SALE", "数据已下架"),
    DATA_HAS_EXPIRED(4003, "DATA_HAS_EXPIRED", "数据已过期"),
    DATA_NOT_BEGUN(4004, "DATA_NOT_BEGUN", "数据未到开始时间"),
    DATA_HAS_UESD(4005, "DATA_HAS_UESD", "数据已被使用"),
    DATA_REACHED_MAXIMUM(4005, "DATA_REACHED_MAXIMUM", "数据已达上限"),
    REPEAT_DATA_VEHICLE(4006, "REPEAT_DATA_VEHICLE", "重复车辆数据"),
    VEHICLE_HAS_ORDER(4007, "VEHICLE_HAS_ORDER", "车辆关联订单"),
    TOO_MANY_REQUEST(4009, "TOO_MANY_REQUEST", "操作过于频繁"),

    ACTIVITY_CODE_NOT_INPUT(4050,"ACTIVITY_ENCRYPTED_CODE_NOT_INPUT","未输入活动编号"),
    ACTIVITY_APPLY_FAILED(4051,"ACTIVITY_APPLY_FAILED","活动报名失败"),
    ACTIVITY_NOT_EXIST(4052,"ACTIVITY_NOT_EXIST","此活动不存在"),
    AC_ORDER_CODE_NOT_INPUT(4053,"ACTIVITY_CUSTOMER_ORDER_CODE_NOT_INPUT","未输入活动订单号"),
    AC_ORDER_NOT_EXIST(4054,"ACTIVITY_CUSTOMER_NOT_EXIST","此活动订单不存在"),


    SYSTEM_INNER_ERROR(5000, "SYSTEM_INNER_ERROR", "系统内部错误"),

    PARAM_ERROR(6000, "PARAM_ERROR", "参数错误"),
    SECKILL_ACTIVITY_PARAM_ERROR(6001,"SECKILL_ACTIVITY_PARAM_ERROR","秒杀活动抢购失败");


    private int code;
    private String name;
    private String desc;

    MarketingBizErrorCodeEnum(int code, String name, String desc) {

        this.code = code;
        this.name = name;
        this.desc = desc;
    }

    @Override
    public int getCode() {
        return this.code;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public String getDesc() {
        return this.desc;
    }
}
