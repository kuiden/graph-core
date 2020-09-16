package com.tuhu.store.saas.marketing.response;

import lombok.Data;

import java.util.Date;

@Data
public class CustomerCouponResponse {
    //优惠券id
    private Long id;
    //领取时间
    private Date createTime;

    private String customerId;
    private String customerName;
    //优惠券状态
    //0:未使用;1:已使用;2:已过期
    private Integer status;



}
