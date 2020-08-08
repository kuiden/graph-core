package com.tuhu.store.saas.marketing.dataobject;

import java.io.Serializable;
import java.util.Date;

/**
 * customer_coupon
 * @author 
 */
public class CustomerCoupon implements Serializable {
    public CustomerCoupon() {
    }

    public CustomerCoupon(String couponCode) {
        this.couponCode = couponCode;
    }
    /**
     * 主键ID
     */
    private Long id;

    /**
     * 优惠券编码
     */
    private String couponCode;

    /**
     * 客户ID
     */
    private String customerId;

    /**
     * 领取类型：0：主动在线领取 1：手动发券 2：营销发券
     */
    private Byte receiveType;

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
    private Byte useStatus;

    /**
     * 客户优惠券编码
     */
    private String code;

    private static final long serialVersionUID = 1L;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCouponCode() {
        return couponCode;
    }

    public void setCouponCode(String couponCode) {
        this.couponCode = couponCode;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public Byte getReceiveType() {
        return receiveType;
    }

    public void setReceiveType(Byte receiveType) {
        this.receiveType = receiveType;
    }

    public String getSendUser() {
        return sendUser;
    }

    public void setSendUser(String sendUser) {
        this.sendUser = sendUser;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUseStartTime() {
        return useStartTime;
    }

    public void setUseStartTime(Date useStartTime) {
        this.useStartTime = useStartTime;
    }

    public Date getUseEndTime() {
        return useEndTime;
    }

    public void setUseEndTime(Date useEndTime) {
        this.useEndTime = useEndTime;
    }

    public Date getUseTime() {
        return useTime;
    }

    public void setUseTime(Date useTime) {
        this.useTime = useTime;
    }

    public Byte getUseStatus() {
        return useStatus;
    }

    public void setUseStatus(Byte useStatus) {
        this.useStatus = useStatus;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}