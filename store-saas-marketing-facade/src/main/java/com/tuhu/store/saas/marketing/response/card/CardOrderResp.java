/*
 * Copyright 2018 tuhu.cn All right reserved. This software is the
 * confidential and proprietary information of tuhu.cn ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with Tuhu.cn
 */
package com.tuhu.store.saas.marketing.response.card;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author sunkuo
 * @date 2018/11/29  19:29
 */
@Setter
@Getter
public class CardOrderResp {

    /**
     * 开卡单ID
     */
    private Long id;

    /**
     * 开卡单号
     */
    private String orderNo;

    /**
     * 卡ID
     */
    private Long cardId;
    /**
     * 卡号
     */
    private String cardNo;

    /**
     * 卡名称
     */
    private String cardName;

    /**
     * 卡状态code
     */
    private String cardStatusCode;

    /**
     * 卡状态
     */
    private String cardStatus;
    /**
     * 客户ID
     */
    private String customerId;
    /**
     * 客户名称
     */
    private String customerName;
    /**
     * 客户性别
     */
    private Boolean customerGender;
    /**
     * 客户电话
     */
    private String customerPhoneNumber;

    /**
     * 卡模板ID
     */
    private Long cardTemplateId;
    /**
     * 车辆ID
     */
    private String vehicleId;

    /**
     * 车牌号
     */
    private String licensePlateNo;

    /**
     * 销售人员ID
     */
    private String salesmanId;
    /**
     * 销售人员姓名
     */
    private String salesmanName;


    /**
     * 收款状态code
     */
    private String paymentStatusCode;

    /**
     * 收款状态
     */
    private String paymentStatus;

    /**
     * 是否永久有效
     */
    private Boolean forever;

    /**
     * 有效期
     */
    private Date expiryDate;
    /**
     * 卡面值
     */
    private BigDecimal amount;
    /**
     * 优惠金额
     */
    private BigDecimal discountAmount;
    /**
     * 实付金额
     */
    private BigDecimal actualAmount;

    /**
     * 创建 时间
     */
    private Date createTime;
    /**
     * 更新 时间
     */
    private Date updateTime;

    /**
     * 描述
     */
    private String description;

    /**
     * 剩余次数
     */
    private Long remainQuantity;

    /**
     * 开卡单状态
     */
    private String status;
}
