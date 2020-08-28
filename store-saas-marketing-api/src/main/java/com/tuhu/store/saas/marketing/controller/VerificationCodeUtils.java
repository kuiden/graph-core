package com.tuhu.store.saas.marketing.controller;

import com.tuhu.store.saas.marketing.bo.SMSResult;
import com.tuhu.store.saas.marketing.parameter.SMSParameter;
import com.tuhu.store.saas.marketing.service.IMessageTemplateLocalService;
import com.tuhu.store.saas.marketing.service.ISMSService;
import com.tuhu.store.saas.marketing.util.StoreRedisUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * @Author: yanglanqing
 * @Date: 2020/8/12 14:33
 */
@Slf4j
@Component
public class VerificationCodeUtils {

    private static final String verificationCodeKey = "STORE_SAAS_VERI_CODE";

    @Autowired
    StoreRedisUtils storeRedisUtils;

    @Autowired
    IMessageTemplateLocalService iMessageTemplateLocalService;

    @Autowired
    ISMSService ismsService;

    /**
     * 发送验证码
     * @param templateCode 短信模板code
     * @param phoneNumber 手机号
     * @param expireTime 过期时间
     * @param unit 时间单位
     * @return
     */
    public String send(String templateCode,String phoneNumber, Integer expireTime, TimeUnit unit){
        //生成随机6位数字的验证码
        String code = String.valueOf((new Random().nextInt(899999) + 100000));
        //发送短信
        SMSParameter smsParameter = new SMSParameter();
        smsParameter.setPhone(phoneNumber);
        smsParameter.setTemplateId(iMessageTemplateLocalService.getSMSTemplateIdByCodeAndStoreId(templateCode,null));
        List<String> list = new ArrayList<>();
        list.add(code);
        smsParameter.setDatas(list);
        SMSResult sendResult = ismsService.sendCommonSms(smsParameter);
        if(sendResult != null && sendResult.isSendResult()){
            //将验证码写入verificationCodeKeyredis，并设置过期时间
            storeRedisUtils.redisSet(verificationCodeKey+phoneNumber,code);
            storeRedisUtils.setExpire(verificationCodeKey+phoneNumber, expireTime, unit);
            return "发送成功";
        }else {
            return "发送失败";
        }
    }
}
