package com.tuhu.store.saas.marketing.response.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 优惠券可用的服务或商品
 */
@Data
public class ServiceOrderItemDTO implements Serializable {
    private static final long serialVersionUID = 8004204899016007387L;
    /**
     * 服务项目或者商品ID
     */
    private String itemId;

    /**
     * 业务分类
     */
    private String categoryCode;

    /**
     * 应收价格
     */
    private Long amount;
}
