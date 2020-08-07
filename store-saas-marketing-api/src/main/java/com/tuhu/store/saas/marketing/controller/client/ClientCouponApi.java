package com.tuhu.store.saas.marketing.controller.client;


import com.tuhu.boot.common.facade.BizBaseResponse;
import com.tuhu.store.saas.marketing.context.EndUserContextHolder;
import com.tuhu.store.saas.marketing.controller.BaseApi;
import com.tuhu.store.saas.marketing.remote.EndUser;
import com.tuhu.store.saas.marketing.remote.ResultObject;
import com.tuhu.store.saas.marketing.request.CouponReceiveRecordRequest;
import com.tuhu.store.saas.marketing.request.CouponRequest;
import com.tuhu.store.saas.marketing.request.CouponSearchRequest;
import com.tuhu.store.saas.marketing.response.CouponPageResp;
import com.tuhu.store.saas.marketing.response.CustomerCouponPageResp;
import com.tuhu.store.saas.marketing.service.ICouponService;
import com.tuhu.store.saas.marketing.service.IMCouponService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 优惠券相关Controller
 */
@RestController
@RequestMapping("/mini/c/coupon")
public class ClientCouponApi extends BaseApi {

    @Autowired
    private ICouponService iCouponService;
    @Autowired
    private IMCouponService imCouponService;

    @RequestMapping(value = "/getCoupon", method = RequestMethod.POST)
    @ApiOperation(value = "领券")
    public BizBaseResponse getCoupon(@Validated @RequestBody CouponRequest req) {
        EndUser dto =  EndUserContextHolder.getUser();
         Map map = imCouponService.getCoupon(req, dto.getUserId());
      //  Map map = imCouponService.getCoupon(req, "159006368380700017990" );
        return new BizBaseResponse(map);
    }

    /**
     * 领券中心
     * @param req
     * @return
     */
    @GetMapping("/client/getCouponList")
    public BizBaseResponse getCouponList(CouponSearchRequest req) {
        EndUser dto =  EndUserContextHolder.getUser();
          CouponPageResp result = imCouponService.getCouponList(req, dto.getUserId());
      //  CouponPageResp result = imCouponService.getCouponList(req, "159006368380700017990");
        return new BizBaseResponse(result);
    }

    /**
     * 我的优惠券列表
     *
     * @param req
     * @return
     */
    @GetMapping("/client/myCouponList")
    public BizBaseResponse getMyCouponList(CouponReceiveRecordRequest req) {
      //  CustomerAuthDto dto = CustomerContextHolder.getUser();
     //   CustomerCouponPageResp map = imCouponService.getMyCouponList(req, dto.getUserId());
        CustomerCouponPageResp map = imCouponService.getMyCouponList(req, "159006368380700017990");
        return new BizBaseResponse(map);
    }


    @GetMapping("/client/couponDetail")
    public ResultObject getCouponDetailForClient(CouponRequest req) {
        EndUser dto =  EndUserContextHolder.getUser();
         //   Map result = imCouponService.getCouponDetailForClient(req, customerId);
        String customerId = "159006368380700017990";
        Map result = imCouponService.getCouponDetailForClient(req, customerId);
        return new ResultObject(result);
    }





}
