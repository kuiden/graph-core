package com.tuhu.store.saas.marketing.request.seckill;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
@ApiModel("秒杀获取二维码图片入参")
public class SeckillActivityQrCodeReq implements Serializable {

    private static final long serialVersionUID = 12344564321L;

    @ApiModelProperty("秒杀活动id")
    @NotNull(message = "秒杀活动id不能为空")
    private String seckillActivityId;
    /**
     * 参数"activityId=222"
     */
    private String scene;
    @ApiModelProperty("页面路径")
    private String path;

    @ApiModelProperty("二维码宽度")
    private Long width;
}