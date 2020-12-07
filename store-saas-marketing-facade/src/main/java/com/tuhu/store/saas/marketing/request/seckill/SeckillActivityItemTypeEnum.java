package com.tuhu.store.saas.marketing.request.seckill;

public enum SeckillActivityItemTypeEnum {

    WAIT_OPENED_CARD("GOODS", "商品", 2),
    OPENED_CARD("SERVICE", "服务", 1);


    private String enumCode;

    private String description;

    private byte type;

    SeckillActivityItemTypeEnum(String enumCode, String description, Integer type) {
        this.enumCode = enumCode;
        this.type = type.byteValue();
        this.description = description;
    }


    public static SeckillActivityItemTypeEnum getEnumByCode(byte code) {

        for (SeckillActivityItemTypeEnum validityTypeEnum : SeckillActivityItemTypeEnum.values()) {
            if (validityTypeEnum.getType() == code) {
                return validityTypeEnum;
            }
        }
        return null;
    }


    public byte getType() {
        return type;
    }

    public String getDescription() {
        return description;
    }

    public String getEnumCode() {
        return enumCode;
    }
}
