package com.tuhu.store.saas.marketing.request.seckill;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author wangyuqing
 * @since 2020/12/4 10:41
 */
@Data
public class SeckillRemindAddReq implements Serializable {

    @ApiModelProperty("客户端类型")
    private String clientType;

    @ApiModelProperty("用户小程序code")
    private String openIdCode;

    @ApiModelProperty("小程序openId")
    private String openId;

    @ApiModelProperty("模板ID")
    private String templateId;

    @ApiModelProperty("小程序页面url")
    private String page;

    @ApiModelProperty("活动id")
    private String seckillActivityId;

    private Long storeId;

    private Long tenantId;

}
