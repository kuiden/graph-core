package com.tuhu.store.saas.marketing.remote.order;

import com.tuhu.boot.common.facade.BizBaseResponse;
import com.tuhu.store.saas.marketing.remote.reponse.CardUseRecordDTO;
import com.tuhu.store.saas.order.request.serviceorder.ListCustomerInfoReq;
import com.tuhu.store.saas.order.response.serviceorder.ListCustomerInfoResp;
import com.tuhu.store.saas.order.vo.finance.receiving.AddReceivingVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * @author wangxiang2
 */
@FeignClient(name = "${feign.application.orderServer.name}", fallbackFactory = TradeOrderRemoteFactory.class)
public interface TradeOrderClient {

    /**
     * 根据秒杀活动创建交易单
     *
     * @param addReceivingVO
     * @return
     */
    @PostMapping("/feign/finance/trade/addTradeOrderBySeckillActivity")
    BizBaseResponse<String> addTradeOrderBySeckillActivity(@RequestBody AddReceivingVO addReceivingVO);

}
