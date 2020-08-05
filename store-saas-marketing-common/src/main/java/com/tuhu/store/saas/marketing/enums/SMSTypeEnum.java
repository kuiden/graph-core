package com.tuhu.store.saas.marketing.enums;

/**
 * 短信类型
 * 对应于表message_template_local template_code字段
 */
public enum SMSTypeEnum {

    MARKETING_ACTIVITY("MARKETING_ACTIVITY","营销活动模板"),

    MARKETING_COUPON("MARKETING_COUPON","优惠卷模板"),
    ;
    private String templateCode;

    private String desc;

    SMSTypeEnum(String templateCode, String desc) {
        this.templateCode = templateCode;
        this.desc = desc;
    }

    public String templateCode() {
        return templateCode;
    }

    public void setTemplateCode(String templateCode) {
        this.templateCode = templateCode;
    }

    public String desc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
