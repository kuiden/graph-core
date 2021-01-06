package com.tuhu.store.saas.marketing.remote.order;

import com.tuhu.boot.common.enums.BizErrorCodeEnum;
import com.tuhu.boot.common.exceptions.BizException;
import com.tuhu.boot.common.facade.BizBaseResponse;
import com.tuhu.store.saas.order.dto.finance.receiving.TradeOrderDTO;
import com.tuhu.store.saas.order.vo.finance.receiving.AddReceivingVO;
import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.stereotype.Component;

/**
 * @author wangxiang2
 */
@Component
@Slf4j
public class TradeOrderRemoteFactory implements FallbackFactory<TradeOrderClient> {
    @Override
    public TradeOrderClient create(Throwable throwable) {
        return new TradeOrderClient() {

            @Override
            public BizBaseResponse<String> addTradeOrderBySeckillActivity(AddReceivingVO addReceivingVO) {
                log.error("addTradeOrderBySeckillActivity error,error={}", "", ExceptionUtils.getStackTrace(throwable));
                throw new BizException(BizErrorCodeEnum.CALLSERVICCE_ERROR, throwable.getMessage(), throwable);
            }

            @Override
            public BizBaseResponse<String> addReceivingAndTradeOrder(AddReceivingVO addReceivingVO) {
                log.error("addReceivingAndTradeOrder error,error={}", "", ExceptionUtils.getStackTrace(throwable));
                throw new BizException(BizErrorCodeEnum.CALLSERVICCE_ERROR, throwable.getMessage(), throwable);
            }

            @Override
            public BizBaseResponse<TradeOrderDTO> getTradeOrderById(String tradeOrderId) {
                log.error("getTradeOrderById error,error={}", "", ExceptionUtils.getStackTrace(throwable));
                throw new BizException(BizErrorCodeEnum.CALLSERVICCE_ERROR, throwable.getMessage(), throwable);
            }
        };
    }
}
