package com.tuhu.store.saas.marketing.dataobject;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * crd_card
 * @author 
 */
@Data
public class CrdCard implements Serializable {
    private Long id;

    /**
     * 卡模板ID
     */
    private Long cardTemplateId;

    /**
     * 卡分类编码
     */
    private String cardCategoryCode;

    /**
     * 卡号码
     */
    private String cardNo;

    /**
     * 卡名称
     */
    private String cardName;

    /**
     * 卡种类编码(如:计次卡,计时卡,月卡,年卡)
     */
    private String cardTypeCode;

    /**
     * 是否永久。0 否  1 是
     */
    private Byte forever;

    /**
     * 有效期
     */
    private Date expiryDate;

    /**
     * 金额
     */
    private BigDecimal faceAmount;

    /**
     * 优惠金额
     */
    private BigDecimal discountAmount;

    /**
     * 实付金额
     */
    private BigDecimal actualAmount;

    /**
     * 状态
     */
    private String status;

    /**
     * 描述
     */
    private String description;

    /**
     * 客户ID
     */
    private String customerId;

    /**
     * 客户名称
     */
    private String customerName;

    /**
     * 性别:0(女);1(男)
     */
    private String customerGender;

    /**
     * 客户电话
     */
    private String customerPhoneNumber;

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


    /**
     * 秒杀活动订单id
     */
    private String seckillRegisterRecodeId;

    private static final long serialVersionUID = 1L;

}