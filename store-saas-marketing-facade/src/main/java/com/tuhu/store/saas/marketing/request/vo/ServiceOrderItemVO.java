package com.tuhu.store.saas.marketing.request.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * 工单item
 */
@Data
public class ServiceOrderItemVO implements Serializable {
    private static final long serialVersionUID = 2590700960725233601L;

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
