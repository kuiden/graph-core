package com.tuhu.store.saas.marketing.response.card;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @author wangyuqing
 * @since 2020/8/7 16:31
 */
@Data
public class CardItemResp {

    /*
     * 服务项目id
     */
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
     * 次数
     */
    private Integer measuredQuantity;

    /**
     * 已使用次数
     */
    private Integer usedQuantity;

    /*
     * 剩余次数
     */
    private Integer remainQuantity;

    /**
     * 商品类型1：服务 2：商品
     */
    private Byte type;

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

    /*
     * 库存
     */
    private BigDecimal inventory;

    /**
     * 描述
     */
    private String description;

}
