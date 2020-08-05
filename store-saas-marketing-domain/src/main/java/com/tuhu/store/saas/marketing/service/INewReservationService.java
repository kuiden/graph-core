package com.tuhu.store.saas.marketing.service;

import com.tuhu.store.saas.marketing.request.NewReservationReq;
import com.tuhu.store.saas.marketing.request.ReservePeriodReq;
import com.tuhu.store.saas.marketing.response.ReservationDateResp;
import com.tuhu.store.saas.marketing.response.ReservationPeriodResp;

import java.util.List;

/**
 * @Author: yanglanqing
 * @Date: 2020/8/3 16:36
 * 预约相关接口
 */
public interface INewReservationService {

//    List<ReservationDateResp> getReserveDateList(Long storeId);

    List<ReservationPeriodResp> getReservationPeroidList(ReservePeriodReq req);

    /**
     * 创建预约单
     * @param req
     * @param type 门店：0，小程序和H5:1
     * @return
     */
    String addReservation(NewReservationReq req, Integer type);
}
