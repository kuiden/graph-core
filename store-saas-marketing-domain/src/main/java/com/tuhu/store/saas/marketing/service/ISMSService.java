package com.tuhu.store.saas.marketing.service;

import com.tuhu.store.saas.marketing.bo.SMSResult;
import com.tuhu.store.saas.marketing.parameter.SMSParameter;

/**
 * 发送短信的接口服务类
 * Created by wangxiangyun on 2018/10/9.
 */
public interface ISMSService {

    /**
     * 通用发送云通信短信
     * @param smsParameter
     * @return
     */
    SMSResult sendCommonSms(SMSParameter smsParameter);
}
