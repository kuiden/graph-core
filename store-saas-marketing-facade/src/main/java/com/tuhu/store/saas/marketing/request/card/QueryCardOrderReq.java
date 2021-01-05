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

/**
 * @author sunkuo
 * @date 2018/11/29  15:36
 */
@Setter
@Getter
@ToString
public class QueryCardOrderReq {

    @ApiModelProperty(value = "开卡单ID", dataType = "Long", required = false, example = "卡名称")
    private Long cardOrderId;

    @ApiModelProperty(value = "门店ID", dataType = "Long", required = false, example = "17")
    private Long storeId;

    @ApiModelProperty(value = "租户ID", dataType = "Long", required = false, example = "19")
    private Long tenantId;

    /**
     * 秒杀活动订单id
     */
    private String seckillRegisterRecodeId;

}
