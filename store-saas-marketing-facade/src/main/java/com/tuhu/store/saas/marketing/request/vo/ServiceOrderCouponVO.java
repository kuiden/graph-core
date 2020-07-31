package com.tuhu.store.saas.marketing.request.vo;

import lombok.Data;
import java.io.Serializable;
import java.util.List;

/**
 * 工单优惠券VO
 */
@Data
public class ServiceOrderCouponVO implements Serializable {

    /**
     * 门店ID
     */
    private String storeId;

    /**
     * 租户ID
     */
    private String tenantId;

    /**
     * 门口客户ID
     */
    private String customerId;

    /**
     * 工单ID
     */
    private String orderId;

    /**
     * 服务项目及商品集合
     */
    private List<ServiceOrderItemVO> items;

}
