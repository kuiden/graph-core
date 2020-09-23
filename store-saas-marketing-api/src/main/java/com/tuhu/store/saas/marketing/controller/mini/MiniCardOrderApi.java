package com.tuhu.store.saas.marketing.controller.mini;

import com.github.pagehelper.PageInfo;
import com.tuhu.boot.common.facade.BizBaseResponse;
import com.tuhu.store.saas.marketing.controller.BaseApi;
import com.tuhu.store.saas.marketing.request.card.AddCardOrderReq;
import com.tuhu.store.saas.marketing.request.card.CustomerCardOrderReq;
import com.tuhu.store.saas.marketing.request.card.ListCardOrderReq;
import com.tuhu.store.saas.marketing.request.card.QueryCardOrderReq;
import com.tuhu.store.saas.marketing.response.card.CardOrderResp;
import com.tuhu.store.saas.marketing.service.ICardOrderService;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

/**
 * @author wangyuqing
 * @since 2020/8/4 16:34
 */

@Slf4j
@ApiOperation("开卡单API")
@RestController
@RequestMapping("/mini/cardOrder")
public class MiniCardOrderApi extends BaseApi {

    @Autowired
    private ICardOrderService iCardOrderService;

    @PostMapping("/add")
    @ApiOperation("新建开卡单")
    public BizBaseResponse<String> add(@Validated @RequestBody AddCardOrderReq req) {
        req.setStoreId(super.getStoreId());
        req.setTenantId(super.getTenantId());
        req.setCreateUser(super.getUserId());
        req.setCreateTime(new Date());
        req.setUpdateTime(new Date());
        req.setStoreNo(super.getStoreNo());
        return new BizBaseResponse(iCardOrderService.addCardOrder(req));
    }

    @PostMapping("/list")
    @ApiOperation("开卡单列表")
    public BizBaseResponse<PageInfo<CardOrderResp>> list(@Validated @RequestBody ListCardOrderReq req) {
        req.setStoreId(super.getStoreId());
        req.setTenantId(super.getTenantId());
        return new BizBaseResponse(iCardOrderService.getCardOrderList(req));
    }

    @PostMapping("/query")
    @ApiOperation("查询开卡单")
    public BizBaseResponse query(@Validated @RequestBody QueryCardOrderReq req) {
        req.setStoreId(super.getStoreId());
        req.setTenantId(super.getTenantId());
        return new BizBaseResponse(iCardOrderService.queryCardOrder(req));
    }
}
