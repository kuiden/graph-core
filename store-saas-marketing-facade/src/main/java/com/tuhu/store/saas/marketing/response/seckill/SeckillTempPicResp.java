package com.tuhu.store.saas.marketing.response.seckill;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
@ApiModel(value = "秒杀活动模板图片")
public class SeckillTempPicResp implements Serializable {

    private static final long serialVersionUID = 1l;

    @ApiModelProperty("活动模板id")
    private String tempId;

    @ApiModelProperty("活动模板图片")
    private String picUrl;

    @ApiModelProperty("活动名称")
    private String activityTitle;
}
