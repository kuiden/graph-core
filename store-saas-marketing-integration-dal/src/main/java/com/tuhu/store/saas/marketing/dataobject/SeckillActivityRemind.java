package com.tuhu.store.saas.marketing.dataobject;

import com.baomidou.mybatisplus.enums.IdType;
import java.util.Date;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import java.io.Serializable;

/**
 * <p>
 * 秒杀活动开抢提醒
 * </p>
 *
 * @author wangyuqing
 * @since 2020-12-08
 */
@TableName("seckill_activity_remind")
public class SeckillActivityRemind implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    /**
     * 小程序openId
     */
    @TableField("open_id")
    private String openId;
    /**
     * 模板id
     */
    @TableField("template_id")
    private String templateId;
    /**
     * 小程序页面url
     */
    private String page;
    /**
     * 活动id
     */
    @TableField("seckill_activity_id")
    private String seckillActivityId;
    /**
     * 门店地址
     */
    @TableField("store_address")
    private String storeAddress;
    /**
     * 门店名称
     */
    @TableField("store_name")
    private String storeName;
    /**
     * 门店电话
     */
    @TableField("store_phone")
    private String storePhone;
    @TableField("store_id")
    private Long storeId;
    @TableField("tenant_id")
    private Long tenantId;
    @TableField("create_time")
    private Date createTime;
    @TableField("update_time")
    private Date updateTime;
    @TableField("is_delete")
    private Integer isDelete;
    /**
     * 发送状态 0未发送 1成功 2失败
     */
    private Integer status;
    /**
     * 返回消息
     */
    @TableField("return_message")
    private String returnMessage;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOpenId() {
        return openId;
    }

    public void setOpenId(String openId) {
        this.openId = openId;
    }

    public String getTemplateId() {
        return templateId;
    }

    public void setTemplateId(String templateId) {
        this.templateId = templateId;
    }

    public String getPage() {
        return page;
    }

    public void setPage(String page) {
        this.page = page;
    }

    public String getSeckillActivityId() {
        return seckillActivityId;
    }

    public void setSeckillActivityId(String seckillActivityId) {
        this.seckillActivityId = seckillActivityId;
    }

    public String getStoreAddress() {
        return storeAddress;
    }

    public void setStoreAddress(String storeAddress) {
        this.storeAddress = storeAddress;
    }

    public String getStoreName() {
        return storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    public String getStorePhone() {
        return storePhone;
    }

    public void setStorePhone(String storePhone) {
        this.storePhone = storePhone;
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

    public Integer getIsDelete() {
        return isDelete;
    }

    public void setIsDelete(Integer isDelete) {
        this.isDelete = isDelete;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getReturnMessage() {
        return returnMessage;
    }

    public void setReturnMessage(String returnMessage) {
        this.returnMessage = returnMessage;
    }

    @Override
    public String toString() {
        return "SeckillActivityRemindDAO{" +
                "id=" + id +
                ", openId=" + openId +
                ", templateId=" + templateId +
                ", page=" + page +
                ", seckillActivityId=" + seckillActivityId +
                ", storeAddress=" + storeAddress +
                ", storeName=" + storeName +
                ", storePhone=" + storePhone +
                ", storeId=" + storeId +
                ", tenantId=" + tenantId +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                ", isDelete=" + isDelete +
                ", status=" + status +
                ", returnMessage=" + returnMessage +
                "}";
    }
}
