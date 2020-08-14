package com.tuhu.store.saas.marketing.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
@ApiModel(value = "营销短信预览")
public class MarketingSmsReq implements Serializable {

    /**
     * 营销方式 0、优惠券营销 1、活动营销
     */
    @ApiModelProperty(value = "营销方式")
    @NotNull(message = "营销方式不能为空")
    private Byte marketingMethod;

    /**
     * 券Ids
     * marketingMethod为0为发送优惠券
     * marketingMethod为0为发送活动
     */
    @ApiModelProperty(value = "券或者活动Id")
    @NotNull(message = "券或者活动Id")
    private String couponOrActiveId;

    /**
     * 门店ID
     */
    private Long storeId;

    /**
     * 租户ID
     */
    private Long tenantId;

    private String originUrl;

}
