package com.tuhu.store.saas.marketing.response.dto;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

/**
 * @Author: yanglanqing
 * @Date: 2020/8/5 11:01
 */
@Data
@ToString
public class ReservationDTO implements Serializable {
    private static final long serialVersionUID = 2906873346400796302L;

    private String id;

    //预约渠道:MD(门店创建);ZXYY(小程序在线预约);COUPON(优惠券营销);ACTIVITY(活动营销)
    private String sourceChannel;

    /*
    预约状态:
    B：待确认(UNCONFIRMED);已确认(CONFIRMED);已结束(OVER);已取消(CANCEL)
    C：已预约(ORDERED);已结束(OVER);已取消(CANCEL)
     */
    private String status;

    private String customerName;

    private String customerPhoneNumber;

    //备注
    private String description;

    //预约到店时间
    private Long reservationTime;

}
