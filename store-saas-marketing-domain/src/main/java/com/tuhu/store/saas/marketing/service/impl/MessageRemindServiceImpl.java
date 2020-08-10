/*
 * Copyright 2018 tuhu.cn All right reserved. This software is the
 * confidential and proprietary information of tuhu.cn ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with Tuhu.cn
 */
package com.tuhu.store.saas.marketing.service.impl;

import com.google.common.collect.Lists;
import com.tuhu.store.saas.marketing.dataobject.MessageRemind;
import com.tuhu.store.saas.marketing.dataobject.MessageRemindExample;
import com.tuhu.store.saas.marketing.enums.MessageStatusEnum;
import com.tuhu.store.saas.marketing.enums.SMSTypeEnum;
import com.tuhu.store.saas.marketing.mysql.marketing.write.dao.MessageRemindMapper;
import com.tuhu.store.saas.marketing.service.IMessageRemindService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author xuechaofu
 * @date 2018/11/1513:50
 */
@Service
@Slf4j
public class MessageRemindServiceImpl implements IMessageRemindService {

    @Autowired
    private MessageRemindMapper remindMapper;

    @Override
    public void insertMessageRemindList(List<MessageRemind> list) {
        remindMapper.insertMessageRemindList(list);
    }

    @Override
    public List<MessageRemind> getAllNeedSendReminds() {
        MessageRemindExample remindExample = new MessageRemindExample();
        MessageRemindExample.Criteria statusUnsend = remindExample.createCriteria();
        MessageRemindExample.Criteria statusSendFailed = remindExample.createCriteria();

        statusUnsend.andStatusEqualTo(MessageStatusEnum.MESSAGE_WAIT.getCode()).andIsDeleteEqualTo(Boolean.FALSE).andSourceIn(Lists.newArrayList(SMSTypeEnum.MARKETING_ACTIVITY.templateCode(), SMSTypeEnum.MARKETING_COUPON.templateCode()));
        statusSendFailed.andStatusEqualTo(MessageStatusEnum.MESSAGE_FAIL.getCode()).andTryTimeGreaterThan(0).andIsDeleteEqualTo(Boolean.FALSE).andSourceIn(Lists.newArrayList(SMSTypeEnum.MARKETING_ACTIVITY.templateCode(), SMSTypeEnum.MARKETING_COUPON.templateCode()));

//        remindExample.or(statusUnsend);
        remindExample.or(statusSendFailed);

        return remindMapper.selectByExample(remindExample);
    }
}
