package com.tuhu.store.saas.marketing.po;

import com.tuhu.store.saas.marketing.dataobject.Coupon;
import com.tuhu.store.saas.marketing.dataobject.CouponScopeCategory;
import lombok.Data;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: pengqiujing
 * Date: 2019/6/3
 * Time: 17:02
 * Description:
 */
@Data
public class CouponPO extends Coupon {
    private List<CouponScopeCategory> couponScopeCategories;
}