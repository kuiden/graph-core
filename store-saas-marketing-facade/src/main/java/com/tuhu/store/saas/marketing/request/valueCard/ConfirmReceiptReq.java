package com.tuhu.store.saas.marketing.request.valueCard;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author wangyuqing
 * @since 2020/10/19 11:25
 */
@Data
public class ConfirmReceiptReq implements Serializable {

    private Long storeId;

    private Long tenantId;

    @ApiModelProperty("最终收费金额")
    private Long amount;
    @ApiModelProperty("变更单主键ID")
    private Long id;

    private String customerId;

}
