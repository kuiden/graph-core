package com.tuhu.store.saas.marketing.response;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.util.Date;

/**
 * @Author: yanglanqing
 * @Date: 2020/8/3 16:31
 */
@Data
@ToString
public class ReservationDateResp implements Serializable {
    private static final long serialVersionUID = -3743906468570794517L;

    /**
     日期
     */
    @ApiModelProperty(value = "日期")
    private Date reserveDate;

    /**
     日期
     */
    @ApiModelProperty(value = "日期String")
    private String reserveDateString;

    /**
     * 星期数
     */
    @ApiModelProperty(value = "星期数")
    private int dayOfWeek;

    /**
     * 星期几
     */
    @ApiModelProperty(value = "星期几")
    private String dayOfWeekName;

}
