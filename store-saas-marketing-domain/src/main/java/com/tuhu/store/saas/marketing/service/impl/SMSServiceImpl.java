package com.tuhu.store.saas.marketing.service.impl;

import com.alibaba.fastjson.JSON;
import com.cloopen.rest.sdk.BodyType;
import com.cloopen.rest.sdk.CCPRestSmsSDK;
import com.tuhu.store.saas.marketing.bo.SMSResult;
import com.tuhu.store.saas.marketing.parameter.SMSParameter;
import com.tuhu.store.saas.marketing.service.ISMSService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import java.util.HashMap;

/**
 * Created by wangxiangyun on 2018/10/9.
 */
@Service
@Slf4j
public class SMSServiceImpl implements ISMSService, InitializingBean {

    /*云通信短信*/
    @Value("${saas.sms.yuntongxun.accountsid}")
    private String accountSid;
    @Value("${saas.sms.yuntongxun.authtoken}")
    private String authToken;
    @Value("${saas.sms.yuntongxun.serverip}")
    private String serverIp;
    @Value("${saas.sms.yuntongxun.port}")
    private String port;
    @Value("${saas.sms.yuntongxun.appid}")
    private String smsShopAppID;

    @Autowired
    private ApplicationContext context;

    /**
     * 短信sdk
     */
    private CCPRestSmsSDK sdk = new CCPRestSmsSDK();

    @Override
    public void afterPropertiesSet() throws Exception {
        sdk.init(serverIp, port);
        sdk.setAccount(accountSid, authToken);
        sdk.setAppId(smsShopAppID);
        sdk.setBodyType(BodyType.Type_JSON);
    }

    @Override
    public SMSResult sendCommonSms(SMSParameter smsInfoReq) {
        final String funName = "发送云通信短信";
        log.info(" {},请求参数 : {}", funName, JSON.toJSONString(smsInfoReq));
        SMSResult smsResult = new SMSResult();
        String[] datas = null;
        if(smsInfoReq.getDatas()!=null){
            datas = smsInfoReq.getDatas().toArray(new String[]{});
        }

        String profile = context.getEnvironment().getActiveProfiles()[0];
        if ("test".equals(profile) || "dev".equals(profile)) {
            smsResult.setSendResult(true);
            smsResult.setStatusCode("000000");
            smsResult.setStatusMsg("短信发送成功");
            return smsResult;
        }

        HashMap<String, Object> result = sdk.sendTemplateSMS(smsInfoReq.getPhone(),smsInfoReq.getTemplateId(),datas);
        log.info(" {},返回参数 : {}", funName, JSON.toJSONString(result));
        smsResult.setSendResult(true);
        if(!"000000".equals(result.get("statusCode"))){
            //异常返回输出错误码和错误信息
            smsResult.setSendResult(false);
        }
        smsResult.setStatusCode((String) result.get("statusCode"));
        smsResult.setStatusMsg((String) result.get("statusMsg"));
        return smsResult;
    }


}
