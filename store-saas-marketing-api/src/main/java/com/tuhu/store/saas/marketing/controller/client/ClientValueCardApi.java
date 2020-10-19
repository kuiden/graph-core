package com.tuhu.store.saas.marketing.controller.client;

import com.github.pagehelper.PageInfo;
import com.tuhu.boot.common.facade.BizBaseResponse;
import com.tuhu.store.saas.marketing.context.EndUserContextHolder;
import com.tuhu.store.saas.marketing.controller.BaseApi;
import com.tuhu.store.saas.marketing.remote.EndUser;
import com.tuhu.store.saas.marketing.request.BasePageReq;
import com.tuhu.store.saas.marketing.request.valueCard.ValueCardChangeRecordReq;
import com.tuhu.store.saas.marketing.response.valueCard.ValueCardChangeResp;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
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

    @ApiOperation("我的储值-余额")
    @GetMapping("/queryAmount")
    BizBaseResponse<BigDecimal> customerValueCardAmount(){
        //EndUser dto = EndUserContextHolder.getUser();
        return new BizBaseResponse();
    }

    @ApiOperation("我的储值-充值/消费记录列表")
    @PostMapping("/rechargeRecord")
    BizBaseResponse<PageInfo<ValueCardChangeResp>> rechargeRecord(@RequestBody ValueCardChangeRecordReq req){
        //EndUser dto = EndUserContextHolder.getUser();
        return new BizBaseResponse();
    }

}
