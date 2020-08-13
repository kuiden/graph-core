package com.tuhu.store.saas.marketing.enums;

/**
 * @time 2020-08-13
 * @auther kudeng
 */
public enum EventTypeEnum {
    VISIT("visit", "访问"),
    REGISTERED("registered", "注册"),
    LOGIN("login", "登录"),
    WECHATFORWARD("wechatForward", "微信转发"),
    OTHER("other", "其他");

    private String code;
    private String desc;

    private EventTypeEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public String getCode() {
        return this.code;
    }

    public String getDesc() {
        return this.desc;
    }
}