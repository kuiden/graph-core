/*
 * Copyright 2018 tuhu.cn All right reserved. This software is the
 * confidential and proprietary information of tuhu.cn ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with Tuhu.cn
 */
package com.tuhu.store.saas.marketing.service;

import com.tuhu.store.saas.marketing.dataobject.MessageTemplateLocal;

/**
 * @author xuechaofu
 * @date 2018/11/1513:16
 */
public interface IMessageTemplateLocalService {

    /**
     * 通过消息模板id获取短信模板信息
     * @param id
     * @return
     */
    MessageTemplateLocal getTemplateLocalById(String id);

    /**
     * 通过消息模板编码和门店id获取消息模板信息
     * 优先获取私有模板不存在在获取共有模板
     * @param templateCode
     * @param storeId
     * @return
     */
    MessageTemplateLocal getTemplateLocalById(String templateCode,Long storeId);

    /**
     * 通过消息模板编码和门店id获取短信平台消息模板id
     * 优先获取私有模板不存在在获取共有模板
     * @param templateCode
     * @param storeId
     * @return
     */
    String getSMSTemplateIdByCodeAndStoreId(String templateCode,Long storeId);

}
