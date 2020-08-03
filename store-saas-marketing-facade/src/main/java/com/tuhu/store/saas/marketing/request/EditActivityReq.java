package com.tuhu.store.saas.marketing.request;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.ToString;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * @ClassName EditActivityRequest
 * @Description 营销版
 * @Author fast
 * @Date 2020/3/13 12:51
 * @Version 1.0
 */
@Data
@ToString
@ApiModel(value = "营销活动编辑对象")
public class EditActivityReq implements Serializable {
    private static final long serialVersionUID = -1188637576984297880L;

    /**
     * id
     */
    @NotNull(message = "营销活动ID不能为空")
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
    @NotBlank(message = "活动标题不能为空")
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
    @NotNull(message = "更新时间不能为空")
    private Date updateTime;

    /**
     * 活动内容
     */
    @NotNull(message = "活动内容不能为空")
    private List<ActivityContent> contents;

    /**
     * 活动费用
     */
    @NotNull(message = "活动价格不能为空")
    private BigDecimal activityPrice;

    /**
     * 营销活动项目
     */
    @Valid
    @NotNull(message = "活动项目不能为空")
    private List<ActivityItemReq> items;
}
