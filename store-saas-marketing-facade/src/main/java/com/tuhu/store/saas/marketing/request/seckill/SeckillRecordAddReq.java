package com.tuhu.store.saas.marketing.request.seckill;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @author wangyuqing
 * @since 2020/12/3 15:55
 */
@Data
public class SeckillRecordAddReq implements Serializable {

    @ApiModelProperty("活动id")
    private String seckillActivityId;

    @ApiModelProperty("活动名称")
    private String seckillActivityName;

    @ApiModelProperty("报名客户id")
    private String customerId;

    @ApiModelProperty("报名客户名称")
    private String customerName;

    @ApiModelProperty("是否新客户 0:否 1:是")
    private Integer isNewCustomer;

    @ApiModelProperty("购买人手机号")
    private String buyerPhoneNumber;

    @ApiModelProperty("使用人手机号码")
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

    private Long storeId;

    private Long tenantId;

}
