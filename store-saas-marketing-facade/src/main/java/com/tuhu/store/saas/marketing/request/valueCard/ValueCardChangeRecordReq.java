package com.tuhu.store.saas.marketing.request.valueCard;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author wangyuqing
 * @since 2020/10/19 11:48
 */
@Data
public class ValueCardChangeRecordReq {
    @ApiModelProperty("客户id")
    private String customerId;

    @ApiModelProperty("类型 0充值和退款 1消费")
    private Integer type;

    @ApiModelProperty("页码")
    private Integer pageNum;

    @ApiModelProperty("页量")
    private Integer pageSize;

    private Long storeId;

    private Long tenantId;
}
