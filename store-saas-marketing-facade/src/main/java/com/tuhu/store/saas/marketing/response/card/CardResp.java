/*
 * Copyright 2018 tuhu.cn All right reserved. This software is the
 * confidential and proprietary information of tuhu.cn ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with Tuhu.cn
 */
package com.tuhu.store.saas.marketing.response.card;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * @author sunkuo
 * @date 2018/11/27  19:06
 */
@Data
public class CardResp {

    /**
     * 卡Id
     */
    private Long id;

    /**
     * 卡状态
     */
    private String cardStatus;
    /**
     * 卡状态Code
     */
    private String cardStatusCode;

    /**
     * 卡模板ID
     */
    private Long cardTemplateId;

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
    private String expiryDate;

    /**
     * b端 - 有效期显示字符串
     */
    private String date;

    /**
     * 金额
     */
    private BigDecimal faceAmount;
    /**
     * 优惠金额
     */
    private BigDecimal discountAmount;

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
    private BigDecimal actualAmount;
    /**
     * 描述
     */
    private String description;

    /*
     * 次卡服务列表
     */
    private List<CardItemResp> cardServiceItem;

    /*
     * 次卡商品列表
     */
    private List<CardItemResp> cardGoodsItem;

}
