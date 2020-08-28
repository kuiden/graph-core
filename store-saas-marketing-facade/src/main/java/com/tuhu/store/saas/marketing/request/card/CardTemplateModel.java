/*
 * Copyright 2018 tuhu.cn All right reserved. This software is the
 * confidential and proprietary information of tuhu.cn ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with Tuhu.cn
 */
package com.tuhu.store.saas.marketing.request.card;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * @author sunkuo
 * @date 2018/11/20  18:41
 */
@Setter
@Getter
@ToString
public class CardTemplateModel {


    @ApiModelProperty(value = "id", dataType = "String", required = false, example = "2345345")
    private Long id;

    @ApiModelProperty(value = "卡分类编码", dataType = "String", required = false, example = "卡分类编码")
    private String cardCategoryCode;
    @NotBlank(message = "卡名称不能为空")
    @ApiModelProperty(value = "卡名称", dataType = "String", required = false, example = "卡名称")
    private String cardName;

    @ApiModelProperty(value = "有效期  X个月", dataType = "Integer", required = false, example = "1")
    private Integer expiryPeriod;

    @ApiModelProperty(value = "是否是永久", dataType = "Boolean", required = false, example = "false")
    private Boolean forever;

    @ApiModelProperty(value = "模板状态", dataType = "String", required = false, example = "ENABLE")
    private String status;

    @Min(value = 0,message = "卡面值不能低于0元")
    @ApiModelProperty(value = "卡面值", dataType = "Long", required = false, example = "100")
    private BigDecimal faceAmount;

    @Min(value = 0,message = "卡实额不能低于0元")
    @ApiModelProperty(value = "卡实额", dataType = "Long", required = false, example = "90")
    private BigDecimal actualAmount;

    @ApiModelProperty(value = "卡优惠金额", dataType = "Long", required = false, example = "10")
    private BigDecimal discountAmount;

    @ApiModelProperty(value = "卡种类编码  COUNTING_CARD : 计次卡    TIMIN_CARD : 计时卡 MONTHLY_CARD : 月卡 ANNUAL_CARD : 年卡 ", dataType = "String", required = false, example = "COUNTING_CARD")
    private String cardTypeCode;

    @ApiModelProperty(value = "描述", dataType = "String", required = false, example = "描述")
    private String description;

    @ApiModelProperty(value = "租户ID", dataType = "Long", required = false, example = "1")
    private Long tenantId;

    @ApiModelProperty(value = "门店ID", dataType = "Long", required = false, example = "1")
    private Long storeId;

    @ApiModelProperty(value = "用户ID", dataType = "Long", required = false, example = "1")
    private String createUser;
    @ApiModelProperty(value = "创建时间", dataType = "Date", required = false, example = "1")
    private Date createTime;

    @ApiModelProperty(value = "用户名", dataType = "String", required = false, example = "用户名")
    private String userName;

    @ApiModelProperty(value = "更新时间", dataType = "Date", required = false, example = "111")
    private Date updateTime;

    @ApiModelProperty(value = "卡类型 1次卡", dataType = "Date", required = false, example = "1")
    private Byte type ;


    @ApiModelProperty(value = "过期时间  日期格式 开卡的时候用", dataType = "Date", required = false, example = "2020-08-06")
    private  Date expiryDate;
    @Valid
    List<CardTemplateItemModel> cardTemplateItemModelList;
}
