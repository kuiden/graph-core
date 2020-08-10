package com.tuhu.store.saas.marketing.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @time 2020-08-07
 * @auther kudeng
 */
@Data
@ApiModel("C端活动详情入参")
public class CActivityDetailReq implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("活动id")
    @NotNull(message = "活动id不能为空")
    private Long activityId;

    private Long storeId;

}
