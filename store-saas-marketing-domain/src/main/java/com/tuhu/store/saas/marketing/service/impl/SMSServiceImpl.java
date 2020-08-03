package com.tuhu.store.saas.marketing.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.tuhu.store.saas.marketing.bo.SMSResult;
import com.tuhu.store.saas.marketing.parameter.SMSParameter;
import com.tuhu.store.saas.marketing.service.ISMSService;
import com.tuhu.store.saas.marketing.util.Base64Util;
import com.tuhu.store.saas.marketing.util.GsonTool;
import com.tuhu.store.saas.marketing.util.Md5Util;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by wangxiangyun on 2018/10/9.
 */
@Service
@Slf4j
public class SMSServiceImpl implements ISMSService {

    /*云通信短信*/
    @Value("${saas.sms.yuntongxun.accountsid}")
    private String accountSid;
    @Value("${saas.sms.yuntongxun.authtoken}")
    private String authToken;
    @Value("${saas.sms.yuntongxun.baseurl}")
    private String smsBaseUrl;
    @Value("${saas.sms.yuntongxun.appid}")
    private String smsShopAppID;

    /**
     * 获取yyyyMMddHHmmss时间戳
     *
     * @return
     */
    private String getTimeStamp() {
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
        return format.format(new Date());
    }

    /**
     * 生成签名验证参数
     *
     * @param accountId
     * @param token
     * @param timeStamp
     * @return
     */
    private String getSigParameter(String accountId, String token, String timeStamp) {
        String str = accountId + token + timeStamp;
        try {
            return Md5Util.md5(str, null).toUpperCase();
        } catch (Throwable t) {
            throw new RuntimeException("Failed to MD5 digest for message:" + str, t);
        }

    }

    /**
     * 生成验证信息
     *
     * @param accountId
     * @param timeStamp
     * @return
     */
    private String getAuthorization(String accountId, String timeStamp) {
        String str = accountId + ":" + timeStamp;
        return Base64Util.encode(str, "utf-8");
    }


    @Override
    public SMSResult sendCommonSms(SMSParameter smsInfoReq) {
        final String funName = "发送云通信短信";
        log.info(" {},请求参数 : {}", funName, JSON.toJSONString(smsInfoReq));
        //获取当前时间戳
        String timeStamp = getTimeStamp();
        //获取签名验证参数
        String sigParameter = getSigParameter(accountSid, authToken, timeStamp);
        //生成完整url
        String completeUrl = String.format(smsBaseUrl, accountSid, sigParameter);
        log.info("发送短信url:{}", completeUrl);
        //生成验证信息
        String authorization = getAuthorization(accountSid, timeStamp);
        //设置HTTP标准包头字段
        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept", "application/json");
        headers.set("Content-Type", "application/json;charset=utf-8");

        headers.set("Authorization", authorization);
        //封装请求包体
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("to", smsInfoReq.getPhone());
        jsonObject.put("appId", smsShopAppID);
        jsonObject.put("templateId", smsInfoReq.getTemplateId());
        List<String> datas = smsInfoReq.getDatas();
        if (datas != null && datas.size() > 0) {
            StringBuilder sb = new StringBuilder("[");
            for (String s : datas) {
                sb.append("\"" + s + "\"" + ",");
            }
            sb.replace(sb.length() - 1, sb.length(), "]");
            jsonObject.put("datas", JSONArray.parseArray(sb.toString()));
        }
        String jsonStr = jsonObject.toString();
        System.out.println(jsonStr);
        headers.set("Content-Length", "" + jsonStr.getBytes().length);
        HttpEntity<String> entity = new HttpEntity<>(jsonStr, headers);
        RestTemplate restTemplate = new RestTemplate();
        //调用短信接口
        JSONObject resultObject = restTemplate.postForObject(completeUrl, entity, JSONObject.class);
        SMSResult smsResult = GsonTool.fromJson(resultObject.toJSONString(),SMSResult.class);
        smsResult.setSendResult(true);
        log.info("短信,请求参数：{},返回信息：{}", JSONObject.toJSONString(smsInfoReq), JSONObject.toJSONString(resultObject));
        if (!resultObject.get("statusCode").equals("000000")) {
//            throw new SmsException(String.format("发送短信出错！%s:%s;", resultObject.get("statusCode"), resultObject.get("statusMsg")));
            smsResult.setSendResult(false);
        }
        return smsResult;
    }


}
