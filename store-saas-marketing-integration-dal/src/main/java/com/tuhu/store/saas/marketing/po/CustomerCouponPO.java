package com.tuhu.store.saas.marketing.po;

import com.tuhu.store.saas.marketing.dataobject.CustomerCoupon;
import lombok.Data;

/**
 * Created with IntelliJ IDEA.
 * User: pengqiujing
 * Date: 2019/5/16
 * Time: 15:15
 * Description:
 */
@Data
public class CustomerCouponPO extends CustomerCoupon {
    private String customerName;
    private String customerTelephone;
    private Long storeId;
    private String encryptedCode;
    private CouponPO couponInfo;
}