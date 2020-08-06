/*
 * Copyright 2018 tuhu.cn All right reserved. This software is the
 * confidential and proprietary information of tuhu.cn ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with Tuhu.cn
 */
package com.tuhu.store.saas.marketing.service.impl;

import com.tuhu.store.saas.marketing.dataobject.MessageQuantity;
import com.tuhu.store.saas.marketing.dataobject.MessageQuantityExample;
import com.tuhu.store.saas.marketing.mysql.marketing.write.dao.MessageQuantityMapper;
import com.tuhu.store.saas.marketing.service.IMessageQuantityService;
import com.tuhu.store.saas.marketing.util.IdKeyGen;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * @author xuechaofu
 * @date 2018/11/1516:40
 */
@Service
public class MessageQuantityServiceImpl implements IMessageQuantityService {

    @Autowired
    private MessageQuantityMapper quantityMapper;

    @Autowired
    private IdKeyGen idKeyGen;

    @Override
    public MessageQuantity selectQuantityByTenantIdAndStoreId(MessageQuantity select) {
        Date now = new Date();
        MessageQuantityExample quantityExample =new MessageQuantityExample();
        MessageQuantityExample.Criteria criteria =quantityExample.createCriteria();
        criteria.andTenantIdEqualTo(select.getTenantId()).andStoreIdEqualTo(select.getStoreId()).andIsDeleteEqualTo(Boolean.FALSE);
        List<MessageQuantity> list = quantityMapper.selectByExample(quantityExample);
        if(list.isEmpty()){
            String id = idKeyGen.generateId(select.getTenantId());
            MessageQuantity messageQuantity = new MessageQuantity();
            messageQuantity.setId(id);
            messageQuantity.setCreateUser(select.getCreateUser());
            messageQuantity.setCreateTime(now);
            messageQuantity.setTenantId(select.getTenantId());
            messageQuantity.setStoreId(select.getStoreId());
            messageQuantity.setInitialQuantity(1000L);
            messageQuantity.setQuantity(1000L);
            messageQuantity.setRemainderQuantity(1000L);
            quantityMapper.insert(messageQuantity);
            return messageQuantity;
        }
        return list.get(0);
    }

    @Override
    @Transactional
    public void reduceQuantity(String id, Integer usedNum, String updateUser) {
        MessageQuantity messageQuantity = quantityMapper.selectByPrimaryKey(id);
        messageQuantity.setUpdateTime(new Date());
        messageQuantity.setUpdateUser(updateUser);
        messageQuantity.setRemainderQuantity(messageQuantity.getRemainderQuantity() - usedNum);
        quantityMapper.updateByPrimaryKey(messageQuantity);
    }
}
