/*
 * Copyright 2018 tuhu.cn All right reserved. This software is the
 * confidential and proprietary information of tuhu.cn ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with Tuhu.cn
 */
package com.tuhu.store.saas.marketing.service.impl;

import com.tuhu.store.saas.marketing.dataobject.MessageTemplateLocal;
import com.tuhu.store.saas.marketing.dataobject.MessageTemplateLocalExample;
import com.tuhu.store.saas.marketing.mysql.marketing.write.dao.MessageTemplateLocalMapper;
import com.tuhu.store.saas.marketing.service.IMessageTemplateLocalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author xuechaofu
 * @date 2018/11/1513:17
 */
@Service
public class MessageTemplateLocalServiceImpl implements IMessageTemplateLocalService {

    @Autowired
    private MessageTemplateLocalMapper templateLocalMapper;

    @Override
    public MessageTemplateLocal getTemplateLocalById(String id) {
        return templateLocalMapper.selectByPrimaryKey(id);
    }

    @Override
    public MessageTemplateLocal getTemplateLocalById(String templateCode, Long storeId) {
        MessageTemplateLocalExample privateExample = new MessageTemplateLocalExample();
        MessageTemplateLocalExample.Criteria privateCriteria =privateExample.createCriteria();
        privateCriteria.andTypeEqualTo("PRIVATE")
                .andIsDeleteEqualTo(Boolean.FALSE)
                .andStoreIdEqualTo(storeId)
                .andTemplateCodeEqualTo(templateCode);
        List<MessageTemplateLocal> list = templateLocalMapper.selectByExample(privateExample);
        if(list==null||list.size()<=0){
            MessageTemplateLocalExample publicExample = new MessageTemplateLocalExample();
            MessageTemplateLocalExample.Criteria publicCriteria =publicExample.createCriteria();
            publicCriteria.andTypeEqualTo("PUBLIC")
                    .andIsDeleteEqualTo(Boolean.FALSE)
                    .andTemplateCodeEqualTo(templateCode);
            list.addAll(templateLocalMapper.selectByExample(publicExample));
        }
        if(list==null||list.size()<=0){
            return null;
        }
        return list.get(0);
    }

}
