package com.tuhu.store.saas.marketing.controller.mini;

import com.github.pagehelper.PageInfo;
import com.google.common.collect.Maps;
import com.tuhu.boot.common.facade.BizBaseResponse;
import com.tuhu.store.saas.marketing.context.EndUserContextHolder;
import com.tuhu.store.saas.marketing.controller.BaseApi;
import com.tuhu.store.saas.marketing.request.ActivityCustomerReq;
import com.tuhu.store.saas.marketing.request.card.MiniQueryCardReq;
import com.tuhu.store.saas.marketing.request.card.QueryCardItemReq;
import com.tuhu.store.saas.marketing.response.card.CardResp;
import com.tuhu.store.saas.marketing.service.IActivityService;
import com.tuhu.store.saas.marketing.service.ICardService;
import com.tuhu.store.saas.marketing.service.ICouponService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.bouncycastle.cms.PasswordRecipientId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;

/**
 * @author wangyuqing
 * @since 2020/8/7 17:35
 */

@Slf4j
@Api("次卡API")
@RestController
@RequestMapping("/mini/card")
public class MiniCardApi extends BaseApi {

    @Autowired
    private ICardService iCardService;

    @PostMapping("/client/query")
    @ApiOperation("C端-查询客户次卡")
    public BizBaseResponse<List<CardResp>> clientQuery(@Validated @RequestBody MiniQueryCardReq req){
        req.setCustomerId(EndUserContextHolder.getCustomerId());
        req.setStoreId(EndUserContextHolder.getStoreId());
        req.setTenantId(EndUserContextHolder.getTenantId());
        return new BizBaseResponse(iCardService.queryCardRespList(req));
    }

    @PostMapping("/query")
    @ApiOperation("B端-查询客户次卡")
    public BizBaseResponse<List<CardResp>> query(@Validated @RequestBody MiniQueryCardReq req){
        req.setStoreId(super.getStoreId());
        req.setTenantId(super.getTenantId());
        return new BizBaseResponse(iCardService.queryCardRespList(req));
    }

    @PostMapping("/queryCardItem")
    @ApiOperation("查询次卡服务项目")
    public BizBaseResponse queryCardItem(@Validated @RequestBody QueryCardItemReq req) {
        req.setStoreId(super.getStoreId());
        req.setTenantId(super.getTenantId());
        return new BizBaseResponse(iCardService.queryCardItem(req));
    }

    @GetMapping("/consumptionHistory")
    @ApiOperation("次卡消费历史")
    public BizBaseResponse consumptionHistory(@RequestParam Long cardId) {
        return new BizBaseResponse(iCardService.consumptionHistory(cardId));
    }

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
            log.info("调用活动核销接口P{" + code);
            ActivityCustomerReq req = new ActivityCustomerReq();
            req.setActivityOrderCode(code);
            req.setUseStatus(Integer.valueOf(1));
            result = activityService.writeOffOrCancelActivityCustomer(req);
        }
        return new BizBaseResponse<>(result);
    }

}
