package com.tuhu.store.saas.marketing.mysql.marketing.write.dao;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.tuhu.store.saas.marketing.po.ReservationDateDTO;
import com.tuhu.store.saas.marketing.po.SrvReservationOrder;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public interface SrvReservationOrderMapper extends BaseMapper<SrvReservationOrder> {

    List<Map> getReservedPeriodListForCustomer(HashMap params);

    int updateReservation(SrvReservationOrder order);

    List<SrvReservationOrder> getCReservationList(@Param("storeId") Long storeId, @Param("customerId") String customerId,
                                             @Param("pageIndex") Integer pageIndex, @Param("pageSize") Integer pageSize);

    Long getCReservationCount(@Param("storeId") Long storeId, @Param("customerId") String customerId);

    List<ReservationDateDTO> getReserveDateList(@Param("storeId") Long storeId);

    List<SrvReservationOrder> getBReservationList(@Param("storeId") Long storeId , @Param("reservationDate") String reservationDate);

}