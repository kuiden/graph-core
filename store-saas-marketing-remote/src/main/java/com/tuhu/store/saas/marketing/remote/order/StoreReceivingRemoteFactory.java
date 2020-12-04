package com.tuhu.store.saas.marketing.remote.order;

import com.tuhu.boot.common.enums.BizErrorCodeEnum;
import com.tuhu.boot.common.exceptions.BizException;
import com.tuhu.boot.common.facade.BizBaseResponse;
import com.tuhu.store.saas.order.dto.finance.receiving.ReceivingDTO;
import com.tuhu.store.saas.order.vo.finance.nonpayment.AddNonpaymentVO;
import com.tuhu.store.saas.order.vo.finance.receiving.AddReceivingVO;
import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
public class StoreReceivingRemoteFactory implements FallbackFactory<StoreReceivingClient> {
    @Override
    public StoreReceivingClient create(Throwable throwable) {
        return new StoreReceivingClient(){

            @Override
            public BizBaseResponse<Boolean> addReceiving(AddReceivingVO addReceivingVO) {
                log.error("addReceiving error,error={}", "", ExceptionUtils.getStackTrace(throwable));
                throw new BizException(BizErrorCodeEnum.CALLSERVICCE_ERROR, throwable.getMessage(), throwable);
            }

            @Override
            public BizBaseResponse<String> addNonpaymentForValueCard(AddNonpaymentVO addNonpaymentVO) {
                log.error("addNonpaymentForValueCard error,error={}", "", ExceptionUtils.getStackTrace(throwable));
                throw new BizException(BizErrorCodeEnum.CALLSERVICCE_ERROR, throwable.getMessage(), throwable);
            }

            @Override
            public BizBaseResponse<String> addReceivingForValueCard(AddReceivingVO addReceivingVO) {
                log.error("addReceivingForValueCard error,error={}", "", ExceptionUtils.getStackTrace(throwable));
                throw new BizException(BizErrorCodeEnum.CALLSERVICCE_ERROR, throwable.getMessage(), throwable);
            }

            @Override
            public BizBaseResponse<Boolean> updateInitReceivingListByOrderNos(List<String> orderNos) {
                log.error("updateInitReceivingListByOrderNos error,error={}", "", ExceptionUtils.getStackTrace(throwable));
                throw new BizException(BizErrorCodeEnum.CALLSERVICCE_ERROR, throwable.getMessage(), throwable);
            }

        };
    }
}
