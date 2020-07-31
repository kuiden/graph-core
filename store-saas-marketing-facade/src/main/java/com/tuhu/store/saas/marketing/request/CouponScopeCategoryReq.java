package com.tuhu.store.saas.marketing.request;

import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 优惠券适用分类
 */
@Data
@ToString
public class CouponScopeCategoryReq implements Serializable {
    private static final long serialVersionUID = -4113322467530691241L;

    private Long id;

    /**
     * 分类code
     */
    @NotNull(message = "分类不能为空")
    private String categoryCode;
}
