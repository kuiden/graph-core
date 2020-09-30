package com.tuhu.store.saas.marketing.po;

import lombok.Data;
import lombok.ToString;

import java.util.Date;

@ToString
public class SrvReservationOrder {

    private String id;

    /**
     * 预约单号
     */
    private String reservationOrdeNo;

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
     * 预约状态:待确认(UNCONFIRMED);已确认(CONFIRMED);已开单(ORDER);已取消(CANCEL)
     */
    private String status;

    /**
     * 预约备注
     */
    private String description;

    /**
     * 门店ID
     */
    private Long storeId;

    /**
     * 租户ID
     */
    private Long tenantId;

    /**
     * 创建人
     */
    private String createUser;

    /**
     * 更新人
     */
    private String updateUser;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 是否删除
     */
    private Boolean isDelete;


    /**
     * 预约创建终端(0:H5 1:b端  2:c端)
     */
    private Integer teminal;

    /**
     * 预约渠道:MD(门店创建);ZXYY(小程序在线预约);COUPON(优惠券营销);ACTIVITY(活动营销);SUBCARD(次卡办理)
     */
    private String sourceChannel;

    public String getSourceChannel() {
        return sourceChannel;
    }

    public void setSourceChannel(String sourceChannel) {
        this.sourceChannel = sourceChannel;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getReservationOrdeNo() {
        return reservationOrdeNo;
    }

    public void setReservationOrdeNo(String reservationOrdeNo) {
        this.reservationOrdeNo = reservationOrdeNo;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getCustomerPhoneNumber() {
        return customerPhoneNumber;
    }

    public void setCustomerPhoneNumber(String customerPhoneNumber) {
        this.customerPhoneNumber = customerPhoneNumber;
    }

    public Date getEstimatedArriveTime() {
        return estimatedArriveTime;
    }

    public void setEstimatedArriveTime(Date estimatedArriveTime) {
        this.estimatedArriveTime = estimatedArriveTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public String getCreateUser() {
        return createUser;
    }

    public void setCreateUser(String createUser) {
        this.createUser = createUser;
    }

    public String getUpdateUser() {
        return updateUser;
    }

    public void setUpdateUser(String updateUser) {
        this.updateUser = updateUser;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public Boolean getDelete() {
        return isDelete;
    }

    public void setDelete(Boolean delete) {
        isDelete = delete;
    }

    public Integer getTeminal() {
        return teminal;
    }

    public void setTeminal(Integer teminal) {
        this.teminal = teminal;
    }
}