package com.tuhu.store.saas.marketing.po;

import java.util.Date;

public class ActivityItem {
    /**
     * 主键id
     */
    private Long id;

    /**
     * 门店ID
     */
    private Long storeId;

    /**
     * 活动编码
     */
    private String activityCode;

    /**
     * 商品ID
     */
    private String goodsId;

    /**
     * 商品编码code
     */
    private String goodsCode;

    /**
     * 商品名称
     */
    private String goodsName;

    /**
     * 商品类型，goods类型（0："商品,:1：服务）
     */
    private Boolean goodsType;

    /**
     * 商品数量
     */
    private Integer itemQuantity;

    /**
     * 商品原单价
     */
    private Long originalPrice;

    /**
     * 商品实际单价
     */
    private Long actualPrice;

    /**
     * 更新人
     */
    private String updateUser;

    /**
     * 创建人
     */
    private String createUser;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 业务分类
     */
    private Long businessCategory;

    /**
     * 业务分类编码
     */
    private String businessCategoryCode;

    /**
     * 业务分类名称
     */
    private String businessCategoryName;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getStoreId() {
        return storeId;
    }

    public void setStoreId(Long storeId) {
        this.storeId = storeId;
    }

    public String getActivityCode() {
        return activityCode;
    }

    public void setActivityCode(String activityCode) {
        this.activityCode = activityCode;
    }

    public String getGoodsId() {
        return goodsId;
    }

    public void setGoodsId(String goodsId) {
        this.goodsId = goodsId;
    }

    public String getGoodsCode() {
        return goodsCode;
    }

    public void setGoodsCode(String goodsCode) {
        this.goodsCode = goodsCode;
    }

    public String getGoodsName() {
        return goodsName;
    }

    public void setGoodsName(String goodsName) {
        this.goodsName = goodsName;
    }

    public Boolean getGoodsType() {
        return goodsType;
    }

    public void setGoodsType(Boolean goodsType) {
        this.goodsType = goodsType;
    }

    public Integer getItemQuantity() {
        return itemQuantity;
    }

    public void setItemQuantity(Integer itemQuantity) {
        this.itemQuantity = itemQuantity;
    }

    public Long getOriginalPrice() {
        return originalPrice;
    }

    public void setOriginalPrice(Long originalPrice) {
        this.originalPrice = originalPrice;
    }

    public Long getActualPrice() {
        return actualPrice;
    }

    public void setActualPrice(Long actualPrice) {
        this.actualPrice = actualPrice;
    }

    public String getUpdateUser() {
        return updateUser;
    }

    public void setUpdateUser(String updateUser) {
        this.updateUser = updateUser;
    }

    public String getCreateUser() {
        return createUser;
    }

    public void setCreateUser(String createUser) {
        this.createUser = createUser;
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

    public Long getBusinessCategory() {
        return businessCategory;
    }

    public void setBusinessCategory(Long businessCategory) {
        this.businessCategory = businessCategory;
    }

    public String getBusinessCategoryCode() {
        return businessCategoryCode;
    }

    public void setBusinessCategoryCode(String businessCategoryCode) {
        this.businessCategoryCode = businessCategoryCode;
    }

    public String getBusinessCategoryName() {
        return businessCategoryName;
    }

    public void setBusinessCategoryName(String businessCategoryName) {
        this.businessCategoryName = businessCategoryName;
    }
}