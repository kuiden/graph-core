package com.tuhu.store.saas.marketing.controller.mini;

import com.github.pagehelper.PageInfo;
import com.google.common.collect.Maps;
import com.tuhu.boot.common.facade.BizBaseResponse;
import com.tuhu.store.saas.marketing.controller.BaseApi;
import com.tuhu.store.saas.marketing.request.card.MiniQueryCardReq;
import com.tuhu.store.saas.marketing.request.card.QueryCardItemReq;
import com.tuhu.store.saas.marketing.response.card.CardResp;
import com.tuhu.store.saas.marketing.service.ICardService;
import com.tuhu.store.saas.marketing.service.ICouponService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
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

    @PostMapping("/query")
    @ApiOperation("查询客户次卡")
    public BizBaseResponse<List<CardResp>> query(@Validated @RequestBody MiniQueryCardReq req){
        String customerId = req.getCustomerId();
        if (StringUtils.isBlank(customerId)) {
            customerId = super.getUserId();
            req.setCustomerId(customerId);
        }
        req.setStoreId(super.getStoreId());
        req.setTenantId(super.getTenantId());
        return new BizBaseResponse(iCardService.queryCardRespList(req));
    }

    @PostMapping("/queryCardItem")
    @ApiOperation("查询次卡服务项目")
    public BizBaseResponse queryCardItem(@Validated @RequestBody QueryCardItemReq req){
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

    /**
     * 核销扫码
     *
     * @param code
     * @return
     */
    @RequestMapping(value = "/writeOff", method = RequestMethod.GET)
    public BizBaseResponse<Boolean> writeOff(@RequestParam String code) {
        Long storeId = super.getStoreId();
        Long tenanId = super.getTenantId();
        String userId = super.getUserId();
        Boolean result = Boolean.FALSE;
        if (code.startsWith("YHQ")) {
            couponService.writeOffCustomerCouponV2(code);
            result = Boolean.FALSE;
        } else if (code.startsWith("YXHD")) {

        }
        return new BizBaseResponse<>(result);
    }

}
