package com.tuhu.store.saas.marketing.controller;

import com.tuhu.boot.common.facade.BizBaseResponse;
import com.tuhu.store.saas.marketing.remote.ResultObject;

import com.tuhu.store.saas.marketing.request.vo.ServiceOrderCouponUseVO;
import com.tuhu.store.saas.marketing.request.vo.ServiceOrderCouponVO;
import com.tuhu.store.saas.marketing.response.CouponResp;
import com.tuhu.store.saas.marketing.response.dto.CustomerCouponDTO;
import com.tuhu.store.saas.marketing.response.dto.ServiceOrderCouponDTO;
import com.tuhu.store.saas.marketing.service.ICouponService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/coupon")
public class CouponApi extends BaseApi {

    @Autowired
    private ICouponService iCouponService;

    @RequestMapping(value = "/getCouponsForServiceOrder", method = RequestMethod.POST)
    @ApiOperation(value = "根据工单查询可用优惠券")
    public ResultObject getCouponsForServiceOrder(@RequestBody ServiceOrderCouponVO serviceOrderCouponVO) {
        serviceOrderCouponVO.setStoreId(String.valueOf(this.getStoreId()));
        serviceOrderCouponVO.setTenantId(String.valueOf(this.getTenantId()));
        ServiceOrderCouponDTO serviceOrderCouponDTO = iCouponService.getCouponsForServiceOrder(serviceOrderCouponVO);
        return new ResultObject(serviceOrderCouponDTO);
    }

    @RequestMapping(value = "/writeOffCustomerCouponForServiceOrder", method = RequestMethod.POST)
    @ApiOperation(value = "根据工单核销客户优惠券")
    public ResultObject writeOffCustomerCouponForServiceOrder(@RequestBody ServiceOrderCouponUseVO serviceOrderCouponUseVO) {
        serviceOrderCouponUseVO.setStoreId(String.valueOf(this.getStoreId()));
        serviceOrderCouponUseVO.setTenantId(String.valueOf(this.getTenantId()));
        ServiceOrderCouponDTO serviceOrderCouponDTO = iCouponService.writeOffCustomerCouponForServiceOrder(serviceOrderCouponUseVO);
        return new ResultObject(serviceOrderCouponDTO);
    }

    @RequestMapping(value = "/cancelWriteOffCustomerCouponForServiceOrder", method = RequestMethod.POST)
    @ApiOperation(value = "根据工单取消核销客户优惠券")
    public ResultObject cancelWriteOffCustomerCouponForServiceOrder(@RequestBody ServiceOrderCouponUseVO serviceOrderCouponUseVO) {
        serviceOrderCouponUseVO.setStoreId(String.valueOf(this.getStoreId()));
        serviceOrderCouponUseVO.setTenantId(String.valueOf(this.getTenantId()));
        ServiceOrderCouponDTO serviceOrderCouponDTO = iCouponService.cancelWriteOffCustomerCouponForServiceOrder(serviceOrderCouponUseVO);
        return new ResultObject(serviceOrderCouponDTO);
    }

    @RequestMapping(value = "/getCouponDetailByCustomerCouponId", method = {RequestMethod.POST, RequestMethod.GET})
    @ApiOperation(value = "根据客户优惠券ID查询优惠券详情")
    public ResultObject getCouponDetailByCustomerCouponId(String customerCouponId) {
        CustomerCouponDTO customerCouponDTO = iCouponService.getCouponDetailByCustomerCouponId(customerCouponId, String.valueOf(this.getStoreId()));
        return new ResultObject(customerCouponDTO);
    }

}
