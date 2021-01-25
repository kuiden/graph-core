package com.tuhu.store.saas.marketing.response;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class CustomerIdMarketInfo {
    private Integer count = 0;
    private BigDecimal amount = BigDecimal.ZERO;
    private String customerId;
    private String paymentStatus;
}
