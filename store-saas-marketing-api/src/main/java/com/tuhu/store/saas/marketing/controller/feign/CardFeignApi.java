package com.tuhu.store.saas.marketing.controller.feign;

import com.github.pagehelper.PageInfo;
import com.tuhu.boot.common.facade.BizBaseResponse;
import com.tuhu.boot.common.facade.response.BizResponse;
import com.tuhu.store.saas.marketing.dataobject.CrdCardOrder;
import com.tuhu.store.saas.marketing.exception.StoreSaasMarketingException;
import com.tuhu.store.saas.marketing.request.CustomerLastPurchaseRequest;
import com.tuhu.store.saas.marketing.request.QueryCardToCommissionReq;
import com.tuhu.store.saas.marketing.request.card.CardTemplateModel;
import com.tuhu.store.saas.marketing.request.card.CardTemplateReq;
import com.tuhu.store.saas.marketing.request.vo.UpdateCardVo;
import com.tuhu.store.saas.marketing.response.ComputeMarktingCustomerForReportResp;
import com.tuhu.store.saas.marketing.response.dto.CrdCardOrderExtendDTO;
import com.tuhu.store.saas.marketing.response.dto.CustomerMarketCountDTO;
import com.tuhu.store.saas.marketing.service.ICardOrderService;
import com.tuhu.store.saas.marketing.service.ICardService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author wangyuqing
 * @since 2020/8/7 11:53
 */
@RestController
@RequestMapping("/feign/card")
@Api(tags = "次卡对外接口")
public class CardFeignApi {

    @Autowired
    private ICardOrderService iCardOrderService;

    @Autowired
    private ICardService iCardService;

    @GetMapping("/updateCardPaymentStatus")
    @ApiOperation("更新卡支付状态")
    public BizBaseResponse updateCardPaymentStatus(@RequestParam String orderNo,
                                                   @RequestParam Long storeId,
                                                   @RequestParam Long tenantId,
                                                   @RequestParam Long amount) {
        iCardOrderService.updateCardPaymentStatus(orderNo, storeId, tenantId, amount);
        return new BizBaseResponse();
    }

    @PostMapping("/updateCardQuantity")
    @ApiOperation("更新次卡次数")
    public BizBaseResponse updateCardQuantity(@RequestBody UpdateCardVo updateCardVo) {
        return new BizBaseResponse(iCardService.updateCardQuantity(updateCardVo));
    }


    @GetMapping("/hasCardByCustomerId")
    @ApiOperation("客户是否绑定卡")
    public BizBaseResponse hasCardByCustomerId(@RequestParam String customerId, @RequestParam Long storeId,
                                               @RequestParam Long tenantId) {
        return new BizBaseResponse(iCardService.hasCardByCustomerId(customerId, storeId, tenantId));
    }

    @GetMapping("/ComputeMarketingCustomerForReport")
    @ApiOperation("计算优惠券/次卡/活动/消费客户生成报表数据")
    public BizBaseResponse<Map<String, List<ComputeMarktingCustomerForReportResp>>> ComputeMarktingCustomerForReport(@RequestParam Long storeId,
                                                                                                  @RequestParam Long tenantId) {
        if(storeId == null || storeId <=0 || tenantId== null || tenantId <=0){
            throw new StoreSaasMarketingException("参数验证失败");
        }
        Map<String, List<ComputeMarktingCustomerForReportResp>> result = iCardOrderService.ComputeMarktingCustomerForReport(storeId,tenantId);
        return new BizBaseResponse(result);
    }

    @GetMapping("/customerCouponAndCardCount/{customerId}")
    @ApiOperation("获取用户次卡与优惠券总数")
    public BizBaseResponse<CustomerMarketCountDTO> getCustomerMarketInfo(@PathVariable String customerId){
        CustomerMarketCountDTO customerMarketCountDTO = iCardService.queryCustomerMarketInfo(customerId);
        return new BizBaseResponse<>(customerMarketCountDTO);
    }

    @PostMapping(value = "/customerLastPurchaseTime")
    public BizBaseResponse<Map<String, Date>> customerLastPurchaseTime(@RequestBody CustomerLastPurchaseRequest request){
        Map<String, Date> stringDateMap = iCardOrderService.customerLastPurchaseTime(request);
        return new BizBaseResponse<>(stringDateMap);
    }

    @PostMapping(value = "/getCardTemplateList")
    public BizResponse<PageInfo<CardTemplateModel>> getCardTemplateList(@RequestBody CardTemplateReq req) {
        return new BizResponse(iCardService.getCardTemplatePageInfo(req));
    }


    @PostMapping(value = "/queryCardToCommission")
    @ApiOperation("统计次卡，计算员工提成")
    public BizBaseResponse<List<CrdCardOrderExtendDTO>> queryCardToCommission(@RequestBody QueryCardToCommissionReq request){
        List<CrdCardOrderExtendDTO> cardOrderList = iCardOrderService.queryCardToCommission(request);
        return new BizBaseResponse<>(cardOrderList);
    }
}
