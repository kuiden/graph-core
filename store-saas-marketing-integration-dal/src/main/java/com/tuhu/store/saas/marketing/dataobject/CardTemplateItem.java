package com.tuhu.store.saas.marketing.dataobject;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * <p>
 * 卡模板所属服务项目
 * </p>
 *
 * @author sunkuo
 * @since 2018-11-20
 */
@Setter
@Getter
@ToString
public class CardTemplateItem implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    /**
     * 卡模板ID
     */
    private Long cardTemplateId;
    /**
     * 商品ID
     */
    private String goodsId;
    /**
     * 产品PID
     */
    private String pid;
    /**
     * 服务项名称
     */
    private String serviceItemName;

    /**
     * 服务项名code
     */
    private String serviceItemCode;

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
     * 工时单价
     */
    private BigDecimal price;

    /**
     * 金额
     */
    private BigDecimal faceAmount;
    /**
     * 总优惠金额
     */
    private BigDecimal discountAmount;


    /**
     * 实额
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
    private Boolean isDelete;

    private Byte type;
}
