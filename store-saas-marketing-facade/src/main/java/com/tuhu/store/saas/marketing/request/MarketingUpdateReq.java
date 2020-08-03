package com.tuhu.store.saas.marketing.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * Create by ZhangXiao on 2019/5/27
 */
@Data
public class MarketingUpdateReq {


    /**
     * 表主键ID
     */
    @ApiModelProperty(value = "定向营销任务id")
    @NotNull(message = "id不能为空")
    private Long id;

    /**
     * 任务状态 0、待发送 1、已发送 2、已取消 3、发送失败
     */
    @ApiModelProperty(value = "任务状态")
    @NotNull(message = "任务状态不能为空")
    private Byte taskType;

    /**
     * 更新人
     */
    private String updateUser;

    /**
     * 更新时间
     */
    private Date updateTime;

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

    /**
     * 是否做版本校验
     */
    private Boolean judgeVersion = false;

}
