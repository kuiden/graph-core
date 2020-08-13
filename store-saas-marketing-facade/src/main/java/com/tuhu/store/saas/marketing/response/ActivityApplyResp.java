/*
 * Copyright 2020 tuhu.cn All right reserved. This software is the
 * confidential and proprietary information of tuhu.cn ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with Tuhu.cn
 */
package com.tuhu.store.saas.marketing.response;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

/**
 * @author xiesisi
 * @date 2020/8/1317:05
 */
@Data
@ToString
@ApiModel("活动报名出参")
public class ActivityApplyResp implements Serializable {

    private static final long serialVersionUID = -8674700589576401770L;

    private String ActivityCustomerOrderCode;

    private Boolean appliedSuccess;
}