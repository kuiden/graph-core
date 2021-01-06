package com.tuhu.store.saas.marketing.request.card;


import lombok.Data;

import java.io.Serializable;

/**
 * 导入储值卡模板
 */
@Data
public class ValueCardReq implements Serializable {

    private String customerId;

    private Long storeId;

    private Long tenantId;

    private String amount;

    private String presentAmount;

    private String userId ;

    private String errorMsg;
}