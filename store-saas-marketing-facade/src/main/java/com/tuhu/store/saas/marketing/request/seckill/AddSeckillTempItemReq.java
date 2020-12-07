package com.tuhu.store.saas.marketing.request.seckill;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.*;
import java.io.Serializable;
import java.util.Date;

/**
 * @time 2020-12-04
 * @auther kudeng
 */
@Data
@ApiModel(value = "添加秒杀活动模板明细")
public class AddSeckillTempItemReq implements Serializable {

    private static final long serialVersionUID = 1l;

    @ApiModelProperty(value = "商品服务名称", required = true)
    @NotBlank(message = "商品服务名称不能为空")
    @Size(max = 20, message = "商品服务名称最多可输入20个汉字")
    private String goodsName;

    @ApiModelProperty(value = "项目类型，0-商品，1-服务", required = true)
    @NotNull(message = "项目类型不能为空")
    private Integer goodsType;

    @ApiModelProperty(value = "商品服务数量", required = true)
    @NotNull(message = "商品服务数量为空")
    @Min(value = 1, message = "商品数量大于0")
    @Max(value = 999999999, message = "商品数量不超过999999999")
    private Integer itemQuantity;

}
