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

    @ApiModelProperty(value = "状态 0未开始或已购客户、1进行中或浏览未购买、9已下架, (未开始或进行中)-1")
    private Integer status = 0;

    @ApiModelProperty(value = "活动标题")
    private String activityTitle;

    private Long storeId;

    private Long tenantId;

    @ApiModelProperty(value = "手机号")
    private String phone;

    @ApiModelProperty(value = "秒杀活动id")
    private String seckillActivityId;
}
