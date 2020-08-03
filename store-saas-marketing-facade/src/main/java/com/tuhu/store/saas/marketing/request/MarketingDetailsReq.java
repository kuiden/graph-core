package com.tuhu.store.saas.marketing.request;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.io.Serializable;

@Data
@ApiModel(value = "定向营销任务详情对象")
public class MarketingDetailsReq implements Serializable {

    private static final long serialVersionUID = 508377874738781144L;
    private Long id;

    private Long storeId;

    private Long tenantId;

    private Boolean isHide = Boolean.FALSE;

}
