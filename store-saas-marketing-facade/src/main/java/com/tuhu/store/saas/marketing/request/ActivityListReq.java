package com.tuhu.store.saas.marketing.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 营销活动列表查询请求
 */
@Data
@ToString
@ApiModel("营销活动列表查询请求")
public class ActivityListReq implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty("活动名称，模糊查找")
    private String title;

    @ApiModelProperty("活动状态,全部：null，未开始:0,进行中:1,已结束:2,未结束:3")
    private Integer dateStatus;

    @ApiModelProperty("活动上下架，全部：null，0：下架，1：上架")
    private Boolean status;

    @ApiModelProperty("用户ID")
    private String userId;

    @ApiModelProperty("门店ID")
    private Long storeId;

    @ApiModelProperty("租户ID")
    private Long tenantId;

    @NotNull(message = "pageNum不能为空")
    @Min(0)
    private Integer pageNum = 0;

    @NotNull(message = "pageSize不能为空")
    @Min(1)
    private Integer pageSize = 10;
}
