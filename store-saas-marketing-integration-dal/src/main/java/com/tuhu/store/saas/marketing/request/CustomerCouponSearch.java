package com.tuhu.store.saas.marketing.request;

import com.tuhu.store.saas.marketing.dataobject.CustomerCoupon;
import lombok.Data;

/**
 * Created with IntelliJ IDEA.
 * User: pengqiujing
 * Date: 2019/5/16
 * Time: 11:31
 * Description:
 */
@Data
public class CustomerCouponSearch extends CustomerCoupon {
    /*
    抵用券总张数
     */
    private Long grantNumber;

    private String searchKey;

    private Long storeId;
}