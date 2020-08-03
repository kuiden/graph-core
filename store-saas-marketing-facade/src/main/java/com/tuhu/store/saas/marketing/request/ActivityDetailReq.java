package com.tuhu.store.saas.marketing.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @time 2020-08-03
 * @auther kudeng
 */
@Data
@ApiModel(value = "获取营销活动详情对象")
public class ActivityDetailReq implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("活动id")
    @NotNull(message = "活动id不能为空")
    private Long activityId;

}
