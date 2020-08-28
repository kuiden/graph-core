package com.tuhu.store.saas.marketing.remote.order;

import com.tuhu.boot.common.enums.BizErrorCodeEnum;
import com.tuhu.boot.common.exceptions.BizException;
import com.tuhu.boot.common.facade.BizBaseResponse;
import com.tuhu.store.saas.marketing.remote.reponse.CardUseRecordDTO;
import com.tuhu.store.saas.order.request.serviceorder.ListCustomerInfoReq;
import com.tuhu.store.saas.order.response.serviceorder.ListCustomerInfoResp;
import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
public class ServiceOrderRemoteFactory implements FallbackFactory<ServiceOrderClient> {
    @Override
    public ServiceOrderClient create(Throwable throwable) {
        return new ServiceOrderClient() {
            @Override
            public BizBaseResponse listCustomerInfos(ListCustomerInfoReq request) {
                log.error("listCustomerInfos error,request={},error={}", request, ExceptionUtils.getStackTrace(throwable));
                throw new BizException(BizErrorCodeEnum.CALLSERVICCE_ERROR, throwable.getMessage(), throwable);
            }

            @Override
            public BizBaseResponse<List<ListCustomerInfoResp>> listCustomerInfoForGoods(ListCustomerInfoReq request) {
                log.error("listCustomerInfoForGoods error,request={},error={}", request, ExceptionUtils.getStackTrace(throwable));
                throw new BizException(BizErrorCodeEnum.CALLSERVICCE_ERROR, throwable.getMessage(), throwable);
            }

            @Override
            public BizBaseResponse<List<CardUseRecordDTO>> getCardUseRecord(String cardId) {
                log.error("getCardUseRecord error,request={},error={}", cardId, ExceptionUtils.getStackTrace(throwable));
                throw new BizException(BizErrorCodeEnum.CALLSERVICCE_ERROR, throwable.getMessage(), throwable);
            }
        };
    }
}
