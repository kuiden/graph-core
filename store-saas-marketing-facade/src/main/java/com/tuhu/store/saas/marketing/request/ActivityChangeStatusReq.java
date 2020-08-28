package com.tuhu.store.saas.marketing.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 营销活动上下架
 */
@Data
@ToString
@ApiModel("营销活动上下架对象")
public class ActivityChangeStatusReq implements Serializable {
    private static final long serialVersionUID = 7997823276593668203L;

    @ApiModelProperty("门店ID")
    private Long storeId;

    @ApiModelProperty("操作人ID")
    private String userId;

    @ApiModelProperty("营销活动ID")
    @NotNull(message = "营销活动ID不能为空")
    private Long activityId;

    @ApiModelProperty("活动状态，0：下架，1：上架")
    @NotNull(message = "上下架状态不能为空")
    private Boolean status;
}
