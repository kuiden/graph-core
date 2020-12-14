package com.tuhu.store.saas.marketing.controller.feign;

import com.alibaba.fastjson.JSONObject;
import com.mengfan.common.response.fianace.PaymentResponse;
import com.tuhu.boot.common.facade.BizBaseResponse;
import com.tuhu.springcloud.common.annotation.DistributedLock;
import com.tuhu.store.saas.marketing.enums.ShoppingPlatformEnum;
import com.tuhu.store.saas.marketing.exception.StoreSaasMarketingException;
import com.tuhu.store.saas.marketing.request.seckill.SeckillRecordAddReq;
import com.tuhu.store.saas.marketing.service.seckill.SeckillRegistrationRecordService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.Objects;


/**
 * @author wangxiang2
 */
@Slf4j
@RestController
@RequestMapping("/feign/seckill/registrationRecord")
@Api(tags = "C端秒杀活动报名对外API")
public class SeckillRegistrationRecordApi {


    @Autowired
    private SeckillRegistrationRecordService seckillRegistrationRecordService;


    @PostMapping("/orderAdd")
    @ApiOperation("H5、创建秒杀订单")
    @DistributedLock(timeout = 5, key = "#req.seckillActivityId + #req.buyerPhoneNumber")
    public BizBaseResponse<Map<String, Object>> customerActivityOrderAdd(@Validated @RequestBody SeckillRecordAddReq req) {
        //创建活动订单、待收单
        if (Objects.isNull(req.getStoreId()) || Objects.isNull(req.getTenantId())) {
            throw new StoreSaasMarketingException("请求参数异常！");
        }
        Map<String, Object> mapResult = seckillRegistrationRecordService.customerActivityOrderAdd(req, ShoppingPlatformEnum.H5);
        return new BizBaseResponse(mapResult);
    }


    /**
     * 秒杀（抢购）回调接口
     *
     * @param paymentResponse
     * @return
     */
    @PostMapping("/callback")
    @ApiOperation(value = "秒杀（抢购）回调接口", notes = "callback")
    public BizBaseResponse callback(@RequestBody PaymentResponse paymentResponse) {
        log.info("/feign/seckill/registrationRecord/callback,paymentResponse={}", JSONObject.toJSONString(paymentResponse));
        //异步调用
        seckillRegistrationRecordService.callBack(paymentResponse);
        return new BizBaseResponse();
    }

}

