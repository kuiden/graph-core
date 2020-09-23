package com.tuhu.store.saas.marketing.request;

import lombok.Data;

import java.util.Date;

@Data
public class CustomerLastPurchaseDTO {
    private String customerId;
    private Date purchaseTime;
}
