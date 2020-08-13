package com.tuhu.store.saas.marketing.po;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * activity_template
 * @author 
 */
@Data
public class ActivityTemplate implements Serializable {
    /**
     * 模板id
     */
    private Long id;

//    /**
//     * 活动模板编码
//     */
//    private String code;

//    /**
//     * 活动类型， 0：营销活动
//     */
//    private Byte type;

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
     * 模板状态，0：禁用，1：启用
     */
    private Boolean status;

    /**
     * 引用次数
     */
    private Long refenceNumber;

//    /**
//     * 更新人
//     */
//    private String updateUser;
//
//    /**
//     * 创建人
//     */
//    private String createUser;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

//    /**
//     * 标签，多个标签逗号分隔
//     */
//    private String tagstring;
//
//    /**
//     * 活动内容
//     */
//    private String activityContent;

    /**
     * 报名开始时间
     */
    private Date startTime;

    /**
     * 报名结束时间
     */
    private Date endTime;

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

}