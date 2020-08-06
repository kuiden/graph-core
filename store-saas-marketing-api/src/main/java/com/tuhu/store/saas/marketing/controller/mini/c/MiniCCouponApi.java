package com.tuhu.store.saas.marketing.controller.mini.c;


import com.github.pagehelper.PageInfo;
import com.tuhu.boot.common.facade.BizBaseResponse;
import com.tuhu.store.saas.marketing.context.CustomerContextHolder;
import com.tuhu.store.saas.marketing.controller.BaseApi;
import com.tuhu.store.saas.marketing.dataobject.CustomerCoupon;
import com.tuhu.store.saas.marketing.remote.CustomerAuthDto;
import com.tuhu.store.saas.marketing.remote.ResultObject;
import com.tuhu.store.saas.marketing.request.*;
import com.tuhu.store.saas.marketing.request.vo.ServiceOrderCouponUseVO;
import com.tuhu.store.saas.marketing.request.vo.ServiceOrderCouponVO;
import com.tuhu.store.saas.marketing.response.*;
import com.tuhu.store.saas.marketing.response.dto.CustomerCouponDTO;
import com.tuhu.store.saas.marketing.response.dto.ServiceOrderCouponDTO;
import com.tuhu.store.saas.marketing.service.ICouponService;
import com.tuhu.store.saas.marketing.service.IMCouponService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 优惠券相关Controller
 */
@RestController
@RequestMapping("/mini/c/coupon")
public class MiniCCouponApi extends BaseApi {

    @Autowired
    private ICouponService iCouponService;

    @RequestMapping(value = "/send", method = RequestMethod.POST)
    @ApiOperation(value = "优惠券活动送券")
    public BizBaseResponse<List<CommonResp<CustomerCoupon>>> send(@Validated @RequestBody SendCouponReq sendCouponReq) {
        CustomerAuthDto dto = CustomerContextHolder.getUser();
        sendCouponReq.setUserId(dto.getUserId());
        sendCouponReq.setStoreId(Long.valueOf(dto.getStoreId()));
        sendCouponReq.setTenantId(Long.valueOf(dto.getTenantId()));
        if (sendCouponReq.getReceiveType() == null) {
            sendCouponReq.setReceiveType(Integer.valueOf(0));//手动发券
        }
        List<CommonResp<CustomerCoupon>> customerCouponRespList = iCouponService.sendCoupon(sendCouponReq);
        boolean hasFailed = false;
        for (CommonResp<CustomerCoupon> customerCouponResp : customerCouponRespList) {
            if (!customerCouponResp.isSuccess()) {
                hasFailed = true;
                break;
            }
        }
        BizBaseResponse<List<CommonResp<CustomerCoupon>>> resultObject = new BizBaseResponse<List<CommonResp<CustomerCoupon>>>(customerCouponRespList);
        if (hasFailed) {
            resultObject.setCode(4000);
        }
        return resultObject;
    }


}
