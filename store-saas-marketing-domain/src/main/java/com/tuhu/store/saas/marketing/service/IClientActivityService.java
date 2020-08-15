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
import com.tuhu.store.saas.marketing.response.ActivityResp;

/**
 * C端活动service
 *
 * @author xiesisi
 * @date 2020/8/1311:54
 */
public interface IClientActivityService {

    /**
     * C端H5，活动报名
     *
     * @param applyReq
     * @return
     */
    ActivityApplyResp clientActivityApply(ActivityApplyReq applyReq);

    /**
     * C端H5，活动详情
     *
     * @param encryptedCode
     * @return
     */
    ActivityResp getActivityDetailByEncryptedCode(String encryptedCode);

    /**
     * 活动详情，通用
     * @param activityCode
     * @return
     */
    ActivityResp getActivityByActivityCode(String activityCode);

    /**
     * C端H5，查询活动客户详情
     * @param encryptedCode
     * @return
     */
    ActivityCustomerResp getActivityCustomerDetail(String encryptedCode);

    /**
     * 活动活动订单二维码
     * @param activityCustomerOrderCode
     * @return
     */
    byte[] getQrCodeOfActivityCustomer(String activityCustomerOrderCode);

}