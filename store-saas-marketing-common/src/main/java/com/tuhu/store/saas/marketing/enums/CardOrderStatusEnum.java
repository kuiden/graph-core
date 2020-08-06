/*
 * Copyright 2018 tuhu.cn All right reserved. This software is the
 * confidential and proprietary information of tuhu.cn ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with Tuhu.cn
 */
package com.tuhu.store.saas.marketing.enums;

import lombok.Getter;

/**
 * @author sunkuo
 * @date 2018/11/29  14:09
 */
@Getter
public enum CardOrderStatusEnum {
    WAIT_OPENED_CARD("WAIT_OPENED_CARD","待开卡"),
    OPENED_CARD("OPENED_CARD","已开卡"),
    SETTLE_CARD("SETTLE_CARD","已结算");

    private String enumCode;

    private String description;

    CardOrderStatusEnum(String enumCode, String description) {
        this.enumCode = enumCode;
        this.description = description;
    }
}
