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
    private Long reservationDate;

    /**
     * 当天预约数
     */
    @ApiModelProperty(value = "当天预约数")
    private Integer count;

}
