package com.tuhu.store.saas.marketing.request;

import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 优惠券活动查询请求
 */
@Data
@ToString
public class CouponListReq implements Serializable {
    private static final long serialVersionUID = 4015095298360057179L;
    /**
     * 优惠券名称，模糊查找
     */
    private String title;

    /**
     * 优惠券状态
     */
    private Integer status;

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
