package com.tuhu.store.saas.marketing.request.seckill;

public enum AttachedInfoTypeEnum {

    SECKILLACTIVITYRULESINFO("SeckillActivityRulesInfo", "秒杀活动规则详情"),
    SECKILLACTIVITYSTOREINFO("SeckillActivityStoreInfo", "秒杀活动门店描述");


    private String enumCode;

    private String description;



    AttachedInfoTypeEnum(String enumCode, String description) {
        this.enumCode = enumCode;

        this.description = description;
    }


    public static AttachedInfoTypeEnum getEnumByCode(String code) {

        for (AttachedInfoTypeEnum validityTypeEnum : AttachedInfoTypeEnum.values()) {
            if (validityTypeEnum.getEnumCode().equals(code)) {
                return validityTypeEnum;
            }
        }
        return null;
    }




    public String getDescription() {
        return description;
    }

    public String getEnumCode() {
        return enumCode;
    }
}
