package com.tuhu.store.saas.marketing.response.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author wangxiang2
 */
@Data
public class CrdCardOrderExtendDTO {


    private Long id;

    /**
     * 开卡单号
     */
    private String orderNo;

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
     * 车辆ID
     */
    private String vehicleId;

    /**
     * 车牌号
     */
    private String licensePlateNo;

    /**
     * 销售人员ID
     */
    private String salesmanId;

    /**
     * 销售人员姓名
     */
    private String salesmanName;

    /**
     * 开卡单状态
     */
    private String status;

    /**
     * 卡ID
     */
    private Long cardId;

    /**
     * 卡号
     */
    private String cardNo;

    /**
     * 卡名称
     */
    private String cardName;

    /**
     * 卡状态
     */
    private String cardStatus;

    /**
     * 收款状态
     */
    private String paymentStatus;

    /**
     * 付款时间
     */
    private Date paymentTime;

    /**
     * 合计金额
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

    /**
     * 已付金额
     */
    private BigDecimal payedAmount;

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
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新人
     */
    private String updateUser;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 是否删除
     */
    private Byte isDelete;


    /**
     * 卡模板ID
     */
    private Long cardTemplateId;

}
