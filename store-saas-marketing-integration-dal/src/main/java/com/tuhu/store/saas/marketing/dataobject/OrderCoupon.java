package com.tuhu.store.saas.marketing.dataobject;

import java.util.Date;

public class OrderCoupon {
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
     * 客户优惠券ID
     */
    private Long customerCouponId;

    /**
     * 门店id
     */
    private Long storeId;

    /**
     * 租户ID
     */
    private Long tenantId;

    /**
     * 工单ID
     */
    private String serviceOrderId;

    /**
     * 优惠券使用时间
     */
    private Date createTime;

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

    public Long getCustomerCouponId() {
        return customerCouponId;
    }

    public void setCustomerCouponId(Long customerCouponId) {
        this.customerCouponId = customerCouponId;
    }

    public Long getStoreId() {
        return storeId;
    }

    public void setStoreId(Long storeId) {
        this.storeId = storeId;
    }

    public Long getTenantId() {
        return tenantId;
    }

    public void setTenantId(Long tenantId) {
        this.tenantId = tenantId;
    }

    public String getServiceOrderId() {
        return serviceOrderId;
    }

    public void setServiceOrderId(String serviceOrderId) {
        this.serviceOrderId = serviceOrderId;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}
