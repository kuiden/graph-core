package com.tuhu.store.saas.marketing.remote.order;

import com.tuhu.boot.common.facade.BizBaseResponse;
import com.tuhu.store.saas.marketing.remote.reponse.CardUseRecordDTO;
import com.tuhu.store.saas.order.request.serviceorder.ListCustomerInfoReq;
import com.tuhu.store.saas.order.response.serviceorder.ListCustomerInfoResp;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "${feign.application.orderServer.name}", fallbackFactory = ServiceOrderRemoteFactory.class)
public interface ServiceOrderClient {

    @PostMapping("/feign/order/list/customerInfos")
    BizBaseResponse<List<ListCustomerInfoResp>> listCustomerInfos(@RequestBody ListCustomerInfoReq request);

    @PostMapping("/feign/order/list/customerInfoForGoods")
    BizBaseResponse<List<ListCustomerInfoResp>> listCustomerInfoForGoods(@RequestBody ListCustomerInfoReq request);

    @GetMapping("/feign/card/getCardUseRecord")
    BizBaseResponse<List<CardUseRecordDTO>> getCardUseRecord(@RequestParam String cardId);



}
