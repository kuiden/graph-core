package com.tuhu.store.saas.marketing.response;

import lombok.Data;

@Data
public class GoodsResp {

    private String id;

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
    private String name;

    /**
     * spu表主键id
     */
    private Long spuId;

    /**
     * 品牌id
     */
    private Long brandId;

    /**
     * 云商品名称
     */
    private String spuName;

    /**
     * 云商品编码
     */
    private String spuCode;

    private Boolean checked = Boolean.FALSE;
}
