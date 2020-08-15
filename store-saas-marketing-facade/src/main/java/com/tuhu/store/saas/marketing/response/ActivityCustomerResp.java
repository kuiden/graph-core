package com.tuhu.store.saas.marketing.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.util.Date;

/**
 * 客户活动详情
 */
@Data
@ToString
@ApiModel(value = "客户活动详情出参")
public class ActivityCustomerResp implements Serializable {

    @ApiModelProperty("活动详情")
    private ActivityResp activity;

    /**
     * 客户信息详情
     */
//    private CustomerDetailResp customerDetail;

    @ApiModelProperty("客户活动id")
    private Long id;

    @ApiModelProperty("活动订单编码")
    private String activityOrderCode;

    @ApiModelProperty("活动编码")
    private String activityCode;

    @ApiModelProperty("客户ID")
    private String customerId;

    @ApiModelProperty("车主手机号")
    private String telephone;

    @ApiModelProperty("车主姓名")
    private String customerName;

    @ApiModelProperty("门店ID")
    private Long storeId;

    @ApiModelProperty("订单生成时间（活动报名时间）")
    private Date createTime;

    @ApiModelProperty("活动核销时间/取消时间")
    private Date useTime;

    /**
     * 使用状态0:   未核销1：已核销2：已取消3：已开单（控制开单按钮展示）
     */
    @ApiModelProperty("使用状态")
    private Byte useStatus;

    /**
     * 通知消息状态,二进制位表示，成功为1，失败为0，第一位：报名成功，第二位：核销，第三位：取消订单
     */
    @ApiModelProperty("通知消息状态")
    private String messageStatus;


    @ApiModelProperty("工单id")
    private String serviceOrderId;

    @ApiModelProperty("开单时间")
    private Date serviceOrderTime;

    @ApiModelProperty("活动开始时间")
    private Date startTime;

    @ApiModelProperty("活动结束时间")
    private Date endTime;

    @ApiModelProperty("订单二维码字节流")
    private byte[] qrCode;
}
