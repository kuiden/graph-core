package com.tuhu.store.saas.marketing.controller.client;


import com.tuhu.boot.common.facade.BizBaseResponse;
import com.tuhu.store.saas.marketing.context.EndUserContextHolder;
import com.tuhu.store.saas.marketing.controller.BaseApi;
import com.tuhu.store.saas.marketing.exception.StoreSaasMarketingException;
import com.tuhu.store.saas.marketing.remote.EndUser;
import com.tuhu.store.saas.marketing.request.CouponReceiveRecordRequest;
import com.tuhu.store.saas.marketing.request.CouponRequest;
import com.tuhu.store.saas.marketing.request.CouponSearchRequest;
import com.tuhu.store.saas.marketing.response.CouponItemResp;
import com.tuhu.store.saas.marketing.response.CouponPageResp;
import com.tuhu.store.saas.marketing.response.CustomerCouponPageResp;
import com.tuhu.store.saas.marketing.service.IMCouponService;
import com.tuhu.store.saas.marketing.util.StoreRedisUtils;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 优惠券相关Controller
 */
@RestController
@RequestMapping("/mini/c/coupon")
@Slf4j
public class ClientCouponApi extends BaseApi {
    @Autowired
    private IMCouponService imCouponService;

    @Autowired
    private RedisTemplate redisTemplate;

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
        req.setStoreId(Long.valueOf(dto.getStoreId()));
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
        req.setStoreId(Long.valueOf(dto.getStoreId()));
        req.setTenantId(Long.valueOf(dto.getTenantId()));
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

    private String cacheKeyPre = "COUPON:";
    /**
     * 获取客户优惠券信息
     *
     * @return
     */
    @GetMapping("/open/openGetCouponInfo")
    public BizBaseResponse openGetCouponInfo(@RequestParam String code, HttpServletRequest request) {
        log.info("open获取客户优惠券信息开始 -> {} ",code);
        if (StringUtils.isBlank(code)){
            log.info("参数验证失败");
            return null;
        }
        CouponItemResp couponItemResp = null;
        String ip = getIpAddress(request);
        String cacheKey = cacheKeyPre.concat(StringUtils.isNotBlank(ip)?ip:"").concat(code);
        String key = cacheKey.concat("num");
        Long num = redisTemplate.opsForValue().increment(key,1L);
        if (num.equals(1L)){
            redisTemplate.expire(key,2,TimeUnit.SECONDS);
        }
        if (!redisTemplate.hasKey(cacheKey)){
            couponItemResp = imCouponService.openGetCouponInfo(code);
            redisTemplate.opsForValue().set(cacheKey,couponItemResp,2,TimeUnit.SECONDS);
        } else {
            if (num.equals(20L)){
                redisTemplate.expire(cacheKey,30,TimeUnit.SECONDS);
            }
            couponItemResp = (CouponItemResp)redisTemplate.opsForValue().get(cacheKey);
        }
        return new BizBaseResponse(couponItemResp);
    }

    /**
     * 对外 获取小程序核销二维码
     *
     * @param phone
     * @param code
     * @return
     */
    @GetMapping(value = "/open/openGetCustomerCouponCodeByPhone")
    @ResponseBody
    public byte[] openGetCustomerCouponCodeByPhone(@RequestParam String phone, String code) {
        if (StringUtils.isBlank(code) || StringUtils.isBlank(phone)) {
            throw new StoreSaasMarketingException("参数验证失败");
        }
        byte[] codeStream = null;
        try {
            codeStream = imCouponService.openGetCustomerCouponCodeByPhone(phone, code);
        } catch (Exception e) {
            log.info("获取二维码异常 -> e ->", e);
            throw new StoreSaasMarketingException("获取优惠券失败");
        }
        return codeStream;
    }

    public String getIpAddress(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (StringUtils.isNotEmpty(ip) && !"unKnown".equalsIgnoreCase(ip)) {
            //多次反向代理后会有多个ip值，第一个ip才是真实ip
            int index = ip.indexOf(",");
            if (index != -1) {
                return ip.substring(0, index);
            } else {
                return ip;
            }
        }
        ip = request.getHeader("X-Real-IP");
        if (StringUtils.isNotEmpty(ip) && !"unKnown".equalsIgnoreCase(ip)) {
            return ip;
        }
        return request.getRemoteAddr();
    }



}
