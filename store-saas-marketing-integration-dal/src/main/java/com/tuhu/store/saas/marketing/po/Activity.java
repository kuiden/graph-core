package com.tuhu.store.saas.marketing.po;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * activity
 * @author 
 */
@Data
public class Activity implements Serializable {
    /**
     * id
     */
    private Long id;

    /**
     * 活动编码
     */
    private String activityCode;

    /**
     * 加密活动编码
     */
    private String encryptedCode;

    /**
     * 门店ID
     */
    private Long storeId;

    /**
     * 活动类型， 0：营销活动
     */
    private Byte type;

    /**
     * 活动图片url
     */
    private String picUrl;

    /**
     * 活动标题
     */
    private String activityTitle;

    /**
     * 活动介绍
     */
    private String activityIntroduce;

    /**
     * 报名人数限制，-1：不限制人数
     */
    private Long applyNumber;

    /**
     * 活动状态，0：下架，1：上架
     */
    private Boolean status;

    /**
     * 咨询热线
     */
    private String hotline;

    /**
     * 付款方式，0：线上报名，到店付款
     */
    private Boolean payType;

    /**
     * 活动开始时间
     */
    private Date startTime;

    /**
     * 活动结束时间
     */
    private Date endTime;

    /**
     * 微信小程序二维码图片链接
     */
    private String weixinQrUrl;

    /**
     * 活动标签，多个标签逗号分隔
     */
    private String tagstring;

    /**
     * 引用的活动模板ID
     */
    private Long activityTemplateId;

    /**
     * 头图引用的活动模板ID
     */
    private Long picActivityTemplateId;

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
     * 活动内容
     */
    private String activityContent;

    /**
     * 活动价格
     */
    private BigDecimal activityPrice;

    /**
     * 有效期：0-有效天数，1-截止日期
     */
    private Integer activeType;

    /**
     * 报名活动后有效天数
     */
    private Integer activeDays;

    /**
     * 活动截止日期
     */
    private Date activeDate;

    private static final long serialVersionUID = 1L;
}