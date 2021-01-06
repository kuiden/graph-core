package com.tuhu.store.saas.marketing.request.seckill;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @time 2020-12-04
 * @auther kudeng
 */
@Data
@ApiModel("秒杀活动模板排序")
public class SortSeckillTempReq implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "活动模板id",required = true)
    @NotBlank(message = "模板id不能为空")
    private String tempId;

    @ApiModelProperty(value = "序号",required = true)
    @NotNull(message = "序号不能为空")
    private Integer sort;

}
