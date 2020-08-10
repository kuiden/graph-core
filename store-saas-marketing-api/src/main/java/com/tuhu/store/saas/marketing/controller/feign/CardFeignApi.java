package com.tuhu.store.saas.marketing.controller.feign;

import com.tuhu.boot.common.facade.BizBaseResponse;
import com.tuhu.store.saas.marketing.request.vo.UpdateCardVo;
import com.tuhu.store.saas.marketing.service.ICardOrderService;
import com.tuhu.store.saas.marketing.service.ICardService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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
                                                   @RequestParam Long amount){
        iCardOrderService.updateCardPaymentStatus(orderNo,storeId,tenantId,amount);
        return new BizBaseResponse();
    }

    @PostMapping("/updateCardQuantity")
    @ApiOperation("更新次卡次数")
    public BizBaseResponse updateCardQuantity(@RequestBody UpdateCardVo updateCardVo){
        return new BizBaseResponse(iCardService.updateCardQuantity(updateCardVo));
    }




}
