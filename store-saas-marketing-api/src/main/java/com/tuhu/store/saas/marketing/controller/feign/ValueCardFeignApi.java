package com.tuhu.store.saas.marketing.controller.feign;

import com.tuhu.boot.common.facade.BizBaseResponse;
import com.tuhu.store.saas.marketing.dataobject.ValueCardChange;
import com.tuhu.store.saas.marketing.request.QueryCardToCommissionReq;
import com.tuhu.store.saas.marketing.request.card.ValueCardReq;
import com.tuhu.store.saas.marketing.request.valueCard.ConfirmReceiptReq;
import com.tuhu.store.saas.marketing.request.valueCard.CustomerValueCardDetailReq;
import com.tuhu.store.saas.marketing.request.valueCard.ValueCardConsumptionReq;
import com.tuhu.store.saas.marketing.service.IValueCardService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * @author wangyuqing
 * @since 2020/10/20 11:33
 */
@RestController
@RequestMapping("/feign/valueCard")
@Api(tags = "储值卡对外接口")
public class ValueCardFeignApi {

    @Autowired
    private IValueCardService iValueCardService;

    @ApiOperation("储值卡核销")
    @PostMapping("/consumption")
    public BizBaseResponse<Map<String,Long>> valueCardConsumption(@RequestBody ValueCardConsumptionReq req){
        return new BizBaseResponse(iValueCardService.customerConsumption(req));
    }


    @ApiOperation("获取储值卡余额")
    @PostMapping("/getAmount")
    public BizBaseResponse<Map<String,BigDecimal>> getValueCardAmount(@RequestBody CustomerValueCardDetailReq req){
        return new BizBaseResponse<>(iValueCardService.customerValueCardAmount(req));
    }

    @ApiOperation("储值变更单确认收款")
    @PostMapping("/confirmReceipt")
    public BizBaseResponse<Boolean> confirmReceipt(@RequestBody ConfirmReceiptReq req){
        return new BizBaseResponse(iValueCardService.confirmReceipt(req));
    }

    @ApiOperation("查询储值卡，开卡（第一次充值）")
    @PostMapping("/getFirstValueCardChangeList")
    public BizBaseResponse<List<ValueCardChange>> getFirstValueCardChangeList(@RequestBody QueryCardToCommissionReq req){
        return new BizBaseResponse(iValueCardService.getFirstValueCardChangeList(req));
    }

}
