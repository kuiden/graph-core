package com.tuhu.store.saas.marketing.enums;

public enum EventContentTypeEnum {
    STORE("store", "门店"),
    COUPON("coupon", "优惠券"),
    ACTIVITY("activity", "营销活动"),
    OTHER("other", "其他");

    EventContentTypeEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    private String code;

    private String desc;

    public String getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }
}
