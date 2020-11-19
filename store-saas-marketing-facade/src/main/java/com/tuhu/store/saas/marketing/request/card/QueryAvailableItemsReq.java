package com.tuhu.store.saas.marketing.request.card;

import lombok.Data;

import java.util.Date;

/**
 * @author wangyuqing
 * @since 2020/11/19 14:06
 */
@Data
public class QueryAvailableItemsReq {

    private String customerId;

    private String customerPhoneNumber;

    /*
     * 类型 1服务 2商品
     */
    private Integer type = 1;

    /*
     * 当天开始时间
     */
    private Date date;

    private Long storeId;

    private Long tenantId;
}
