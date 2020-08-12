package com.tuhu.store.saas.marketing.controller.mini;

import com.tuhu.boot.common.facade.BizBaseResponse;
import com.tuhu.store.saas.marketing.controller.BaseApi;
import com.tuhu.store.saas.marketing.exception.StoreSaasMarketingException;
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
public class MiniWriteOffApi extends BaseApi {

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
    public BizBaseResponse<String> writeOff(@RequestParam String code) {
        String result = "";
        long storeId = super.getStoreId();
        //code门店校验
        //
        if (code.startsWith("YHQ")) {
            if (!code.startsWith("YHQ" + storeId)) {
                throw new StoreSaasMarketingException("门店校验失败");
            }
            //   couponService.writeOffCustomerCouponV2(code);
            result = "customerCoupon";
        } else if (code.startsWith("YXHD")) {

            if (!code.startsWith("YXHD" + storeId)) {
                throw new StoreSaasMarketingException("门店校验失败");
            }
            result = "activity";
        }
        return new BizBaseResponse<>(result);
    }
}
