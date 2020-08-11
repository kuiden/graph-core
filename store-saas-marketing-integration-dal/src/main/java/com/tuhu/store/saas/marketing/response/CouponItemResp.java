package com.tuhu.store.saas.marketing.response;


import com.tuhu.store.saas.marketing.dataobject.Coupon;
import com.tuhu.store.saas.marketing.dataobject.CouponScopeCategory;
import com.tuhu.store.saas.marketing.dataobject.Customer;
import com.tuhu.store.saas.marketing.dataobject.CustomerCoupon;
import com.tuhu.store.saas.user.dto.ClientStoreDTO;
import lombok.Data;

import java.util.Date;
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

    private CouponItemResp.StoreInfo storeInfo;


    /**
     * * 使用状态 0:未使用 1：已使用
     */
    private Byte customerCouponStatus;

    private String customerCouponCode;

    @Data
    public static class StoreInfo {
        private String storeName;
        private String address;
        /**
         * 营业时间起
         */
        private Date openingEffectiveDate;
        /**
         * 营业时间止
         */
        private Date openingExpiryDate;

        /**
         * 经度
         */
        private Double lon;

        /**
         * 纬度
         */
        private Double lat;
        /**
         * 手机号码
         */
        private String mobilePhone;
    }
}