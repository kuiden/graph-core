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

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @author sunkuo
 * @date 2018/11/20  18:45
 */
@Setter
@Getter
@ToString
public class CardTemplateItemModel {
    @ApiModelProperty(value = "id", dataType = "int", required = false, example = "1")
    private  Long id ;
    @NotNull
    @ApiModelProperty(value = "商品ID", dataType = "String", required = true, example = "123423")
    private String goodsId;

    @ApiModelProperty(value = "产品PID", dataType = "String", required = false, example = "123423")
    private String pid;

    @ApiModelProperty(value = "服务项名称", dataType = "String", required = false, example = "服务项名称")
    private String serviceItemName;

    @ApiModelProperty(value = "服务项编码", dataType = "String", required = false, example = "sdgsdfg")
    private String serviceItemCode;

    @ApiModelProperty(value = "业务分类", dataType = "String", required = false, example = "sdgsdfg")
    private String businessCategory;

    @ApiModelProperty(value = "业务分类编码", dataType = "String", required = false, example = "sdgsdfg")
    private String businessCategoryCode;

    @ApiModelProperty(value = "业务分类名称", dataType = "String", required = false, example = "sdgsdfg")
    private String businessCategoryName;

    @ApiModelProperty(value = "工时", dataType = "String", required = false, example = "12")
    private Integer laborHour;

    @Min(value = 0,message = "次数不能低于0次")
    @ApiModelProperty(value = "次数", dataType = "String", required = false, example = "12")
    private Integer measuredQuantity;

    @ApiModelProperty(value = "原单价", dataType = "String", required = false, example = "12")
    private BigDecimal price;

    @ApiModelProperty(value = "现单价", dataType = "String", required = false, example = "12")
    private BigDecimal faceAmount;

    @ApiModelProperty(value = "优惠金额", dataType = "String", required = false, example = "12")
    private BigDecimal discountAmount;

    @Min(value = 1,message = "项目实额必须大于0元")
    @ApiModelProperty(value = "卡实额", dataType = "String", required = false, example = "12")
    private BigDecimal actualAmount;

    @ApiModelProperty(value = "描述", dataType = "String", required = false, example = "描述")
    private String description;

    @ApiModelProperty(value = "门店ID", dataType = "String", required = false, example = "19")
    private Long storeId;

    @ApiModelProperty(value = "租户ID", dataType = "String", required = false, example = "17")
    private Long tenantId;

    @ApiModelProperty(value = "创建人", dataType = "String", required = false, example = "321")
    private String createUser;

    @ApiModelProperty(value = "更新人", dataType = "String", required = false, example = "321")
    private String updateUser;

    @ApiModelProperty(value = "创建时间", dataType = "String", required = false, example = "2018-11-22T12:36:54.025Z")
    private Date createTime;

    @ApiModelProperty(value = "修改时间", dataType = "String", required = false, example = "2018-11-22T12:36:54.025Z")
    private Date updateTime;
    @NotNull
    @ApiModelProperty(value = "商品类型：1 服务  2商品", dataType = "bype", required = true, example = "1")
    private Byte type;

}
