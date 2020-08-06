package com.tuhu.store.saas.marketing.po;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @Author: yanglanqing
 * @Date: 2020/8/6 19:15
 */
@Data
public class ReservationDateDTO implements Serializable {
    private static final long serialVersionUID = -788118998045789170L;

    /**
     日期
     */
    private Date reservationDate;

    /**
     * 当天预约数
     */
    private Integer count;
}
