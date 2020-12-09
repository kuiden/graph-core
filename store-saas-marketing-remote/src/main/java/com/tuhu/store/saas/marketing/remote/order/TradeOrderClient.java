package com.tuhu.store.saas.marketing.remote.order;

import com.tuhu.boot.common.facade.BizBaseResponse;
import com.tuhu.store.saas.order.dto.finance.receiving.TradeOrderDTO;
import com.tuhu.store.saas.order.vo.finance.receiving.AddReceivingVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;


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


    /**
     * 根据交易单获取交易单详情
     * @param tradeOrderId
     * @return
     */
    @GetMapping("/feign/finance/trade/getTradeOrderById")
    BizBaseResponse<TradeOrderDTO> getTradeOrderById(@RequestParam String tradeOrderId);
}
