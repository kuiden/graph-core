package com.tuhu.store.saas.marketing.controller.feign;

import com.tuhu.boot.common.facade.BizBaseResponse;
import com.tuhu.store.saas.marketing.service.IReservationOrderService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author wangyuqing
 * @since 2020/9/4 10:03
 */
@Api("预约单对外接口")
@RequestMapping("/feign/reservationOrder")
@RestController
@Slf4j
public class ReservationOrderFeignApi {

    @Autowired
    private IReservationOrderService reservationOrderService;

    @ApiOperation("获取门店预约数")
    @GetMapping("/countReservationOrder")
    public BizBaseResponse<Integer> countReservationOrder(@RequestParam Long tenantId, @RequestParam Long storeId){
        return new BizBaseResponse(reservationOrderService.countReservationOrder(tenantId,storeId));
    }

}
