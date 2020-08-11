/*
 * Copyright 2018 tuhu.cn All right reserved. This software is the
 * confidential and proprietary information of tuhu.cn ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with Tuhu.cn
 */
package com.tuhu.store.saas.marketing.service;

import com.tuhu.store.saas.crm.vo.RemindReqVO;
import com.tuhu.store.saas.marketing.dataobject.MessageRemind;

import java.util.List;

/**
 * @author xuechaofu
 * @date 2018/11/1513:49
 */
public interface IMessageRemindService {

    void insertMessageRemindList(List<MessageRemind> list);

    /**
     * 获取所有的未发送和尝试次数为0的发送失败的记录
     *
     */
    List<MessageRemind> getAllNeedSendReminds();

}


