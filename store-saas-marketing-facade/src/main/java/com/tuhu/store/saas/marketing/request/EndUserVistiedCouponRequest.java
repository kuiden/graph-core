package com.tuhu.store.saas.marketing.request;

import io.swagger.annotations.ApiModel;
import lombok.Data;

@Data
@ApiModel("C端用户访问优惠券请求")
public class EndUserVistiedCouponRequest extends EndUserVistiedStoreRequest {

    /**
     * C端用户的openId
     */
    private String openId;

    /**
     * 优惠券编码
     */
    private String encryptedCode;
}
