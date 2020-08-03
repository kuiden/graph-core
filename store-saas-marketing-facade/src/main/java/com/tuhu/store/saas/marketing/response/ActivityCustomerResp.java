package com.tuhu.store.saas.marketing.response;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.util.Date;

/**
 * 客户活动详情
 */
@Data
@ToString
public class ActivityCustomerResp implements Serializable {

    /**
     * 活动详情
     */
    private ActivityResp activity;

    /**
     * 客户信息详情
     */
//    private CustomerDetailResp customerDetail;

    /**
     * 主键id
     */
    private Long id;

    /**
     * 活动订单编码
     */
    private String activityOrderCode;

    /**
     * 活动编码
     */
    private String activityCode;

    /**
     * 客户ID
     */
    private String customerId;

    /**
     * 车主手机号
     */
    private String telephone;

    /**
     * 车主姓名
     */
    private String customerName;


    /**
     * 门店ID
     */
    private Long storeId;

    /**
     * 订单生成时间（活动报名时间）
     */
    private Date createTime;

    /**
     * 活动核销时间/取消时间
     */
    private Date useTime;

    /**
     * 使用状态0:   未核销1：已核销2：已取消3：已开单（控制开单按钮展示）
     */
    private Byte useStatus;

    /**
     * 通知消息状态,二进制位表示，成功为1，失败为0，第一位：报名成功，第二位：核销，第三位：取消订单
     */
    private String messageStatus;

    /**
     * 工单id
     */
    private String serviceOrderId;

    /**
     * 开单时间
     */
    private Date serviceOrderTime;

    /**
     * 活动开始时间
     */
    private Date startTime;

    /**
     * 活动结束时间
     */
    private Date endTime;
}
