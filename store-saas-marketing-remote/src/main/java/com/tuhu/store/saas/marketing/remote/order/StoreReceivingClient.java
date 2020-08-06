package com.tuhu.store.saas.marketing.remote.order;

import com.tuhu.boot.common.facade.BizBaseResponse;
import com.tuhu.store.saas.order.vo.finance.receiving.AddReceivingVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

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

}
