package com.tuhu.store.saas.marketing.request;

import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 客户活动订单列表查询请求
 */
@Data
@ToString
public class ActivityCustomerListReq implements Serializable {
    private static final long serialVersionUID = 7203240126973957956L;

    /**
     * 手机号或姓名
     */
    private String search;

    /**
     * 营销活动编码
     */
    private String activityCode;

    /**
     * 门店ID
     */
    private Long storeId;

    /**
     * 租户ID
     */
    private Long tenantId;

    @NotNull(message = "pageNum不能为空")
    @Min(0)
    private Integer pageNum = 0;

    @NotNull(message = "pageSize不能为空")
    @Min(1)
    private Integer pageSize = 10;

    private Boolean isFromClient = Boolean.FALSE;
}
