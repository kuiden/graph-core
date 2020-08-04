package com.tuhu.store.saas.marketing.service;

import com.tuhu.store.saas.marketing.po.SrvReservationOrder;

import java.util.Date;
import java.util.HashSet;

/**
 * @Author: yanglanqing
 * @Date: 2020/8/4 14:59
 */
public interface IReservationOrderService {

    /**
     * 查询客户已预约过的时间段
     * @param date
     * @param customerId
     * @param storeId
     * @return
     */
    HashSet getReservedPeriodListForCustomer(Date date, String customerId, Long storeId);

    /**
     * 新增预约单
     * @param newOrder
     */
    void insert(SrvReservationOrder newOrder);
}
