package com.tuhu.store.saas.marketing.response;

import com.tuhu.store.saas.marketing.request.ActivityContent;
import com.tuhu.store.saas.marketing.request.vo.ClientStoreInfoVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Data
@ToString
@ApiModel("营销活动详情出参")
public class ActivityResp implements Serializable {
    private static final long serialVersionUID = 4155162529645849902L;

    @ApiModelProperty("活动id")
    private Long id;

    @ApiModelProperty("活动编码")
    private String activityCode;

    @ApiModelProperty("加密活动编码")
    private String encryptedCode;

    @ApiModelProperty("门店ID")
    private Long storeId;

    @ApiModelProperty("租户ID")
    private Long tenantId;

    @ApiModelProperty("活动类型， 0：营销活动")
    private Byte type;

    @ApiModelProperty("活动图片url")
    private String picUrl;

    @ApiModelProperty("活动标题")
    private String activityTitle;

    @ApiModelProperty("活动介绍")
    private String activityIntroduce;

    @ApiModelProperty("报名人数限制，-1：不限制人数")
    private Long applyNumber;

    @ApiModelProperty("活动状态，0：下架，1：上架")
    private Boolean status;

    @ApiModelProperty("活动状态,全部，未开始:0,进行中:1,已结束:2")
    private Integer dateStatus;

    @ApiModelProperty("咨询热线")
    private String hotline;

    @ApiModelProperty("付款方式，0：线上报名，到店付款")
    private Boolean payType;

    @ApiModelProperty("活动开始时间")
    private Date startTime;

    @ApiModelProperty("活动结束时间")
    private Date endTime;

    @ApiModelProperty("微信小程序二维码图片链接")
    private String weixinQrUrl;

    @ApiModelProperty("活动标签，多个标签逗号分隔")
    private String tagstring;

    @ApiModelProperty("引用的活动模板ID")
    private Long activityTemplateId;

    @ApiModelProperty("头图引用的活动模板ID")
    private Long picActivityTemplateId;

    @ApiModelProperty("更新人")
    private String updateUser;

    @ApiModelProperty("创建人")
    private String createUser;

    @ApiModelProperty("创建时间")
    private Date createTime;

    @ApiModelProperty("更新时间")
    private Date updateTime;

    @ApiModelProperty("报名人数")
    private Long applyCount = Long.valueOf(0);

    @ApiModelProperty("已核销人数,包括已核销及已开单状态")
    private Long writeOffCount = Long.valueOf(0);

    @ApiModelProperty("当前用户是否已参加此活动")
    private boolean applyed;

    @ApiModelProperty("活动订单编码")
    private String activityOrderCode;

    @ApiModelProperty("活动项目")
    private List<ActivityItemResp> items;

    @ApiModelProperty("活动内容")
    private List<ActivityContent> contents;

    @ApiModelProperty("活动费用")
    private BigDecimal activityPrice;

    @ApiModelProperty("有效期：0-有效天数，1-截止日期")
    private Integer activeType;

    @ApiModelProperty("报名活动后有效天数")
    private Integer activeDays;

    @ApiModelProperty("活动截止日期")
    private Date activeDate;

    @ApiModelProperty("原价格")
    private BigDecimal originalTotalPrice;

    @ApiModelProperty("门店信息")
    private ClientStoreInfoVO storeInfo;

}
