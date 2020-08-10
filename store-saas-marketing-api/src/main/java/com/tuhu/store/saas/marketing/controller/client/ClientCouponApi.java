package com.tuhu.store.saas.marketing.controller.client;


import com.tuhu.boot.common.facade.BizBaseResponse;
import com.tuhu.store.saas.marketing.context.EndUserContextHolder;
import com.tuhu.store.saas.marketing.controller.BaseApi;
import com.tuhu.store.saas.marketing.exception.StoreSaasMarketingException;
import com.tuhu.store.saas.marketing.remote.EndUser;
import com.tuhu.store.saas.marketing.remote.ResultObject;
import com.tuhu.store.saas.marketing.request.CouponReceiveRecordRequest;
import com.tuhu.store.saas.marketing.request.CouponRequest;
import com.tuhu.store.saas.marketing.request.CouponSearchRequest;
import com.tuhu.store.saas.marketing.response.CouponItemResp;
import com.tuhu.store.saas.marketing.response.CouponPageResp;
import com.tuhu.store.saas.marketing.response.CustomerCouponPageResp;
import com.tuhu.store.saas.marketing.service.ICouponService;
import com.tuhu.store.saas.marketing.service.IMCouponService;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 优惠券相关Controller
 */
@RestController
@RequestMapping("/mini/c/coupon")
@Slf4j
public class ClientCouponApi extends BaseApi {
    @Autowired
    private IMCouponService imCouponService;

    @RequestMapping(value = "/getCoupon", method = RequestMethod.POST)
    @ApiOperation(value = "领券")
    public BizBaseResponse getCoupon(@Validated @RequestBody CouponRequest req) {
        EndUser dto = EndUserContextHolder.getUser();
        Map map = imCouponService.getCoupon(req, dto.getUserId());
        //  Map map = imCouponService.getCoupon(req, "159006368380700017990" );
        return new BizBaseResponse(map);
    }

    /**
     * 领券中心
     *
     * @param req
     * @return
     */
    @GetMapping("/client/getCouponList")
    @ApiOperation(value = "领券中心")
    public BizBaseResponse getCouponList(CouponSearchRequest req) {
        EndUser dto = EndUserContextHolder.getUser();
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
    @ApiOperation(value = "我的优惠券列表")
    @GetMapping("/client/myCouponList")
    public BizBaseResponse getMyCouponList(CouponReceiveRecordRequest req) {
        EndUser dto = EndUserContextHolder.getUser();
        CustomerCouponPageResp map = imCouponService.getMyCouponList(req, dto.getUserId());
        //  CustomerCouponPageResp map = imCouponService.getMyCouponList(req, "159006368380700017990");
        return new BizBaseResponse(map);
    }


    /**
     * 获取车主端优惠券详情
     *
     * @param req
     * @return
     */
    @GetMapping("/client/couponDetail")
    public BizBaseResponse getCouponDetailForClient(CouponRequest req) {
        EndUser dto = EndUserContextHolder.getUser();
        //   Map result = imCouponService.getCouponDetailForClient(req, customerId);
        //  String customerId = dto.getUserId();// "159006368380700017990";
        Map result = imCouponService.getCouponDetailForClient(req, dto.getUserId());
        return new BizBaseResponse(result);
    }

    /**
     * 绕权限查询优惠券模板
     *
     * @return
     */
    @GetMapping("/open/openGetCouponInfo")
    public BizBaseResponse openGetCouponInfo(@RequestParam String code) {
        if (StringUtils.isBlank(code)) {
            throw new StoreSaasMarketingException("参数验证失败");
        }
        CouponItemResp couponItemResp = imCouponService.openGetCouponInfo(code);
        return new BizBaseResponse(couponItemResp);
    }

    /**
     * 对外 获取小程序核销二维码
     *
     * @param phone
     * @param encryptedCode
     * @return
     */
    @GetMapping(value = "/open/openGetCustomerCouponCodeByPhone", produces = MediaType.IMAGE_JPEG_VALUE)
    @ResponseBody
    public byte[] openGetCustomerCouponCodeByPhone(@RequestParam String phone, String encryptedCode) {
        if (StringUtils.isBlank(encryptedCode) || StringUtils.isBlank(phone)) {
            throw new StoreSaasMarketingException("参数验证失败");
        }
        byte[] codeStream = new byte[0];
        try {
            codeStream = imCouponService.openGetCustomerCouponCodeByPhone(phone, encryptedCode);
        } catch (Exception e) {
            log.info("获取二维码异常 -> e ->", e);
        }
        return codeStream;
    }


}
