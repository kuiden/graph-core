package com.tuhu.store.saas.marketing.request.seckill;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @author wangyuqing
 * @since 2020/12/3 15:55
 */
@Data
@ApiModel("秒杀活动抢购对象")
public class SeckillRecordAddReq implements Serializable {

    @ApiModelProperty("活动id")
    @NotNull(message = "活动id不能为空")
    private String seckillActivityId;

    @ApiModelProperty("活动名称")
    @NotNull(message = "活动名称不能为空")
    private String seckillActivityName;

    @ApiModelProperty("报名客户id")
    private String customerId;

    @ApiModelProperty("报名客户名称")
    private String customerName;

    @ApiModelProperty("是否新客户 0:否 1:是")
    private Integer isNewCustomer;

    @ApiModelProperty("购买人手机号")
    @NotNull(message = "购买人手机号不能为空")
    private String buyerPhoneNumber;

    @ApiModelProperty("使用人手机号码")
    @NotNull(message = "使用人手机号不能为空")
    private String userPhoneNumber;

    @ApiModelProperty("车牌号")
    private String vehicleNumber;

    @ApiModelProperty("单价")
    private BigDecimal unitPrice;

    @ApiModelProperty("购买数量")
    private Long quantity;

    @ApiModelProperty("应付金额")
    private BigDecimal expectAmount;

    @ApiModelProperty("支付方式")
    private String paymentModeCode;

    @ApiModelProperty("微信openId")
    private String openId;

    @ApiModelProperty("门店id")
    private Long storeId;

    @ApiModelProperty("企业id")
    private Long tenantId;

}
