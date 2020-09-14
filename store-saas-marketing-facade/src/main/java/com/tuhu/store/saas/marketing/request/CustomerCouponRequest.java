package com.tuhu.store.saas.marketing.request;

import lombok.Data;

@Data
public class CustomerCouponRequest extends BasePageReq{
    private String customerId;
}
