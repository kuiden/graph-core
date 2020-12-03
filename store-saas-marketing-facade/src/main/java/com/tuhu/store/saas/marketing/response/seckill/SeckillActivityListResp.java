package com.tuhu.store.saas.marketing.response.seckill;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author wangyuqing
 * @since 2020/12/3 16:32
 */
@Data
public class SeckillActivityListResp implements Serializable {
    @ApiModelProperty(value = "活动id")
    private String id;
    @ApiModelProperty(value = "状态")
    private Integer status;
    @ApiModelProperty(value = "状态名称")
    private String statusName;
    @ApiModelProperty(value = "售出个数")
    private Integer salesNumber = 0;
    @ApiModelProperty(value = "总个数")
    private Integer totalNumber;
    @ApiModelProperty(value = "活动标题")
    private String activityTitle;
}
