package com.tuhu.store.saas.marketing.response;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

/**
 * 优惠券限定分类
 */
@Data
@ToString
public class CouponScopeCategoryResp implements Serializable {


    private static final long serialVersionUID = -7145290191120273640L;
    /**
     * 主键ID
     */
    private Long id;

    /**
     * 优惠券编码
     */
    private String couponCode;

    /**
     * 门店ID
     */
    private Long storeId;

    /**
     * 租户ID
     */
    private Long tenantId;

    /**
     * 分类code
     */
    private String categoryCode;
}
