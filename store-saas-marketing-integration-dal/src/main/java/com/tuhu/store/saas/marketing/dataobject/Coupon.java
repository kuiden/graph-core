package com.tuhu.store.saas.marketing.dataobject;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * coupon
 * @author 
 */
public class Coupon implements Serializable {
    /**
     * 优惠券活动ID
     */
    private Long id;

    /**
     * 优惠券编码
     */
    private String code;

    /**
     * 加密后的优惠券编码
     */
    private String encryptedCode;

    /**
     * 门店ID
     */
    private Long storeId;

    /**
     * 租户ID
     */
    private Long tenantId;

    /**
     * 优惠券类型：0：满减 ，代金券 1：满折，折扣券
     */
    private Byte type;

    /**
     * 有效期类型: 0：指定有效期时间 1：结束时间-相对时间
     */
    private Byte validityType;

    /**
     * 优惠券名称
     */
    private String title;

    /**
     * 优惠券使用条件金额
     */
    private BigDecimal conditionLimit;

    /**
     * 代金券优惠金额
     */
    private BigDecimal contentValue;

    /**
     * 折扣券折扣数
     */
    private BigDecimal discountValue;

    /**
     * 使用开始时间
     */
    private Date useStartTime;

    /**
     * 使用结束时间
     */
    private Date useEndTime;

    /**
     * validity_type=1时,相对领取时间天数
     */
    private Integer relativeDaysNum;

    /**
     * 发放总量
     */
    private Long grantNumber;

    /**
     * 优惠券状态,0:禁用  1:启用
     */
    private Byte status;

    /**
     * 是否允许领取 0：不允许 1：允许
     */
    private Byte allowGet;

    /**
     * 适用范围类型 0：不限 1：限定商品 2：限定分类
     */
    private Byte scopeType;

    /**
     * 券说明
     */
    private String remark;

    /**
     * 优惠券领取页，微信小程序二维码图片链接
     */
    private String weixinQrUrl;

    /**
     * 创建人
     */
    private String createUser;

    /**
     * 更新用户
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
     * 占用数量
     */
    private Long occupyNum;

    private static final long serialVersionUID = 1L;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getEncryptedCode() {
        return encryptedCode;
    }

    public void setEncryptedCode(String encryptedCode) {
        this.encryptedCode = encryptedCode;
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

    public Byte getType() {
        return type;
    }

    public void setType(Byte type) {
        this.type = type;
    }

    public Byte getValidityType() {
        return validityType;
    }

    public void setValidityType(Byte validityType) {
        this.validityType = validityType;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public BigDecimal getConditionLimit() {
        return conditionLimit;
    }

    public void setConditionLimit(BigDecimal conditionLimit) {
        this.conditionLimit = conditionLimit;
    }

    public BigDecimal getContentValue() {
        return contentValue;
    }

    public void setContentValue(BigDecimal contentValue) {
        this.contentValue = contentValue;
    }

    public BigDecimal getDiscountValue() {
        return discountValue;
    }

    public void setDiscountValue(BigDecimal discountValue) {
        this.discountValue = discountValue;
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

    public Integer getRelativeDaysNum() {
        return relativeDaysNum;
    }

    public void setRelativeDaysNum(Integer relativeDaysNum) {
        this.relativeDaysNum = relativeDaysNum;
    }

    public Long getGrantNumber() {
        return grantNumber;
    }

    public void setGrantNumber(Long grantNumber) {
        this.grantNumber = grantNumber;
    }

    public Byte getStatus() {
        return status;
    }

    public void setStatus(Byte status) {
        this.status = status;
    }

    public Byte getAllowGet() {
        return allowGet;
    }

    public void setAllowGet(Byte allowGet) {
        this.allowGet = allowGet;
    }

    public Byte getScopeType() {
        return scopeType;
    }

    public void setScopeType(Byte scopeType) {
        this.scopeType = scopeType;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getWeixinQrUrl() {
        return weixinQrUrl;
    }

    public void setWeixinQrUrl(String weixinQrUrl) {
        this.weixinQrUrl = weixinQrUrl;
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

    public Long getOccupyNum() {
        return occupyNum;
    }

    public void setOccupyNum(Long occupyNum) {
        this.occupyNum = occupyNum;
    }
}