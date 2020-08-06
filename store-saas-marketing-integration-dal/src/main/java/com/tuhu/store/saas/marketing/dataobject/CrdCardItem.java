package com.tuhu.store.saas.marketing.dataobject;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * crd_card_item
 * @author 
 */
public class CrdCardItem implements Serializable {
    private Long id;

    /**
     * 卡ID
     */
    private Long cardId;

    /**
     * 卡名称
     */
    private String cardName;

    /**
     * 商品ID
     */
    private String goodsId;

    /**
     * 产品PID
     */
    private String pid;

    /**
     * 服务项目code
     */
    private String serviceItemCode;

    /**
     * 服务项名称
     */
    private String serviceItemName;

    /**
     * 业务分类
     */
    private String businessCategory;

    /**
     * 业务分类编码
     */
    private String businessCategoryCode;

    /**
     * 业务分类名称
     */
    private String businessCategoryName;

    /**
     * 工时
     */
    private Integer laborHour;

    /**
     * 次数
     */
    private Integer measuredQuantity;

    /**
     * 已使用次数
     */
    private Integer usedQuantity;

    /**
     * 工时单价
     */
    private BigDecimal price;

    /**
     * 金额
     */
    private BigDecimal amount;

    /**
     * 优惠金额
     */
    private BigDecimal discountAmount;

    /**
     * 实付金额
     */
    private BigDecimal actualAmount;

    /**
     * 描述
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
    private Byte isDelete;

    private static final long serialVersionUID = 1L;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCardId() {
        return cardId;
    }

    public void setCardId(Long cardId) {
        this.cardId = cardId;
    }

    public String getCardName() {
        return cardName;
    }

    public void setCardName(String cardName) {
        this.cardName = cardName;
    }

    public String getGoodsId() {
        return goodsId;
    }

    public void setGoodsId(String goodsId) {
        this.goodsId = goodsId;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public String getServiceItemCode() {
        return serviceItemCode;
    }

    public void setServiceItemCode(String serviceItemCode) {
        this.serviceItemCode = serviceItemCode;
    }

    public String getServiceItemName() {
        return serviceItemName;
    }

    public void setServiceItemName(String serviceItemName) {
        this.serviceItemName = serviceItemName;
    }

    public String getBusinessCategory() {
        return businessCategory;
    }

    public void setBusinessCategory(String businessCategory) {
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

    public Integer getLaborHour() {
        return laborHour;
    }

    public void setLaborHour(Integer laborHour) {
        this.laborHour = laborHour;
    }

    public Integer getMeasuredQuantity() {
        return measuredQuantity;
    }

    public void setMeasuredQuantity(Integer measuredQuantity) {
        this.measuredQuantity = measuredQuantity;
    }

    public Integer getUsedQuantity() {
        return usedQuantity;
    }

    public void setUsedQuantity(Integer usedQuantity) {
        this.usedQuantity = usedQuantity;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public BigDecimal getDiscountAmount() {
        return discountAmount;
    }

    public void setDiscountAmount(BigDecimal discountAmount) {
        this.discountAmount = discountAmount;
    }

    public BigDecimal getActualAmount() {
        return actualAmount;
    }

    public void setActualAmount(BigDecimal actualAmount) {
        this.actualAmount = actualAmount;
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

    public Byte getIsDelete() {
        return isDelete;
    }

    public void setIsDelete(Byte isDelete) {
        this.isDelete = isDelete;
    }
}