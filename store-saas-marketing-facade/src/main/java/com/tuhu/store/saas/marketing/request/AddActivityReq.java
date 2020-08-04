package com.tuhu.store.saas.marketing.request;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.ToString;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 新增营销活动请求对象
 */
@Data
@ToString
@ApiModel(value = "添加营销活动对象")
public class AddActivityReq implements Serializable {
    private static final long serialVersionUID = -5104038827209915355L;

    /**
     * 门店ID
     */
    private Long storeId;

    /**
     * 公司ID
     */
    private Long companyId;
    /**
     * 租户ID
     */
    private Long tenantId;

    /**
     * 活动类型， 0：营销活动
     */
    private Byte type;

    /**
     * 活动图片url
     */
    @NotBlank(message = "活动图片不能为空")
    private String picUrl;

    /**
     * 活动标题
     */
    @NotBlank(message = "活动标题不能为空")
    @Size(max = 30, message = "活动标题只能在30个字符以内")
    private String activityTitle;

    /**
     * 活动介绍
     */
    @NotBlank(message = "活动介绍不能为空")
    @Size(max = 500, message = "活动标题只能在30个字符以内")
    private String activityIntroduce;

    /**
     * 报名人数限制，-1：不限制人数
     */
    @NotNull(message = "报名人数限制不能为空")
    private Long applyNumber;

    /**
     * 咨询热线
     */
    @NotBlank(message = "咨询热线不能为空")
    private String hotline;

    /**
     * 付款方式，0：线上报名，到店付款
     */
    private Boolean payType;

    /**
     * 活动开始时间
     */
    @NotNull(message = "活动开始时间不能为空")
    private Date startTime;

    /**
     * 活动结束时间
     */
    @NotNull(message = "活动结束时间不能为空")
    private Date endTime;

    /**
     * 创建人
     */
    private String createUser;

    /**
     * 营销活动项目
     */
    @Valid
    @NotNull(message = "活动项目不能为空")
    private List<ActivityItemReq> items;

    /**
     * 活动模板ID
     */
    private Long activityTemplateId;
}
