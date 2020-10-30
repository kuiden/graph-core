package com.tuhu.store.saas.marketing.request.valueCard;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author wangyuqing
 * @since 2020/10/19 11:25
 */
@Data
public class ValueCardConsumptionReq {

    @ApiModelProperty("客户id")
    private String customerId;

    @ApiModelProperty("储值卡id")
    private Long cardId;

    private Long storeId;

    private Long tenantId;

    @ApiModelProperty("消费金额")
    private BigDecimal amount;

    @ApiModelProperty("业务订单id")
    private String orderId;

    @ApiModelProperty("业务单号")
    private String orderNo;

    @ApiModelProperty("营收单号")
    private String finNo;

    @ApiModelProperty("创建人id")
    private String createUserId;

    @ApiModelProperty("创建人姓名")
    private String createUserName;

}
