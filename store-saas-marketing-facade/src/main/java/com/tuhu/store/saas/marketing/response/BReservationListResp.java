package com.tuhu.store.saas.marketing.response;

import com.tuhu.store.saas.marketing.response.dto.ReservationDTO;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.util.List;

/**
 * @Author: yanglanqing
 * @Date: 2020/8/5 10:46
 */
@Data
@ToString
public class BReservationListResp implements Serializable {
    private static final long serialVersionUID = 4065035042404493375L;

    //时间段
    private String periodName;

    //预约时间-开始
    private Long reservationStartTime;

    //预约时间-结束
    private Long reservationEndTime;

    private List<ReservationDTO> reservationDTOs;

}
