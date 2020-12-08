package com.tuhu.store.saas.marketing.enums;


/**
 * 秒杀报名(订单)  支付状态 0:未支付 1:成功 2:失败 3:作废
 *
 * @author wangxiang2
 */

public enum SeckillRegistrationRecordPayStatusEnum {
    WZF(0, "未支付"),
    CG(1, "成功"),
    SB(2, "失败"),
    ZF(3, "作废");

    private Integer status;

    private String statusName;

    SeckillRegistrationRecordPayStatusEnum(int status, String statusName) {
        this.status = status;
        this.statusName = statusName;
    }

    public Integer getStatus() {
        return status;
    }

    public String getStatusName() {
        return statusName;
    }

}
