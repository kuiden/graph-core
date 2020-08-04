package com.tuhu.store.saas.marketing.request;

import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 营销活动列表查询请求
 */
@Data
@ToString
public class ActivityListReq implements Serializable {
    private static final long serialVersionUID = -9118912344718089822L;

    /**
     * 活动名称，模糊查找
     */
    private String title;

    /**
     * 活动状态,全部，未开始:0,进行中:1,已结束:2
     */
    private Integer dateStatus;

    /**
     * 用户ID
     */
    private String userId;

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
}
