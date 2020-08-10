package com.tuhu.store.saas.marketing.response;


import com.tuhu.store.saas.marketing.dataobject.Coupon;
import com.tuhu.store.saas.marketing.dataobject.CouponScopeCategory;
import com.tuhu.store.saas.marketing.dataobject.Customer;
import com.tuhu.store.saas.marketing.dataobject.CustomerCoupon;
import lombok.Data;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: pengqiujing
 * Date: 2019/5/17
 * Time: 14:35
 * Description:
 */
@Data
public class CouponItemResp extends Coupon {
    /**
     * 券剩余数量
     */
    private Long leftCouponNumber;

    /**
     * 当前用户是否已领取
     */

    private boolean hasReceived;

    /**
     * 限定分类信息
     */
    private List<CouponScopeCategory> couponScopeCategories;

    /**
     * * 使用状态 0:未使用 1：已使用
     */
    private Byte customerCouponStatus;

    private String customerCouponCode;
}