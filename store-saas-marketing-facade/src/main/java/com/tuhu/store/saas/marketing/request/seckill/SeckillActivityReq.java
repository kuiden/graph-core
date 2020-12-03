package com.tuhu.store.saas.marketing.request.seckill;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * <p>
 * 秒杀活动分类表
 * </p>
 *
 * @author yangshengyong
 * @since 2020-12-02
 */
@Data
public class SeckillActivityReq implements Serializable {

    @ApiModelProperty(value = "页号")
    private Integer pageNum = 1;

    @ApiModelProperty(value = "每页条数")
    private Integer pageSize = 10;

    @ApiModelProperty(value = "状态 0未开始、1进行中、9已下架")
    private Integer status = 0;

    private Long storeId;

    private Long tenantId;

    @ApiModelProperty(value = "手机号")
    private String phone;
}
