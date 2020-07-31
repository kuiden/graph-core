package com.tuhu.store.saas.marketing.request.vo;

import lombok.Data;

/**
 * 工单优惠券使用VO
 */
@Data
public class ServiceOrderCouponUseVO extends ServiceOrderCouponVO {
    private static final long serialVersionUID = 6452644375492947905L;

    /**
     * 客户优惠券ID
     */
    private Long customerCouponId;
}
