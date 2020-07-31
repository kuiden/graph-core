package com.tuhu.store.saas.marketing.request;

import lombok.Data;

import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: pengqiujing
 * Date: 2019/5/17
 * Time: 14:17
 * Description:
 */
@Data
public class CouponSearchRequest implements Serializable {
    private static final long serialVersionUID = -1372144762203209072L;
    private Long storeId;

    /**
     优惠券类型：0：满减 ，代金券 1：满折，折扣券
     */
    private Integer type;

    private String searchKey;
    private Integer pageNum=1;
    private Integer pageSize=10;
}