/*
 * Copyright 2020 tuhu.cn All right reserved. This software is the
 * confidential and proprietary information of tuhu.cn ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with Tuhu.cn
 */
package com.tuhu.store.saas.marketing.enums;


/**
 * @author xiesisi
 *
 * 活动订单状态 枚举
 * @date 2020/8/715:04
 */
public enum  MarketingCustomerUseStatusEnum {

    AC_ORDER_NEVER_USE(0,"未核销"),
    AC_ORDER_IS_USED(1,"已核销"),
    AC_ORDER_IS_CANCELED(2,"被取消"),
    AC_ORDER_CLOSE(3,"已开单")
    ;

    private Integer status;
    private String desc;


    MarketingCustomerUseStatusEnum(Integer status, String desc) {
        this.status = status;
        this.desc = desc;
    }

    public Integer getStatus() {
        return status;
    }


}