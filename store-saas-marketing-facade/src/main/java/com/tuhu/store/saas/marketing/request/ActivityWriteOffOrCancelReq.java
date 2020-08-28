/*
 * Copyright 2020 tuhu.cn All right reserved. This software is the
 * confidential and proprietary information of tuhu.cn ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with Tuhu.cn
 */
package com.tuhu.store.saas.marketing.request;

import lombok.NonNull;

import java.io.Serializable;

/**
 * @author xiesisi
 * @date 2020/8/615:58
 */

public class ActivityWriteOffOrCancelReq implements Serializable {

    private static final long serialVersionUID = 147826635597219753L;

    @NonNull
    private String activityCode;

    @NonNull
    private String telephone;
}