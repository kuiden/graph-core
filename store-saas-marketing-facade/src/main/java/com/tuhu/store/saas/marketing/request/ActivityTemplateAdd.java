package com.tuhu.store.saas.marketing.request;

import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.Date;

@Data
public class ActivityTemplateAdd implements Serializable {
    private static final long serialVersionUID = 848365289241508305L;
    /**
     * 模板id
     */
    private Long id;

    /**
     * 活动图片url
     */
    @NotNull(message = "活动图片url不能为空")
    private String picUrl;

    /**
     * 活动标题
     */
    @NotNull(message = "活动标题不能为空")
    @Size(max = 30, message = "活动标题最多30个汉字")
    private String activityTitle;

    /**
     * 活动介绍
     */
    @NotNull(message = "活动介绍不能为空")
    @Size(max = 200, message = "活动介绍最多200个汉字")
    private String activityIntroduce;

    /**
     * 报名人数限制，-1：不限制人数
     */
    @NotNull(message = "报名人数限制不能为空")
    private Long applyNumber;

    /**
     * 模板状态，0：禁用，1：启用
     */
    @NotNull(message = "模板状态不能为空")
    private Boolean status;

    /**
     * 报名开始时间
     */
    @NotNull(message = "报名开始时间不能为空")
    private Date startTime;

    /**
     * 报名结束时间
     */
    @NotNull(message = "报名结束时间不能为空")
    private Date endTime;

    /**
     * 有效期：0-有效天数，1-截止日期
     */
    @NotNull(message = "有效期类型不能为空")
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