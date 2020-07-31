package com.tuhu.store.saas.marketing.request;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.util.List;

/**
 * 营销发券统计数据
 */
@Data
@ToString
public class CouponStatisticsForCustomerMarketReq implements Serializable {

    private String couponCode;

    private List<String> customerIds;
}
