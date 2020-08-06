package com.tuhu.store.saas.marketing.request;

import io.swagger.annotations.ApiModel;
import lombok.Data;

@Data
@ApiModel("门店C端事件记录请求")
public class ClientEventRecordRequest extends EndUserVistiedCouponRequest {
    /**
     * 事件类型
     */
    private String eventType;
    /**
     * 主题类型
     */
    private String contentType;
    /**
     * 主题内容
     */
    private String contentValue;
    /**
     * 客户ID
     */
    private String customerId;
}
