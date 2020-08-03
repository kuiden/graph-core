/*
 * Copyright 2018 tuhu.cn All right reserved. This software is the
 * confidential and proprietary information of tuhu.cn ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with Tuhu.cn
 */
package com.tuhu.store.saas.marketing.parameter;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

/**
 * @author xiepeng
 * @date 2018/10/17 15:37
 * @desc
 */
@Getter
@Setter
public class SMSParameter implements Serializable {

    /**
     * 手机号码
     */
    private String phone;

    /**
     * 模版id
     */
    private String templateId;

    /**
     * 短信内容变量
     */
    private List<String> datas;

}
