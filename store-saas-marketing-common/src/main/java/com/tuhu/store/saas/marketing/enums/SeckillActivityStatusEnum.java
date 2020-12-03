package com.tuhu.store.saas.marketing.enums;

/**
 * <p>
 * 秒杀活动状态 0:未上架 1:上架 9:下架
 * </p>
 *
 * @author yangshengyong
 * @since 2020-12-03
 */
public enum SeckillActivityStatusEnum {
    WSJ(0, "未上架"),
    SJ(1, "上架"),
    XJ(9, "下架");

    private Integer status;

    private String desc;

    SeckillActivityStatusEnum(int status, String desc) {
        this.status = status;
        this.desc = desc;
    }

    public Integer getStatus() {
        return status;
    }

    public String getDesc() {
        return desc;
    }
}
