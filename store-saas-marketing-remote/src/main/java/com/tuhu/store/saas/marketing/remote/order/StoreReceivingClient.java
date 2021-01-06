package com.tuhu.store.saas.marketing.remote.order;

import com.tuhu.boot.common.facade.BizBaseResponse;
import com.tuhu.store.saas.order.vo.finance.nonpayment.AddNonpaymentVO;
import com.tuhu.store.saas.order.vo.finance.receiving.AddReceivingVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = "${feign.application.orderServer.name}",fallbackFactory = StoreReceivingRemoteFactory.class)
public interface StoreReceivingClient {

    /**
     * 来源单入库
     *
     * @param addReceivingVO
     * @return
     */
    @RequestMapping(value = "/feign/finance/receiving/addReceiving", method = RequestMethod.POST)
    BizBaseResponse<Boolean> addReceiving(@RequestBody AddReceivingVO addReceivingVO);

    /**
     * 储值待付
     *
     * @param addNonpaymentVO
     * @return
     */
    @RequestMapping(value = "/feign/finance/nonpayment/addNonpaymentForValueCard", method = RequestMethod.POST)
    BizBaseResponse<String> addNonpaymentForValueCard(@RequestBody AddNonpaymentVO addNonpaymentVO);

    /**
     * 储值待收
     *
     * @param addReceivingVO
     * @return
     */
    @RequestMapping(value = "/feign/finance/receiving/addReceivingForValueCard", method = RequestMethod.POST)
    BizBaseResponse<String> addReceivingForValueCard(@RequestBody AddReceivingVO addReceivingVO);


    /**
     * 更新 根据来源单号查询待收单的收款状态 已取消
     *
     * @param orderNos
     * @return
     */
    @PostMapping(value = "/feign/finance/receiving/updateReceivingAndTradeOrderByOrderNos")
    BizBaseResponse updateReceivingAndTradeOrderByOrderNos(@RequestBody List<String> orderNos, @RequestParam("state") Integer state);

}
