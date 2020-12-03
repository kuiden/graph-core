package com.tuhu.store.saas.marketing.response.seckill;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 秒杀活动分类表
 * </p>
 *
 * @author yangshengyong
 * @since 2020-12-02
 */
@Data
@ApiModel(value = "SeckillActivityResp", description = "秒杀活动记录")
public class SeckillActivityResp implements Serializable {
    @ApiModelProperty(value = "活动id")
    private String id;


}
