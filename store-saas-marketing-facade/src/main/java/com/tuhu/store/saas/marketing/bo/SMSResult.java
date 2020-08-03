package com.tuhu.store.saas.marketing.bo;

import lombok.Data;

@Data
public class SMSResult {

    /**
     * 短信发送状态
     */
    private boolean sendResult;

    /**
     * 短信发送返回状态
     */
    private String statusCode;

    /**
     * 短信发送返回值
     */
    private String statusMsg;
}
