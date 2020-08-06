package com.tuhu.store.saas.marketing.remote.order;

import com.tuhu.boot.common.facade.BizBaseResponse;
import com.tuhu.store.saas.marketing.remote.crm.CustomerRemoteFactory;
import com.tuhu.store.saas.order.request.serviceorder.ListCustomerInfoReq;
import com.tuhu.store.saas.order.response.serviceorder.ListCustomerInfoResp;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(name = "${feign.application.orderServer.name}", fallbackFactory = ServiceOrderRemoteFactory.class)
public interface ServiceOrderClient {

    @PostMapping("/feign/order/list/customerInfos")
    public BizBaseResponse<List<ListCustomerInfoResp>> listCustomerInfos(@RequestBody ListCustomerInfoReq request);

    @PostMapping("/feign/order/list/customerInfoForGoods")
    public BizBaseResponse<List<ListCustomerInfoResp>> listCustomerInfoForGoods(@RequestBody ListCustomerInfoReq request);

}
