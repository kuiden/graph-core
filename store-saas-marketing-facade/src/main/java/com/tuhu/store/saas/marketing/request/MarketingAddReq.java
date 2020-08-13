package com.tuhu.store.saas.marketing.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;

/**
 * Create by ZhangXiao on 2019/5/27
 */
@Data
public class MarketingAddReq {

    /**
     * 营销方式 0、优惠券营销 1、活动营销
     */
    @ApiModelProperty(value = "营销方式")
    @NotNull(message = "营销方式不能为空")
    private Byte marketingMethod;

    /**
     * 客户群组表ID
     * 和customerId二选一
     */
    @ApiModelProperty(value = "客户群组id")
    private String customerGroupIds;

    /**
     * 客户ID，多个用逗号分隔
     */
    @ApiModelProperty(value = "客户ID")
    private String customerIds;

    /**
     * 券Ids
     * marketingMethod为0为发送优惠券
     * marketingMethod为1为发送活动
     */
    @ApiModelProperty(value = "券或者活动Id")
    private String couponOrActiveId;

    /**
     * 发送时间 精确到年月日时
     */
    @ApiModelProperty(value = "发送时间")
    @NotNull(message = "发送时间不能为空")
    private Date sendTime;

    /**
     * 发送短信标记，默认发送
     * 目前所有的送券都是需要发送短信
     */
    private boolean messageFlag = true;

    /**
     * 门店ID
     */
    private Long storeId;

    /**
     * 租户ID
     */
    private Long tenantId;

    /**
     * 备注
     */
    private String remark;


    private String orginUrl;

}
