package com.tuhu.store.saas.marketing.service;

import com.github.pagehelper.PageInfo;
import com.tuhu.store.saas.marketing.request.CReservationListReq;
import com.tuhu.store.saas.marketing.request.NewReservationReq;
import com.tuhu.store.saas.marketing.request.ReservePeriodReq;
import com.tuhu.store.saas.marketing.response.ReservationDateResp;
import com.tuhu.store.saas.marketing.response.ReservationPeriodResp;
import com.tuhu.store.saas.marketing.response.dto.ReservationDTO;

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
     * @param type 门店：0,小程序：1,H5:2
     * @return
     */
    String addReservation(NewReservationReq req, Integer type);

    Boolean updateReservation(NewReservationReq req);

    PageInfo<ReservationDTO> getCReservationList(CReservationListReq req);

    List<ReservationDateResp> getReserveDateList(Long storeId);
}
