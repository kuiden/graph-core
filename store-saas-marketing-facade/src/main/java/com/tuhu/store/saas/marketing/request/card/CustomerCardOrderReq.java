package com.tuhu.store.saas.marketing.request.card;


import com.tuhu.store.saas.marketing.request.BasePageReq;
import lombok.Data;

@Data
public class CustomerCardOrderReq extends BasePageReq {
    private String customerId;
    private Long storeId;
    private Long tenantId;
}
