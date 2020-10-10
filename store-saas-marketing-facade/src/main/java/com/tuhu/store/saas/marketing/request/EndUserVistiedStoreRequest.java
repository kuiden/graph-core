package com.tuhu.store.saas.marketing.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.NotEmpty;

@Data
@ApiModel("车主端用户访问店铺记录Request")
public class EndUserVistiedStoreRequest {

//    @NotEmpty
    private String openIdCode;

//    @NotEmpty
    private String storeId;

    @NotEmpty
    private String clientType;

    @ApiModelProperty("客户端类型，0-C端小程序，1-C端H5， 默认0")
    private Integer sourceType = 0;
}
