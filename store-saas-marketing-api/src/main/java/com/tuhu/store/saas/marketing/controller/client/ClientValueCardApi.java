package com.tuhu.store.saas.marketing.controller.client;

import com.github.pagehelper.PageInfo;
import com.tuhu.boot.common.facade.BizBaseResponse;
import com.tuhu.store.saas.marketing.context.EndUserContextHolder;
import com.tuhu.store.saas.marketing.controller.BaseApi;
import com.tuhu.store.saas.marketing.request.valueCard.CustomerValueCardDetailReq;
import com.tuhu.store.saas.marketing.request.valueCard.ValueCardChangeRecordReq;
import com.tuhu.store.saas.marketing.response.valueCard.ValueCardChangeResp;
import com.tuhu.store.saas.marketing.service.IValueCardService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

/**
 * @author wangyuqing
 * @since 2020/10/19 11:34
 */
@Api(tags = "小程序-储值卡Api")
@RestController
@RequestMapping("/client/valueCard")
@Slf4j
public class ClientValueCardApi extends BaseApi {

    @Autowired
    private IValueCardService iValueCardService;

    @ApiOperation("我的储值-余额")
    @GetMapping("/queryAmount")
    BizBaseResponse<BigDecimal> customerValueCardAmount(){
        CustomerValueCardDetailReq req = new CustomerValueCardDetailReq();
        req.setStoreId(EndUserContextHolder.getStoreId());
        req.setTenantId(EndUserContextHolder.getTenantId());
        req.setCustomerId(EndUserContextHolder.getUserId());
        return new BizBaseResponse(iValueCardService.customerValueCardAmount(req));
    }

    @ApiOperation("我的储值-充值/消费记录列表")
    @PostMapping("/rechargeRecord")
    BizBaseResponse<PageInfo<ValueCardChangeResp>> rechargeRecord(@RequestBody ValueCardChangeRecordReq req){
        req.setStoreId(EndUserContextHolder.getStoreId());
        req.setTenantId(EndUserContextHolder.getTenantId());
        req.setCustomerId(EndUserContextHolder.getUserId());
        return new BizBaseResponse(iValueCardService.rechargeRecord(req));
    }

}
