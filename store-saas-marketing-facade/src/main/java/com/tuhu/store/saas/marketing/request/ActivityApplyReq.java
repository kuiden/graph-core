package com.tuhu.store.saas.marketing.request;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

/**
 * 活动报名请求
 */
@Data
@ToString
public class ActivityApplyReq implements Serializable {
    /**
     * 门店ID
     */
    private Long storeId;

    /**
     * 租户ID
     */
    private Long tenantId;
    /**
     * 客户ID
     */
    private String customerId;

    /**
     * 活动编码密文
     */
    private String encryptedCode;

    /**
     * 车主手机号
     */
    private String telephone;

    /**
     * 车主姓名
     */
    private String customerName;

    /**
     * 验证码
     */
    private String verificationCode;
}
