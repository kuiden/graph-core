package com.tuhu.store.saas.marketing.request.valueCard;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;


/**
 * @author wangyuqing
 * @since 2020/10/19 11:16
 */
@Data
@ApiModel(description = "储值卡结算入参")
public class ValueCardRechargeOrRefundReq implements Serializable {

    @ApiModelProperty(value = "客户id", example = "1600482306772000011875", required = true, dataType = "String")
    private String customerId;
    @ApiModelProperty(value = "客户手机号")
    private String customerPhoneNumber;
    @ApiModelProperty(value = "storeId", hidden = true)
    private Long storeId;
    @ApiModelProperty(value = "tenantId", hidden = true)
    private Long tenantId;

    @ApiModelProperty(value = "办理类型 0退款 2充值", example = "0", required = true, dataType = "int32")
    @NotNull(message = "结算类型不能为空")
    private Integer type;

    @ApiModelProperty(value = "本金变动", example = "1000", required = true, dataType = "BigDecimal")
    @NotNull(message = "本金变动不能为空")
    private BigDecimal changePrincipal;

    @ApiModelProperty( value = "赠金变动", example = "50", required = true, dataType = "BigDecimal")
    @NotNull(message = "赠金变动不能为空")
    private BigDecimal changePresent;

    @ApiModelProperty("备注")
    private String remark;

    @ApiModelProperty(value = "销售人员ID",example = "张三",dataType = "String")
    private String salesmanId;

    @ApiModelProperty("销售人员姓名")
    private String salesmanName;

    @ApiModelProperty(value = "创建人id", hidden = true)
    private String createUserId;

    @ApiModelProperty(value = "创建人姓名", hidden = true)
    private String createUserName;
}
