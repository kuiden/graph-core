package com.tuhu.store.saas.marketing.controller.mini;

import com.tuhu.boot.common.facade.BizBaseResponse;
import com.tuhu.store.saas.marketing.request.ActivityCustomerReq;
import com.tuhu.store.saas.marketing.service.IActivityService;
import com.tuhu.store.saas.marketing.service.ICouponService;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@Slf4j
@Api("核销API")
@RestController
@RequestMapping("/mini/writeOff")
public class MiniWriteOffApi {

    @Autowired
    private ICouponService couponService;
    @Autowired
    private IActivityService activityService;

    /**
     * 核销扫码
     *
     * @param code
     * @return
     */
    @RequestMapping(value = "/writeOff", method = RequestMethod.GET)
    public BizBaseResponse<Boolean> writeOff(@RequestParam String code) {
        Boolean result = Boolean.FALSE;
        if (code.startsWith("YHQ")) {
            log.info("调用优惠券核销接口 -> {}", code);
            couponService.writeOffCustomerCouponV2(code);
            result = Boolean.FALSE;
        } else if (code.startsWith("YXHD")) {
            log.info("调用活动核销接口 -> {}", code);
            ActivityCustomerReq req = new ActivityCustomerReq();
            req.setActivityOrderCode(code);
            req.setUseStatus(Integer.valueOf(1));
            result = activityService.writeOffOrCancelActivityCustomer(req);
        }
        return new BizBaseResponse<>(result);
    }
}
