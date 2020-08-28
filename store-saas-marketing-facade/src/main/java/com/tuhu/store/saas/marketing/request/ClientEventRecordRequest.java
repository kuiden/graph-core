package com.tuhu.store.saas.marketing.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("门店C端事件记录请求")
public class ClientEventRecordRequest extends EndUserVistiedCouponRequest {

    @ApiModelProperty("事件类型，visit：访问，wechatForward:转发，registered：注册，login：登录，other：其他")
    private String eventType = "visit";

    @ApiModelProperty("主题类型")
    private String contentType;

    @ApiModelProperty("主题内容")
    private String contentValue;

    @ApiModelProperty("客户ID")
    private String customerId;
}
