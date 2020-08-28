package com.tuhu.store.saas.marketing.dataobject;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class CustomerCardOrder implements Serializable {

    private Long storeId;

    private String customerId;

    private BigDecimal carAmount;

    private Long carNum;



}
