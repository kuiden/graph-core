package com.tuhu.store.saas.marketing.response;

import lombok.Data;

@Data
public class GoodsResp {

    private String goodsId;

    /**
     * 租户id
     */
    private Long tenantId;

    /**
     * 门店id
     */
    private Long storeId;

    /**
     * 商品名称
     */
    private String goodsName;

    private String goodsCode;

    private Integer online;

    private Boolean checked = Boolean.FALSE;
}
