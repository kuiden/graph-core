/*
 * Copyright 2018 tuhu.cn All right reserved. This software is the
 * confidential and proprietary information of tuhu.cn ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with Tuhu.cn
 */
package com.tuhu.store.saas.marketing.service;

import com.tuhu.store.saas.marketing.dataobject.MessageQuantity;

/**
 * @author xuechaofu
 * @date 2018/11/1516:39
 */
public interface IMessageQuantityService {

    /**
     * 获取租户剩余信息提醒次数
     * @param select
     * @return
     */
    MessageQuantity selectQuantityByTenantIdAndStoreId(MessageQuantity select);

    /**
     * 通过租户id和门店id减用户可用次数
     * @param reduceMessage
     */
    void reduceQuantity(MessageQuantity reduceMessage);
}


