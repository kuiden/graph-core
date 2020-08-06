package com.tuhu.store.saas.marketing.response;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.util.Date;

/**
 * @Author: yanglanqing
 * @Date: 2020/8/3 16:28
 */
@Data
@ToString
public class ReservationPeriodResp implements Serializable {
    private static final long serialVersionUID = 2105446757437476772L;

    /**
     预约到店开始时间
     */
    @ApiModelProperty(value = "预约到店开始时间")
    private Long reserveStartTime;

    /**
     预约到店时间
     */
    @ApiModelProperty(value = "预约到店时间")
    private String reserveStartTimeString;

    /**
     预约到店结束时间
     */
    @ApiModelProperty(value = "预约到店结束时间")
    private Long reserveEndTime;

    /**
     * 时间段名称： 开始时间-结束时间
     */
    @ApiModelProperty(value = "时间段名称：开始时间-结束时间")
    private String periodName;

    /**
     * 是否已经预约过当前时间段
     */
    @ApiModelProperty(value = "是否已经预约过当前时间段")
    private boolean reserved;
}
