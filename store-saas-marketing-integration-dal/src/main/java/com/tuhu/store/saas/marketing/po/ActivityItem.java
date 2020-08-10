package com.tuhu.store.saas.marketing.po;

import lombok.Data;

import java.util.Date;

@Data
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
     * 租户id
     */
    private Long tenantId;

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
}