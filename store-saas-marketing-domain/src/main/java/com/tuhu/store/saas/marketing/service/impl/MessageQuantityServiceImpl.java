/*
 * Copyright 2018 tuhu.cn All right reserved. This software is the
 * confidential and proprietary information of tuhu.cn ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with Tuhu.cn
 */
package com.tuhu.store.saas.marketing.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.tuhu.store.saas.marketing.context.UserContextHolder;
import com.tuhu.store.saas.marketing.dataobject.MessageQuantity;
import com.tuhu.store.saas.marketing.dataobject.MessageQuantityExample;
import com.tuhu.store.saas.marketing.exception.StoreSaasMarketingException;
import com.tuhu.store.saas.marketing.mysql.marketing.write.dao.MessageQuantityMapper;
import com.tuhu.store.saas.marketing.service.IMessageQuantityService;
import com.tuhu.store.saas.marketing.util.IdKeyGen;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * @author xuechaofu
 * @date 2018/11/1516:40
 */
@Service
@Slf4j
public class MessageQuantityServiceImpl implements IMessageQuantityService {

    @Value("${marketing.message.init.num:1000}")
    private Long initNum;

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
            messageQuantity.setInitialQuantity(initNum);
            messageQuantity.setQuantity(initNum);
            messageQuantity.setOccupyQuantity(0L);
            messageQuantity.setRemainderQuantity(initNum);
            quantityMapper.insertSelective(messageQuantity);
            return messageQuantity;
        }
        MessageQuantity messageQuantity = list.get(0);
        return messageQuantity ;
    }

    @Override
    @Transactional
    public void reduceQuantity(MessageQuantity select, Integer usedNum, String updateUser, boolean releaseOccupy) {
        MessageQuantity messageQuantity = this.selectQuantityByTenantIdAndStoreId(select);
        Long occupyQuantity = 0l;
        if (messageQuantity.getOccupyQuantity() != null) {
            occupyQuantity = messageQuantity.getOccupyQuantity();
        }
        Long availableNum = messageQuantity.getRemainderQuantity() - occupyQuantity;
        if(availableNum < usedNum) {
            throw new StoreSaasMarketingException("用户剩余提醒数量不足!");
        }
        messageQuantity.setUpdateTime(new Date());
        messageQuantity.setUpdateUser(updateUser);

        messageQuantity.setRemainderQuantity(messageQuantity.getRemainderQuantity() - usedNum);
        if(releaseOccupy) {//如果占用过额度，使用短信额度会同时释放占用的额度
            messageQuantity.setOccupyQuantity(messageQuantity.getOccupyQuantity() - usedNum);
        }
        quantityMapper.updateByPrimaryKey(messageQuantity);
    }

    @Override
    public void setStoreOccupyQuantity(MessageQuantity select, Long occupyNum, String updateUser, boolean occupy) {
        MessageQuantity messageQuantity = this.selectQuantityByTenantIdAndStoreId(select);
        Long oldOccupyNum = messageQuantity.getOccupyQuantity();

        if(occupy) {//占用
            messageQuantity.setOccupyQuantity(oldOccupyNum + occupyNum);
        }else {//释放
            messageQuantity.setOccupyQuantity(oldOccupyNum - occupyNum);
        }
        messageQuantity.setUpdateTime(new Date());
        messageQuantity.setUpdateUser(updateUser);
        quantityMapper.updateByPrimaryKey(messageQuantity);
    }

    @Override
    public Long getStoreMessageQuantity(Long tenantId, Long storeId){
        MessageQuantity req = new MessageQuantity();
        req.setStoreId(storeId);
        req.setTenantId(tenantId);
        req.setCreateUser(UserContextHolder.getUser()==null?"system":UserContextHolder.getUserName());
        MessageQuantity messageQuantity = this.selectQuantityByTenantIdAndStoreId(req);
        Long availableNum = messageQuantity.getRemainderQuantity() - messageQuantity.getOccupyQuantity();
        if(availableNum < 1) {
            return 0L;
        }
        return availableNum;
    }

}
