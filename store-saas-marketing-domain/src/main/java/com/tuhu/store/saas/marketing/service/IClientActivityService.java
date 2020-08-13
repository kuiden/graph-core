/*
 * Copyright 2020 tuhu.cn All right reserved. This software is the
 * confidential and proprietary information of tuhu.cn ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with Tuhu.cn
 */
package com.tuhu.store.saas.marketing.service;

import com.tuhu.store.saas.marketing.request.ActivityApplyReq;
import com.tuhu.store.saas.marketing.response.ActivityApplyResp;
import com.tuhu.store.saas.marketing.response.ActivityCustomerResp;

/**
 * C端活动service
 *
 * @author xiesisi
 * @date 2020/8/1311:54
 */
public interface IClientActivityService {

    /**
     * C端，活动报名
     *
     * @param applyReq
     * @return
     */
    ActivityApplyResp clientActivityApply(ActivityApplyReq applyReq);

}