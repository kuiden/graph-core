package com.tuhu.store.saas.marketing.controller.feign;

import com.alibaba.fastjson.JSONObject;
import com.mengfan.common.response.fianace.PaymentResponse;
import com.tuhu.boot.common.facade.BizBaseResponse;
import com.tuhu.store.saas.marketing.service.seckill.SeckillRegistrationRecordService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


/**
 * @author wangxiang2
 */
@Slf4j
@RestController
@RequestMapping("/feign/seckill/registrationRecord")
@Api(tags = "C端秒杀活动对外API")
public class SeckillRegistrationRecordApi {


    @Autowired
    private SeckillRegistrationRecordService seckillRegistrationRecordService;


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

