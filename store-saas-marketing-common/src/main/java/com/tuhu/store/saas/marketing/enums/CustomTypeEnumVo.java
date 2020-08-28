package com.tuhu.store.saas.marketing.enums;


public enum CustomTypeEnumVo {
    PERSON("person", "个人"),
    COMPANY("company", "公司"),
    GOVERNMENT("government", "政府单位"),
    OTHER("other", "其他");

    private String code;
    private String desc;

    CustomTypeEnumVo(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }


    public String getCode() {
        return this.code;
    }

    public String getName() {
        return name();
    }

    public String getDesc() {
        return this.desc;
    }

    public static CustomTypeEnumVo getFromName(String name) {
        for (CustomTypeEnumVo customTypeEnum : values()) {
            if (customTypeEnum.getCode().equals(name)) {
                return customTypeEnum;
            }
        }
        return null;
    }

    public static CustomTypeEnumVo getByCode(String code) {
        for (CustomTypeEnumVo bindTypeEnum : values()) {
            if (bindTypeEnum.getCode().equals(code)) {
                return bindTypeEnum;
            }
        }
        return null;
    }
}
