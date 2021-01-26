package com.tuhu.store.saas.marketing.response.valueCard;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author wangyuqing
 * @since 2020/10/19 10:33
 */
@Data
public class QueryValueCardListResp {
    private Long cardId;

    private String customerId;

    @ApiModelProperty("客户姓名")
    private String customerName;

    @ApiModelProperty("客户手机号")
    private String customerPhone;

    @ApiModelProperty("总余额 = 本金+赠送")
    private BigDecimal amount;

    @ApiModelProperty("赠送金")
    private BigDecimal presentAmount;

    @ApiModelProperty("本金")
    private BigDecimal principalAmount;

}
