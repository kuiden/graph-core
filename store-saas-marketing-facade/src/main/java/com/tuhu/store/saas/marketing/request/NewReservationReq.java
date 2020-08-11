package com.tuhu.store.saas.marketing.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @Author: yanglanqing
 * @Date: 2020/8/4 16:27
 */
@Data
public class NewReservationReq implements Serializable {
    private static final long serialVersionUID = -6270134858919379753L;

    /**
     * 预约单id,修改时使用
     */
    private String id;

    /**
     * 创建用户ID
     */
    private String userId;

    private Long tenantId;

    private Long storeId;

    /**
     * 客户(车主)ID
     */
    private String customerId;

    /**
     * 客户(车主)名称
     */
    private String customerName;

    /**
     * 客户(车主)手机号码
     */
    private String customerPhoneNumber;

    /**
     * 预计到店时间
     */
    private Date estimatedArriveTime;

    /**
     * 预约备注
     */
    private String description;

    /**
     * 验证码
     */
    private String verificationCode;

    /**
     * 预约创建终端(0:H5 1:b端 2:c端小程序)
     */
    private Integer teminal;

    /**
     * 预约渠道:MD(门店创建);ZXYY(小程序在线预约);COUPON(优惠券营销);ACTIVITY(活动营销)
     */
    private String sourceChannel;

    /**
     * 优惠券或活动id
     */
    private String marketingId;

    /**
     * 优惠券或活动名称
     */
    private String marketingName;

}

