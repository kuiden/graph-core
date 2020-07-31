package com.tuhu.store.saas.marketing.enums;

/**
 * 优惠券有效期类型
 */
public enum CouponValidityTypeEnum {
    Fixed(0, (byte) 0, "固定日期"),
    Relative(1, (byte) 1, "相对日期");
    private Integer code;
    private Byte value;
    private String desc;

    CouponValidityTypeEnum(Integer code, Byte value, String desc) {
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

    public static CouponValidityTypeEnum getEnumByCode(Integer code) {
        if (null == code) {
            return null;
        }
        for (CouponValidityTypeEnum validityTypeEnum : CouponValidityTypeEnum.values()) {
            if (validityTypeEnum.getCode().equals(code)) {
                return validityTypeEnum;
            }
        }
        return null;
    }
}
