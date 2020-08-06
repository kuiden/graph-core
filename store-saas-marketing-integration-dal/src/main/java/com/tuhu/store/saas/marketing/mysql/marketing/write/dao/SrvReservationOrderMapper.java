package com.tuhu.store.saas.marketing.mysql.marketing.write.dao;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.tuhu.store.saas.marketing.po.SrvReservationOrder;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public interface SrvReservationOrderMapper extends BaseMapper<SrvReservationOrder> {

    List<Map> getReservedPeriodListForCustomer(HashMap params);

    int updateReservation(SrvReservationOrder order);
}