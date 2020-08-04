package com.tuhu.store.saas.marketing.response;

import com.tuhu.store.saas.marketing.request.ActivityContent;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 营销活动详情
 */
@Data
@ToString
public class ActivityResp implements Serializable {
    private static final long serialVersionUID = 4155162529645849902L;
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
     * 活动状态,全部，未开始:0,进行中:1,已结束:2
     */
    private Integer dateStatus;

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
     * 报名人数
     */
    private Long applyCount = Long.valueOf(0);

    /**
     * 已核销人数,包括已核销及已开单状态
     */
    private Long writeOffCount = Long.valueOf(0);

    /**
     * 当前用户是否已参加此活动
     */
    private boolean applyed;
    /**
     * 活动订单编码
     */
    private String activityOrderCode;

    /**
     * 活动项目
     */
    private List<ActivityItemResp> items;

    /**
     * 活动内容
     */
    private List<ActivityContent> contents;

    /**
     * 活动费用
     */
    private BigDecimal activityPrice;

}
