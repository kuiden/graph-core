package com.tuhu.store.saas.marketing.request.valueCard;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author wangyuqing
 * @since 2020/10/19 10:39
 */
@Data
public class CustomerValueCardDetailReq {
    @ApiModelProperty("客户id")
    private String customerId;

    @ApiModelProperty("储值卡id")
    private Long cardId;

    @ApiModelProperty("页码")
    private Integer pageNum;

    @ApiModelProperty("页量")
    private Integer pageSize;

    private Long storeId;

    private Long tenantId;
}
