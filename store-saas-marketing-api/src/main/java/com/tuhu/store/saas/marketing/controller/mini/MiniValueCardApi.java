package com.tuhu.store.saas.marketing.controller.mini;

import com.github.pagehelper.PageInfo;
import com.tuhu.boot.common.facade.BizBaseResponse;
import com.tuhu.store.saas.marketing.controller.BaseApi;
import com.tuhu.store.saas.marketing.request.valueCard.*;
import com.tuhu.store.saas.marketing.response.valueCard.CustomerValueCardDetailResp;
import com.tuhu.store.saas.marketing.response.valueCard.QueryValueCardListResp;
import com.tuhu.store.saas.marketing.response.valueCard.QueryValueCardRuleResp;
import com.tuhu.store.saas.marketing.response.valueCard.ValueCardChangeResp;
import com.tuhu.store.saas.marketing.service.IValueCardService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Map;

/**
 * @author wangyuqing
 * @since 2020/10/17 17:17
 */
@Slf4j
@Api(tags = "H5-储值卡Api")
@RestController
@RequestMapping("/mini/valueCard")
public class MiniValueCardApi extends BaseApi {

    @Autowired
    private IValueCardService iValueCardService;

    @ApiOperation("H5-新增储值规则")
    @PostMapping("/rule/add")
    BizBaseResponse<AddValueCardRuleReq> addValueCardRule(@RequestBody AddValueCardRuleReq req){
        req.setStoreId(super.getStoreId());
        req.setTenantId(super.getTenantId());
        return new BizBaseResponse(iValueCardService.addValueCardRule(req));
    }

    @ApiOperation("H5-查询储值规则")
    @GetMapping("/rule/query")
    BizBaseResponse<QueryValueCardRuleResp> queryValueCardRule(){
        return new BizBaseResponse(iValueCardService.queryValueCardRule(super.getStoreId(),super.getTenantId()));
    }

    @ApiOperation("H5-门店会员储值总额")
    @GetMapping("/queryTotalValue")
    BizBaseResponse<Map<String, BigDecimal>> queryTotalValue(){
        return new BizBaseResponse(iValueCardService.queryTotalValue(super.getStoreId(),super.getTenantId()));
    }

    @ApiOperation("H5-储值明细列表")
    @PostMapping("/queryDetailList")
    BizBaseResponse<PageInfo<QueryValueCardListResp>> queryDetailList(@RequestBody QueryValueCardListReq req){
        req.setStoreId(super.getStoreId());
        req.setTenantId(super.getTenantId());
        return new BizBaseResponse(iValueCardService.queryDetailList(req));
    }

    @ApiOperation("H5-客户储值详情")
    @PostMapping("/customer/detail")
    BizBaseResponse<CustomerValueCardDetailResp> customerValueCardDetail(@RequestBody CustomerValueCardDetailReq req){
        req.setStoreId(super.getStoreId());
        req.setTenantId(super.getTenantId());
        return new BizBaseResponse(iValueCardService.customerValueCardDetail(req));
    }

    @ApiOperation("H5-客户储值详情-变更明细")
    @PostMapping("/customer/changeList")
    BizBaseResponse<PageInfo<ValueCardChangeResp>> customerValueCardChangeList(@RequestBody CustomerValueCardDetailReq req){
        req.setStoreId(super.getStoreId());
        req.setTenantId(super.getTenantId());
        return new BizBaseResponse(iValueCardService.customerValueCardChangeList(req));
    }

    @ApiOperation("H5-客户储值卡余额")
    @PostMapping("/customer/queryAmount")
    BizBaseResponse<Map<String,BigDecimal>> customerValueCardAmount(@RequestBody CustomerValueCardDetailReq req){
        req.setStoreId(super.getStoreId());
        req.setTenantId(super.getTenantId());
        return new BizBaseResponse(iValueCardService.customerValueCardAmount(req));
    }

    @ApiOperation("H5-客户储值卡充值、退款")
    @GetMapping("/rechargeOrRefund")
    BizBaseResponse<Boolean> customerRechargeOrRefund(@RequestBody ValueCardRechargeOrRefundReq req){

        return new BizBaseResponse();
    }

    @ApiOperation("H5-客户储值卡核销")
    @GetMapping("/consumption")
    BizBaseResponse<Boolean> customerConsumption(@RequestBody ValueCardConsumptionReq req){

        return new BizBaseResponse();
    }




}
