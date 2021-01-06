/*
 * Copyright 2018 tuhu.cn All right reserved. This software is the
 * confidential and proprietary information of tuhu.cn ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with Tuhu.cn
 */
package com.tuhu.store.saas.marketing.request.card;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * @author sunkuo
 * @date 2018/11/28  11:03
 */
@Setter
@Getter
@ToString
public class AddCardOrderReq {

    @ApiModelProperty(value = "客户ID", dataType = "Long", required = false, example = "1")
    @NotNull(message = "客户Id不能为空")
    private String customerId;

    @ApiModelProperty(value = "客户名称", dataType = "Long", required = false, example = "客户名称")
    private String customerName;

    @ApiModelProperty(value = "客户性别", dataType = "Boolean", required = false, example = "1")
    private Boolean customerGender;

    @ApiModelProperty(value = "客户电话", dataType = "Long", required = false, example = "12342134")
    @NotNull(message = "客户电话不能为空")
    private String customerPhoneNumber;

    @ApiModelProperty(value = "销售人员ID", dataType = "Long", required = false, example = "12323")
    private String salesmanId;

    @ApiModelProperty(value = "销售人员姓名", dataType = "Long", required = false, example = "销售人员姓名")
    private String salesmanName;

    @ApiModelProperty(value = "卡模板ID", dataType = "Long", required = false, example = "1")
    @NotNull(message = "卡模板ID不能为空")
    private Long cardTemplateId;

    @ApiModelProperty(value = "有效日期", dataType = "Date", required = false, example = "234543")
    private Date expiryDate;

    @ApiModelProperty(value = "是否永久有效", dataType = "Boolean", required = false, example = "false")
    @NotNull(message = "是否永久有效不能为空")
    private Boolean forever;

    @ApiModelProperty(value = "门店ID", dataType = "Long", required = false, example = "17")
    private Long storeId;

    @ApiModelProperty(value = "门店编码")
    private String storeNo;

    @ApiModelProperty(value = "租户ID", dataType = "Long", required = false, example = "19")
    private Long tenantId;

    @ApiModelProperty(value = "创建人", dataType = "Long", required = false, example = "123")
    private String createUser;

    @ApiModelProperty(value = "创建时间", dataType = "Long", required = false, example = "2018-11-22T12:36:54.025Z")
    private Date createTime;

    @ApiModelProperty(value = "更新时间", dataType = "Long", required = false, example = "2018-11-22T12:36:54.025Z")
    private Date updateTime;

    private String seckillRegisterRecodeId;

    /**
     * 购买数量
     */
    private Long quantity;

}
