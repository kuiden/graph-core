package com.tuhu.store.saas.marketing.request;

import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 营销活动上下架
 */
@Data
@ToString
public class ActivityChangeStatusReq implements Serializable {
    private static final long serialVersionUID = 7997823276593668203L;
    /**
     * 门店ID
     */
    private Long storeId;

    /**
     * 操作人ID
     */
    private String userId;

    /**
     * 营销活动ID
     */
    @NotNull(message = "营销活动ID不能为空")
    private Long activityId;

    /**
     * 活动状态，0：下架，1：上架
     */
    @NotNull(message = "上下架状态不能为空")
    private Boolean status;
}
