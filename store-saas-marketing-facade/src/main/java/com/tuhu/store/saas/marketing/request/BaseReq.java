package com.tuhu.store.saas.marketing.request;

import lombok.Data;

import java.io.Serializable;

@Data
public class BaseReq implements Serializable {

    private Long storeId;

    private Long tenantId;
}
