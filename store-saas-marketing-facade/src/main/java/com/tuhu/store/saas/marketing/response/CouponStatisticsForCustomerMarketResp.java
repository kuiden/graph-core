package com.tuhu.store.saas.marketing.response;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * 营销发券统计数据
 */
@Data
@ToString
public class CouponStatisticsForCustomerMarketResp implements Serializable {
    /**
     * 已发放数量
     */
    private Long sendNumber = 0L;

    /**
     * 已使用数量
     */
    private Long usedNumber = 0L;

    /**
     * 用券工单金额
     */
    private BigDecimal orderAmount;

    /**
     * 用券的工单ID
     */
    private List<String> orderIds;
}
