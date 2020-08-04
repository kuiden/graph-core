package com.tuhu.store.saas.marketing.mysql.marketing.write.dao;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public interface SrvReservationOrderMapper {

    List<Map> getReservedPeriodListForCustomer(HashMap params);
}