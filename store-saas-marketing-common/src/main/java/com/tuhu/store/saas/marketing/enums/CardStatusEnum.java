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
 * @date 2018/11/29  11:01
 */
@Getter
public enum CardStatusEnum {
    INACTIVATED("INACTIVATED","未激活",4),
    ACTIVATED("ACTIVATED","已激活",1),
    EXPIRED("EXPIRED","已过期",3),
    FINISHED("FINISHED","已用完",2);


    private String enumCode;

    private String description;

    private Integer sort;

    CardStatusEnum(String enumCode, String description,Integer sort) {
        this.enumCode = enumCode;
        this.description = description;
        this.sort = sort;
    }

    public static Integer getSort(String code){
        CardStatusEnum[] enums = values();
        for (CardStatusEnum codeEnum : enums) {
            if (codeEnum.getEnumCode().equals(code)) {
                return codeEnum.getSort();
            }
        }
        return  CardStatusEnum.INACTIVATED.getSort();
    }
}
