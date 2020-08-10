/*
 * Copyright 2019 tuhu.cn All right reserved. This software is the
 * confidential and proprietary information of tuhu.cn ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with Tuhu.cn
 */
package com.tuhu.store.saas.marketing.request.card;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * @author sunkuo
 * @date 2019/1/14  11:15
 */
@Setter
@Getter
public class MiniQueryCardReq {

    @ApiModelProperty(value = "客户ID ", dataType = "String", required = false, example = "客户ID")
    private String customerId;

    /**
     * 门店ID
     */
    private Long storeId;

    /**
     * 租户ID
     */
    private Long tenantId;

    /**
     * 第几页
     */
    private Integer pageNum = 0;

    /**
     * 每页大小
     */
    private Integer pageSize = 10;

    public Integer getPageNum() {
        return pageNum + 1;
    }

}
