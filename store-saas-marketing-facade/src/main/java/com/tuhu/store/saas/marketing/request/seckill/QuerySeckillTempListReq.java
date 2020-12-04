package com.tuhu.store.saas.marketing.request.seckill;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @time 2020-12-04
 * @auther kudeng
 */
@Data
@ApiModel(value = "查询秒杀活动模板列表")
public class QuerySeckillTempListReq implements Serializable {

    private static final long serialVersionUID = 1l;

    @ApiModelProperty("活动名称")
    private String activityTitle;

    @ApiModelProperty("模板状态，0：禁用，1：启用")
    private Integer status;

}
