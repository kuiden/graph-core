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
import lombok.ToString;

import java.util.Date;
import java.util.List;

/**
 * @author sunkuo
 * @date 2018/11/27  19:06
 */
@Setter
@Getter
@ToString
public class CardResp {

    /**
     * 卡Id
     */
    private String cardId;

    /**
     * 卡状态
     */
    private String cardStatus;
    /**
     * 卡状态
     */
    private String cardStatusName;

    /**
     * 卡模板ID
     */
    private String cardTemplateId;

    /**
     * 卡分类编码
     */
    private String cardCategoryCode;
    /**
     * 卡号码
     */
    private String cardNo;
    /**
     * 卡名称
     */
    private String cardName;

    /**
     * 卡种类编码(如:计次卡,计时卡,月卡,年卡)
     */
    private String cardTypeCode;

    /**
     * 有效期
     */
    private Boolean forever;

    /**
     * 有效期
     */
    private Date expiryDate;
    /**
     * 金额
     */
    private Long faceAmount;
    /**
     * 优惠金额
     */
    private Long discountAmount;

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
    private String customerGender;

    /**
     * 客户电话
     */
    private String customerPhoneNumber;

    /**
     * 实付金额
     */
    private Long actualAmount;
    /**
     * 描述
     */
    private String description;

    /**
     * 是否有效(有效为true)
     */
    private Boolean validity;

    /**
     * 卡服务
     */
    private List<CardItemResp> cardItemRespList;
}
