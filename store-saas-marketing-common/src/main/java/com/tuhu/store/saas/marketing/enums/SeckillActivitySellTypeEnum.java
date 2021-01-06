package com.tuhu.store.saas.marketing.enums;


/**
 * @author wangxiang2
 */

public enum SeckillActivitySellTypeEnum {
    XZSL(1, "限制数量"),
    BXSL(2, "不限数量");

    private Integer code;

    private String statusName;

    SeckillActivitySellTypeEnum(int code, String statusName) {
        this.code = code;
        this.statusName = statusName;
    }

    public Integer getCode() {
        return code;
    }

    public String getStatusName() {
        return statusName;
    }

}
