/*
 * Copyright 2018 tuhu.cn All right reserved. This software is the
 * confidential and proprietary information of tuhu.cn ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with Tuhu.cn
 */
package com.tuhu.store.saas.marketing.enums;

/**
 * @author sunkuo
 * @date 2018/11/21  18:28
 */
public enum CardTemplateStatusEnum {
    ENABLE("启用"),
    DISABLE("停用");

    private String name;

    CardTemplateStatusEnum(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

}
