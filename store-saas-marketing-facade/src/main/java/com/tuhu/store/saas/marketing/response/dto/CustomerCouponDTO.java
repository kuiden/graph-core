package com.tuhu.store.saas.marketing.response.dto;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 用户优惠券信息
 */
@Data
public class CustomerCouponDTO implements Serializable {

    private static final long serialVersionUID = 2544410109780545842L;

    public enum UnusableType {
        NotStarted(0, "还未到使用时间"),
        Expired(1, "已过期"),
        NotApplicable(2, "不满足使用范围"),
        ThresholdLimited(3, "未达到使用门槛");

        UnusableType(int code, String msg) {
            this.code = code;
            this.msg = msg;
        }

        private int code;
        private String msg;

        public int getCode() {
            return code;
        }

        public void setCode(int code) {
            this.code = code;
        }

        public String getMsg() {
            return msg;
        }

        public void setMsg(String msg) {
            this.msg = msg;
        }
    }

    /**
     * 优惠券信息
     */
    private CouponDTO coupon;

    /**
     * 客户优惠券ID
     */
    private Long id;

    /**
     * 客户ID
     */
    private String customerId;

    /**
     * 领取类型：0：主动在线领取 1：手动发券 2：营销发券
     */
    private Integer receiveType;

    /**
     * 营销发券-发券操作人
     */
    private String sendUser;

    /**
     * 领取时间
     */
    private Date createTime;

    /**
     * 使用开始时间
     */
    private Date useStartTime;

    /**
     * 使用结束时间
     */
    private Date useEndTime;

    /**
     * 优惠券使用时间
     */
    private Date useTime;

    /**
     * 使用状态 0:未使用 1：已使用
     */
    private Integer useStatus;

    /**
     * 当前优惠券可以适用的服务项目或者商品
     */
    private List<ServiceOrderItemDTO> items;

    /**
     * 优惠券不可用类型
     */
    private UnusableType unusableType;

    /**
     * 优惠券优惠的金额
     */
    private BigDecimal couponDiscountAmount;

}
