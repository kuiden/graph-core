package com.tuhu.store.saas.marketing.response;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class CustomerCouponResponse {
    //优惠券id
    @ApiModelProperty(value = "优惠券id")
    private Long id;
    //领取时间
    @ApiModelProperty(value = "领取时间")
    private Date createTime;

    @ApiModelProperty(value = "客户id")
    private String customerId;

    @ApiModelProperty(value = "客户名称")
    private String customerName;

    @ApiModelProperty(value = "券号")
    private String customerCouponCode;
    //优惠券状态
    //0:未使用;1:已使用;-1:已过期
    @ApiModelProperty(value = "优惠券状态 0:未使用;1:已使用;-1:已过期")
    private Integer status;


    @ApiModelProperty(value = "优惠券名称")
    private String title;

    @ApiModelProperty(value = "优惠券类型：0：满减 ，代金券 1：满折，折扣券")
    private Byte type;

    @ApiModelProperty(value = "领取类型：0：主动在线领取 1：手动发券 2：营销发券")
    private Byte receiveType;

    @ApiModelProperty(value = "营销发券-发券操作人")
    private String sendUser;

    @ApiModelProperty(value = "有效期类型: 0：指定有效期时间 1：结束时间-相对时间")
    private Byte validityType;

    @ApiModelProperty(value = "优惠券使用条件金额")
    private BigDecimal conditionLimit;

    @ApiModelProperty(value = "代金券优惠金额")
    private BigDecimal contentValue;

    @ApiModelProperty(value = "折扣券折扣数")
    private BigDecimal discountValue;

    @ApiModelProperty(value = "使用开始时间")
    private Date useStartTime;

    @ApiModelProperty(value = "使用结束时间")
    private Date useEndTime;

    @ApiModelProperty(value = " validity_type=1时,相对领取时间天数")
    private Integer relativeDaysNum;

    @ApiModelProperty(value = " 券说明")
    private String remark;

    @ApiModelProperty(value = " couponCode")
    private String couponCode;
}
