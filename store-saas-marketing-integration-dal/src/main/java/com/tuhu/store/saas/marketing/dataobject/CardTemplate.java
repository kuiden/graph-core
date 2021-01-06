package com.tuhu.store.saas.marketing.dataobject;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * <p>
 * 卡模板
 * </p>
 *
 * @author sunkuo
 * @since 2018-11-20
 */
@Setter
@Getter
@ToString
public class CardTemplate implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    /**
     * 卡分类编码
     */
    private String cardCategoryCode;

    /**
     * 卡名称
     */
    private String cardName;

    /**
     * 有效期类型，0-有效月份，1-永久有效，2-有效天数，3-有效截止日期
     */
    private Integer expiryType;
    /**
     * 有效月份
     */
    private Integer expiryPeriod;
    /**
     * 次卡有效截止日期
     */
    private Date expiryDate;
    /**
     * 次卡有效天数
     */
    private Integer expiryDay;

    /**
     * 是否是永久
     */
    private Boolean forever;

    /**
     * 模板状态
     */
    private String status;

    /**
     * 卡面值
     */
    private BigDecimal faceAmount;


    /**
     * 卡实额
     */

    private BigDecimal actualAmount;

    /**
     * 卡优惠金额
     */
    private BigDecimal discountAmount;

    /**
     * 卡种类编码(如:计次卡,计时卡,月卡,年卡)
     */
    private String cardTypeCode;

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


    /**
     * 是否做版本校验
     */
    private Boolean judgeVersion = false;

    private Byte type ;

    /**
     * 是否车主端展示
     */
    private Byte isShow;


    private List<CardTemplateItem> cardTemplateItemList;



}
