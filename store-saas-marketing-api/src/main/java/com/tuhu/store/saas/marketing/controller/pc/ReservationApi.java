package com.tuhu.store.saas.marketing.controller.pc;

import com.tuhu.boot.common.facade.BizBaseResponse;
import com.tuhu.store.saas.marketing.controller.BaseApi;
import com.tuhu.store.saas.marketing.request.ReservePeriodReq;
import com.tuhu.store.saas.marketing.response.ReservationPeriodResp;
import com.tuhu.store.saas.marketing.service.INewReservationService;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @Author: yanglanqing
 * @Date: 2020/8/3 16:26
 */
@RestController
@RequestMapping("/order/reservation")
@Slf4j
public class ReservationApi extends BaseApi {

    @Autowired
    INewReservationService iNewReservationService;

//    @PostMapping(value = "/dateList")
//    @ApiOperation(value = "预约日期list")
//    public BizBaseResponse<List<ReservationDateResp>> getReserveDateList(){
//        BizBaseResponse<List<ReservationDateResp>> result = BizBaseResponse.success();
//        return result;
//    }

    @PostMapping(value = "/periodList")
    @ApiOperation(value = "预约时间段list")
    public BizBaseResponse getReservePeriodList(@RequestBody @Validated ReservePeriodReq req) {
        BizBaseResponse<List<ReservationPeriodResp>> result = BizBaseResponse.success();
        if (req.getStoreId() == null) {
            req.setStoreId(super.getStoreId());
        }
        result.setData(iNewReservationService.getReservationPeroidList(req));
        return result;
    }
}
