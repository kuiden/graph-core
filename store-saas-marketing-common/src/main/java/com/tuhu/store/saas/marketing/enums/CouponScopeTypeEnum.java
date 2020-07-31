package com.tuhu.store.saas.marketing.enums;

/**
 * 优惠券适用范围
 */
public enum CouponScopeTypeEnum {
    Unlimited(0, (byte) 0, "不限"),
    Goods(1, (byte) 1, "限定商品"),
    Category(2, (byte) 2, "限定分类");
    private Integer code;
    private Byte value;
    private String desc;

    CouponScopeTypeEnum(Integer code, Byte value, String desc) {
        this.code = code;
        this.value = value;
        this.desc = desc;
    }

    public Integer getCode() {
        return this.code;
    }

    public Byte value() {
        return this.value;
    }

    public String getDesc() {
        return this.desc;
    }

    public static CouponScopeTypeEnum getEnumByCode(Integer code) {
        if (null == code) {
            return null;
        }
        for (CouponScopeTypeEnum couponScopeTypeEnum : CouponScopeTypeEnum.values()) {
            if (couponScopeTypeEnum.getCode().equals(code)) {
                return couponScopeTypeEnum;
            }
        }
        return null;
    }
}
