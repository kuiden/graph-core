package com.tuhu.store.saas.marketing.enums;

/**
 * 短信类型
 * 对应于表message_template_local template_code字段
 */
public enum SMSTypeEnum {

    /**
     * 公共模板
     * 【云雀智修】车主您好，{1}，本店{2}邀请您参加{3}活动，点击查看详情：{4},退订回N
     */
    MARKETING_ACTIVITY("MARKETING_ACTIVITY","营销活动模板"),

    /**
     * 公共模板
     * 【云雀智修】车主您好,{1}优惠券,本店{2}已送到您的手机号,点击查看详情{3},退订回N
     */
    MARKETING_COUPON("MARKETING_COUPON","优惠券模板"),

    /**
     * 公共模板
     * 【云雀智修】预约取消{1}（{2}）已取消您{3}的到店预约，如有疑问请联系门店
     */
    SAAS_STORE_CANCEL_ORDER("SAAS_STORE_CANCEL_ORDER","门店取消预约"),

    /**
     * 公共模板
     * 【云雀智修】预约成功！{1}（{2}），{3}，{4}
     */
    SAAS_STORE_ORDER_SUCCESS("SAAS_STORE_ORDER_SUCCESS","门店预约成功"),

    /**
     * 公共模板
     * 【云雀智修】预约客户{1}通过“车主小程序”预约{2}到店，汽配龙APP→我的→门店管理，查看详情
     */
    SAAS_MINI_ORDER_CREATE("SAAS_MINI_ORDER_CREATE","小程序新建预约"),

    /**
     * 公共模板
     * 【云雀智修】客户{1}通过“{2}”预约{3}到店，汽配龙APP→我的→门店管理，查看详情
     */
    SAAS_MINI_ORDER_SUCCESS("SAAS_MINI_ORDER_SUCCESS","H5预约成功给门店"),

    /**
     * 公共模板
     * 【云雀智修】预约您的手机验证码是：{1}，5分钟内有效，请勿泄露，如非本人操作，请删除本短信。
     */
    SAAS_MINI_ORDER_CREATE_CODE("SAAS_MINI_ORDER_CREATE_CODE","H5新建预约发验证码"),

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

    /**
     * 根据code获取编码
     * @param templateCode
     * @return
     */
    public static SMSTypeEnum getByCode(String templateCode){
        for (SMSTypeEnum smsTypeEnum : SMSTypeEnum.values()) {
            if(smsTypeEnum.templateCode.equals(templateCode)){
                return smsTypeEnum;
            }
        }
        return null;
    }
}
