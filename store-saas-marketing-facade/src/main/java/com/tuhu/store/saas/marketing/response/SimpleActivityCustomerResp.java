package com.tuhu.store.saas.marketing.response;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.util.Date;

/**
 * 简单客户活动订单信息
 */
@Data
@ToString
public class SimpleActivityCustomerResp implements Serializable {
    private static final long serialVersionUID = -3637531639971332131L;

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
     * 门店ID
     */
    private Long storeId;

    /**
     * 订单生成时间（活动报名时间）
     */
    private Date createTime;

    /**
     * 使用状态0:   未核销1：已核销2：已取消3：已开单（控制开单按钮展示）
     */
    private Byte useStatus;

    /**
     * 车主姓名
     */
    private String name;

    /**
     * 客户手机号
     */
    private String phoneNumber;

    /**
     * 车主姓名
     */
    private String customerName;
}
