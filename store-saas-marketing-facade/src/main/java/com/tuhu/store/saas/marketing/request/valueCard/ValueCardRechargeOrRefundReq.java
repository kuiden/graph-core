package com.tuhu.store.saas.marketing.request.valueCard;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author wangyuqing
 * @since 2020/10/19 11:16
 */
@Data
public class ValueCardRechargeOrRefundReq {

    @ApiModelProperty("客户id")
    private String customerId;

    @ApiModelProperty("储值卡id")
    private Long cardId;

    private Long storeId;

    private Long tenantId;

    @ApiModelProperty("办理类型 0退款 2充值")
    private Integer type;

    @ApiModelProperty("本金变动")
    private BigDecimal changePrincipal;

    @ApiModelProperty("赠金变动")
    private BigDecimal changePresent;

    @ApiModelProperty("备注")
    private String remark;

    @ApiModelProperty("销售人员ID")
    private String salesmanId;

    @ApiModelProperty("销售人员姓名")
    private String salesmanName;

    @ApiModelProperty("创建人id")
    private String createUserId;

    @ApiModelProperty("创建人姓名")
    private String createUserName;
}
