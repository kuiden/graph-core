package com.tuhu.store.saas.marketing.response.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 优惠券适用分类远程VO
 */
@Data
public class CouponScopeCategoryDTO implements Serializable {
    private static final long serialVersionUID = 4205373481628431741L;
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

    /**
     * 分类名称
     *
     * @return
     */
    private String categoryName;
}
