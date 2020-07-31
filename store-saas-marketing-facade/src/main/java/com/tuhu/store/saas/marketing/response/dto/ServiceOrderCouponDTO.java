package com.tuhu.store.saas.marketing.response.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 客户工单优惠券信息
 */
@Data
public class ServiceOrderCouponDTO implements Serializable {
    private static final long serialVersionUID = 9151876420126285014L;

    /**
     * 门店ID
     */
    private String storeId;

    /**
     * 门口客户ID
     */
    private String customerId;

    /**
     * 租户ID
     */
    private String tenantId;

    /**
     * 工单ID
     */
    private String orderId;

    /**
     * 可用优惠券列表
     */
    private List<CustomerCouponDTO> usableCoupons;

    /**
     * 不可用可用优惠券列表
     */
    private List<CustomerCouponDTO> unusableCoupons;

    /**
     * 已使用的优惠券
     */
    private CustomerCouponDTO usedCoupon;
}
