package com.tuhu.store.saas.marketing.response.seckill;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.*;
import java.io.Serializable;

/**
 * @time 2020-12-04
 * @auther kudeng
 */
@Data
@ApiModel(value = "秒杀活动模板明细")
public class SeckillTempItemResp implements Serializable {

    private static final long serialVersionUID = 1l;

    @ApiModelProperty("秒杀活动明细id")
    private String id;

    @ApiModelProperty(value = "商品服务名称", required = true)
    private String goodsName;

    @ApiModelProperty(value = "项目类型，0-商品，1-服务", required = true)
    private Integer goodsType;

    @ApiModelProperty(value = "商品服务数量", required = true)
    private Integer itemQuantity;

}
