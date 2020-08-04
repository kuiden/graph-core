package com.tuhu.store.saas.marketing.service.impl;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.tuhu.store.saas.marketing.mysql.marketing.write.dao.SrvReservationOrderMapper;
import com.tuhu.store.saas.marketing.service.IReservationOrderService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @Author: yanglanqing
 * @Date: 2020/8/4 15:00
 */
@Service
public class IReservationOrderServiceImpl implements IReservationOrderService {

    @Autowired
    SrvReservationOrderMapper reservationOrderMapper;

    @Override
    public HashSet getReservedPeriodListForCustomer(Date date, String customerId, Long storeId) {
        HashSet set= Sets.newHashSet();

        if (StringUtils.isBlank(customerId)){
            return set;
        }
        HashMap paramsMap= Maps.newHashMap();
        SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd");
        paramsMap.put("dateString",format.format(date));
        paramsMap.put("customerId",customerId);
        paramsMap.put("storeId",storeId);
        List<Map> list = reservationOrderMapper.getReservedPeriodListForCustomer(paramsMap);
        if (CollectionUtils.isNotEmpty(list)){
            list.forEach(map -> {
                set.add(map.get("dayString"));
            });
        }
        return set;
    }
}
