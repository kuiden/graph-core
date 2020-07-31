package com.tuhu.store.saas.marketing.enums;

/**
 * 优惠券类型
 */
public enum CouponTypeEnum {
    Money(0, (byte) 0, "代金券"),
    Percentage(1, (byte) 1, "折扣券");
    private Integer code;
    private Byte value;
    private String desc;

    CouponTypeEnum(Integer code, Byte value, String desc) {
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

    public static CouponTypeEnum getEnumByCode(Integer code) {
        if (null == code) {
            return null;
        }
        for (CouponTypeEnum couponTypeEnum : CouponTypeEnum.values()) {
            if (couponTypeEnum.getCode().equals(code)) {
                return couponTypeEnum;
            }
        }
        return null;
    }
}
