package com.tuhu.store.saas.marketing.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
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
    private static final long serialVersionUID = 1L;

    @ApiModelProperty("门店ID")
    private Long storeId;

    @ApiModelProperty("公司ID")
    private Long companyId;

    @ApiModelProperty("租户ID")
    private Long tenantId;

    @ApiModelProperty("活动类型， 0：营销活动")
    private Byte type;

    @ApiModelProperty("活动图片url")
    @NotBlank(message = "活动图片不能为空")
    private String picUrl;

    @ApiModelProperty("活动标题")
    @NotBlank(message = "活动标题不能为空")
    @Size(max = 30, message = "活动标题只能在30个字符以内")
    private String activityTitle;

    @ApiModelProperty("活动介绍")
    @NotBlank(message = "活动介绍不能为空")
    @Size(max = 500, message = "活动标题只能在30个字符以内")
    private String activityIntroduce;

    @ApiModelProperty("报名人数限制，-1：不限制人数")
    @NotNull(message = "报名人数限制不能为空")
    private Long applyNumber = -1l;

    @ApiModelProperty("咨询热线")
    @NotBlank(message = "咨询热线不能为空")
    private String hotline;

    @ApiModelProperty("付款方式，0：线上报名，到店付款")
    private Boolean payType;

    @ApiModelProperty("活动开始时间")
    @NotNull(message = "活动开始时间不能为空")
    private Date startTime;

    @ApiModelProperty("活动结束时间")
    @NotNull(message = "活动结束时间不能为空")
    private Date endTime;

    @ApiModelProperty("创建人")
    private String createUser;

    @ApiModelProperty("营销活动项目")
    @Valid
    @NotNull(message = "活动项目不能为空")
    private List<ActivityItemReq> items;

    @ApiModelProperty("活动模板ID")
    private Long activityTemplateId;
}
