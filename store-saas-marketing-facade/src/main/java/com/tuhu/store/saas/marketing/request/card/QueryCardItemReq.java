package com.tuhu.store.saas.marketing.request.card;

import lombok.Data;

/**
 * @author wangyuqing
 * @since 2020/8/10 17:30
 */
@Data
public class QueryCardItemReq {

    /*
     * 卡id
     */
    private Long cardId;

    /*
     * 关键字搜索
     */
    private String search;

    /**
     * 门店ID
     */
    private Long storeId;

    /**
     * 租户ID
     */
    private Long tenantId;

    /*
     * 类型 1服务 2商品
     */
    private Integer type;
}
