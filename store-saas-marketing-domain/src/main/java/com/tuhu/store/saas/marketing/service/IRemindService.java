/*
 * Copyright 2018 tuhu.cn All right reserved. This software is the
 * confidential and proprietary information of tuhu.cn ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with Tuhu.cn
 */
package com.tuhu.store.saas.marketing.service;

import com.tuhu.store.saas.marketing.request.SendRemindReq;

/**
 * @author xuechaofu
 * @date 2018/11/1318:43
 */
public interface IRemindService {

    /**
     * 通过客户id列表进行短信发送
     * @param sendRemindReq
     * @return
     */
    boolean send(SendRemindReq sendRemindReq);

    /**
     * 向指定的手机号发送短信通知
     *
     * @param sendRemindReq
     * @param phone
     */
    boolean sendWithPhone(SendRemindReq sendRemindReq, String phone);
}


