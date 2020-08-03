package com.tuhu.store.saas.marketing.request;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

/**
 * 客户报名详情请求对象
 */
@Data
@ToString
public class ActivityCustomerReq implements Serializable {

    /**
     * 主键id
     */
    private Long id;

    /**
     * 活动订单编码
     */
    private String activityOrderCode;

    /**
     * 客户ID
     */
    private String customerId;

    /**
     * 门店ID
     */
    private Long storeId;

    /**
     * 租户ID
     */
    private Long tenantId;

    /**
     * 操作人ID
     */
    private String userId;

    /**
     * 使用状态0:   未核销1：已核销2：已取消3：已开单（控制开单按钮展示）
     */
    private Integer useStatus;

    /**
     * 是否C端小程序的请求
     */
    private Boolean isFromClient = Boolean.FALSE;
}
