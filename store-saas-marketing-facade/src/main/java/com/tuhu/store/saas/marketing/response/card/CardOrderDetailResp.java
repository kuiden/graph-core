package com.tuhu.store.saas.marketing.response.card;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * @author wangyuqing
 * @since 2020/8/7 16:21
 */
@Data
public class CardOrderDetailResp {

    /**
     * 开卡单ID
     */
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
     * 客户性别
     */
    private String customerGender;
    /**
     * 客户电话
     */
    private String customerPhoneNumber;

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
     * 卡状态code
     */
    private String cardStatusCode;

    /**
     * 卡状态
     */
    private String cardStatus;

    /**
     * 销售人员ID
     */
    private String salesmanId;

    /**
     * 销售人员姓名
     */
    private String salesmanName;

    /**
     * 收款状态code
     */
    private String paymentStatusCode;

    /**
     * 收款状态
     */
    private String paymentStatus;

    /**
     * 是否永久有效
     */
    private Boolean forever;

    /**
     * 有效期
     */
    private String expiryDate;
    /**
     * 卡面值
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
     * 创建时间
     */
    private Date createTime;

    /*
     * 卡类型
     */
    private String cardTypeCode;

    /*
     * 次卡服务列表
     */
    private List<CardItemResp> cardServiceItem;

    /*
     * 次卡商品列表
     */
    private List<CardItemResp> cardGoodsItem;

    /*
     * 使用记录
     */
    private List<CardUseRecordResp> useRecord;


}
