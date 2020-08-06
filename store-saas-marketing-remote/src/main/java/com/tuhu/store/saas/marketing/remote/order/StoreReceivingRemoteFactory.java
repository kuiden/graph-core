package com.tuhu.store.saas.marketing.remote.order;

import com.tuhu.boot.common.enums.BizErrorCodeEnum;
import com.tuhu.boot.common.exceptions.BizException;
import com.tuhu.boot.common.facade.BizBaseResponse;
import com.tuhu.store.saas.order.vo.finance.receiving.AddReceivingVO;
import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.stereotype.Component;

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

        };
    }
}
