package com.tuhu.store.saas.marketing.dataobject;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 车主端用户访问的优惠券记录
 * </p>
 *
 * @author someone
 * @since 2020-08-03
 */
@TableName("end_user_visited_coupon")
public class EndUserVisitedCouponDAO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    private String id;
    /**
     * 优惠券编码
     */
    @TableField("coupon_code")
    private String couponCode;
    /**
     * 车主端小程序微信openId
     */
    @TableField("open_id")
    private String openId;
    /**
     * 门店ID
     */
    @TableField("store_id")
    private String storeId;
    /**
     * 创建时间
     */
    @TableField("create_time")
    private Date createTime;
    /**
     * 门店客户ID
     */
    @TableField("customer_id")
    private String customerId;
    /**
     * 门店客户注册时间
     */
    @TableField("registered_time")
    private Date registeredTime;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCouponCode() {
        return couponCode;
    }

    public void setCouponCode(String couponCode) {
        this.couponCode = couponCode;
    }

    public String getOpenId() {
        return openId;
    }

    public void setOpenId(String openId) {
        this.openId = openId;
    }

    public String getStoreId() {
        return storeId;
    }

    public void setStoreId(String storeId) {
        this.storeId = storeId;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public Date getRegisteredTime() {
        return registeredTime;
    }

    public void setRegisteredTime(Date registeredTime) {
        this.registeredTime = registeredTime;
    }

    @Override
    public String toString() {
        return "EndUserVisitedCouponDAO{" +
        "id=" + id +
        ", couponCode=" + couponCode +
        ", openId=" + openId +
        ", storeId=" + storeId +
        ", createTime=" + createTime +
        ", customerId=" + customerId +
        ", registeredTime=" + registeredTime +
        "}";
    }
}
