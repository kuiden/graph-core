package com.tuhu.store.saas.marketing.response.seckill;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

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
    @ApiModelProperty(value = "活动头图")
    private String headImage;
    @ApiModelProperty(value = "活动标题")
    private String activityTitle;
    @ApiModelProperty(value = "原价")
    private BigDecimal originalPrice;
    @ApiModelProperty(value = "现价")
    private BigDecimal newPrice;
    @ApiModelProperty(value = "活动开始时间")
    private Date startTime;
    @ApiModelProperty(value = "活动结束时间")
    private Date endTime;
}
