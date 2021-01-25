package com.tuhu.store.saas.marketing.response.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class CustomerMarketCountDTO {
    //次卡总数
    private Integer onceCardCount;
    //优惠券总数
    private Integer couponCount;
    //可用次卡总数
    private Integer useOnceCardCount = 0;
    //储值
    private BigDecimal valueCardAmount = BigDecimal.ZERO;
}
