package com.tuhu.store.saas.marketing.dataobject;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 车主端用户访问的门店记录
 * </p>
 *
 * @author someone
 * @since 2020-08-03
 */
@TableName("end_user_visited_store")
public class EndUserVisitedStoreDAO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String id;
    /**
     * 车主端小程序微信openId
     */
    @TableField("open_id")
    private String openId;
    /**
     * 门店id
     */
    @TableField("store_id")
    private String storeId;
    /**
     * 浏览次数
     */
    private Long count;
    /**
     * 创建时间
     */
    @TableField("create_time")
    private Date createTime;
    /**
     * 更新时间
     */
    @TableField("update_time")
    private Date updateTime;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public Long getCount() {
        return count;
    }

    public void setCount(Long count) {
        this.count = count;
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

    @Override
    public String toString() {
        return "EndUserVisitedStoreDAO{" +
        "id=" + id +
        ", openId=" + openId +
        ", storeId=" + storeId +
        ", count=" + count +
        ", createTime=" + createTime +
        ", updateTime=" + updateTime +
        "}";
    }
}
