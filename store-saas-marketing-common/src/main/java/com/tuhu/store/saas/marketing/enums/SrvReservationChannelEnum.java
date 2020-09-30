package com.tuhu.store.saas.marketing.enums;

public enum SrvReservationChannelEnum {
    MD("MD","门店创建"),
    ZXYY("ZXYY","小程序在线预约"),
    COUPON("COUPON","优惠券营销"),
    ACTIVITY("ACTIVITY","活动营销"),
    SUBCARD("SUBCARD","次卡办理")
    ;
    private String enumCode;

    private String description;

    SrvReservationChannelEnum(String enumCode, String description) {
        this.enumCode = enumCode;
        this.description = description;
    }

    public String getEnumCode() {
        return enumCode;
    }

    public void setEnumCode(String enumCode) {
        this.enumCode = enumCode;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
