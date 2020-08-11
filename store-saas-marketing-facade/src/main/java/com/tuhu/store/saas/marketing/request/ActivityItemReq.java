package com.tuhu.store.saas.marketing.request;

import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;

/**
 * 营销活动项目
 */
@Data
@ToString
public class ActivityItemReq implements Serializable {
    private static final long serialVersionUID = -4193798538037025159L;

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
     * 商品ID
     */
    private String pid;

    /**
     * 商品编码code
     */
    @NotNull(message = "服务项目或商品编码不能为空")
    private String goodsCode;

    /**
     * 商品名称
     */
    @NotNull(message = "服务项目或商品名称不能为空")
    private String goodsName;

    /**
     * 商品类型，goods类型（0："商品,:1：服务）
     */
    @NotNull(message = "商品类型不能为空")
    private Boolean goodsType;

    /**
     * 商品数量
     */
    @NotNull(message = "商品数量不能为空")
    private Integer itemQuantity;

    /**
     * 商品原单价
     */
    @NotNull(message = "原单价不能为空")
    private Long originalPrice;

    /**
     * 商品实际单价
     */
    @NotNull(message = "活动价不能为空")
    private Long actualPrice;

    /**
     * 是否来自云端
     */
    private Boolean isFromCloud = Boolean.FALSE;

    /**
     * 项目档位
     */
    private String vehicleType = "A";

    private String userId;

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
