package com.tuhu.store.saas.marketing.po;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * activity_customer
 * @author 
 */
@Data
public class ActivityCustomer implements Serializable {
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
     * 门店ID
     */
    private Long storeId;

    /**
     * 企业ID
     */
    private Long tenantId;

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

    /**
     * 车主姓名
     */
    private String customerName;

    /**
     * 微信openId
     */
    private String openId;

    private static final long serialVersionUID = 1L;
}