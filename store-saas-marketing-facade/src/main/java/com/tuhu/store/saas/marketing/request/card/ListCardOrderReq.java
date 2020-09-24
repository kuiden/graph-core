/*
 * Copyright 2018 tuhu.cn All right reserved. This software is the
 * confidential and proprietary information of tuhu.cn ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with Tuhu.cn
 */
package com.tuhu.store.saas.marketing.request.card;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Min;

/**
 * @author sunkuo
 * @date 2018/11/29  19:21
 */
@Data
public class ListCardOrderReq {

    /*
     * 客户姓名/手机号
     */
    @ApiModelProperty(value = "查询条件", dataType = "Long", required = false, example = "客户姓名/手机号")
    private String condition;

    @Min(1)
    @ApiModelProperty(value = "pageSize", dataType = "Integer", required = false, example = "10")
    private Integer pageSize = 10;

    @Min(0)
    @ApiModelProperty(value = "pageNum", dataType = "Integer", required = false, example = "0")
    private Integer pageNum = 1;

    @ApiModelProperty(value = "门店ID", dataType = "Long", required = false, example = "17")
    private Long storeId;

    @ApiModelProperty(value = "租户ID", dataType = "Long", required = false, example = "19")
    private Long tenantId;

    @ApiModelProperty(value = "开卡单收款状态")
    //930,客户详情进入需要查询所有状态的次卡订单信息,加入 ALL状态
    private String paymentStatus;

}
