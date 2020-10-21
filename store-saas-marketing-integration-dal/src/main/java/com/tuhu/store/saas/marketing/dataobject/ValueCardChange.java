package com.tuhu.store.saas.marketing.dataobject;

import com.tuhu.store.saas.marketing.context.UserContextHolder;
import com.tuhu.store.saas.marketing.request.valueCard.ValueCardRechargeOrRefundReq;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * value_card_change
 * @author 
 */
public class ValueCardChange implements Serializable {
    private Long id;

    /**
     * 储值卡id
     */
    private Long cardId;

    private Long storeId;

    private Long tenantId;

    /**
     * 变更单号
     */
    private String changeNo;

    /**
     * 关联业务单号
     */
    private String orderNo;

    /**
     * 关联营收单号
     */
    private String finNo;

    /**
     * 销售人员ID
     */
    private String salesmanId;

    /**
     * 销售人员姓名
     */
    private String salesmanName;

    /**
     * 本金变动
     */
    private BigDecimal changePrincipal;

    /**
     * 赠金变动
     */
    private BigDecimal changePresent;

    /**
     * 变更后账户余额
     */
    private BigDecimal amount;

    /**
     * 变更类型（0退款 1消费 2充值 3取消退款 4取消消费 5取消充值）
     */
    private Integer changeType;

    /**
     * 0未生效 1已生效
     */
    private Boolean status;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 备注
     */
    private String remark;

    /**
     * 创建人id
     */
    private String createUserId;

    /**
     * 更新人id
     */
    private String updateUserId;

    /**
     * 创建人姓名
     */
    private String createUserName;

    /**
     * 删除标识
     */
    private Boolean isDelete;

    private static final long serialVersionUID = 1L;
    public ValueCardChange(){}

    /**
     * 充值和退款初始化
     * @param valueCard
     * @param req
     */
    public ValueCardChange(ValueCard valueCard, ValueCardRechargeOrRefundReq req) {
        this.setCardId(valueCard.getId());
        this.tenantId = valueCard.getTenantId();
        this.storeId = valueCard.getStoreId();
        this.salesmanId = req.getSalesmanId();
        this.salesmanName = req.getSalesmanName();
        if (valueCard.getAmount().compareTo(BigDecimal.ZERO) == 0) {
            //如果当前数据为初始化的数据则当前变更的总额就是 当前客户总额
            this.amount = req.getChangePrincipal();
        }
        this.status = Boolean.FALSE;
        this.isDelete = Boolean.FALSE;
        this.changeType = req.getType();
        this.createUserName = UserContextHolder.getName();
        this.createUserId= UserContextHolder.getStoreUserId();
        this.updateUserId = UserContextHolder.getStoreUserId();
        this.changePrincipal = req.getChangePrincipal();
        this.createTime = valueCard.getUpdateTime();
        this.updateTime = valueCard.getUpdateTime();
        this.remark = req.getRemark();
        this.changePresent = req.getChangePresent();
    }

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

    public String getChangeNo() {
        return changeNo;
    }

    public void setChangeNo(String changeNo) {
        this.changeNo = changeNo;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public String getFinNo() {
        return finNo;
    }

    public void setFinNo(String finNo) {
        this.finNo = finNo;
    }

    public String getSalesmanId() {
        return salesmanId;
    }

    public void setSalesmanId(String salesmanId) {
        this.salesmanId = salesmanId;
    }

    public String getSalesmanName() {
        return salesmanName;
    }

    public void setSalesmanName(String salesmanName) {
        this.salesmanName = salesmanName;
    }

    public BigDecimal getChangePrincipal() {
        return changePrincipal;
    }

    public void setChangePrincipal(BigDecimal changePrincipal) {
        this.changePrincipal = changePrincipal;
    }

    public BigDecimal getChangePresent() {
        return changePresent;
    }

    public void setChangePresent(BigDecimal changePresent) {
        this.changePresent = changePresent;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public Integer getChangeType() {
        return changeType;
    }

    public void setChangeType(Integer changeType) {
        this.changeType = changeType;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
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

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getCreateUserId() {
        return createUserId;
    }

    public void setCreateUserId(String createUserId) {
        this.createUserId = createUserId;
    }

    public String getUpdateUserId() {
        return updateUserId;
    }

    public void setUpdateUserId(String updateUserId) {
        this.updateUserId = updateUserId;
    }

    public String getCreateUserName() {
        return createUserName;
    }

    public void setCreateUserName(String createUserName) {
        this.createUserName = createUserName;
    }

    public Boolean getIsDelete() {
        return isDelete;
    }

    public void setIsDelete(Boolean isDelete) {
        this.isDelete = isDelete;
    }
}