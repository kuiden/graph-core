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
@ApiModel("营销活动列表请求")
public class StoreIdRequest implements Serializable {

    @ApiModelProperty("门店id")
    @NotNull(message = "门店id不能为空")
    private Long storeId;

}
