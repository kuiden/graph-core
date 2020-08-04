package com.tuhu.store.saas.marketing.remote.product;

import com.tuhu.boot.common.enums.BizErrorCodeEnum;
import com.tuhu.boot.common.exceptions.BizException;
import com.tuhu.boot.common.facade.BizBaseResponse;
import com.tuhu.store.saas.marketing.remote.crm.CustomerClient;
import com.tuhu.store.saas.vo.product.IssuedVO;
import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.stereotype.Component;

/**
 * @time 2020-08-04
 * @auther kudeng
 */
@Component
@Slf4j
public class StoreProductRemoteFactory implements FallbackFactory<StoreProductClient>{


    @Override
    public StoreProductClient create(Throwable throwable) {
        return new StoreProductClient() {
            @Override
            public BizBaseResponse issuedGoodOrServiceSpu(IssuedVO issuedVO) {
                log.error("issuedGoodOrServiceSpu error,request={},error={}", issuedVO, ExceptionUtils.getStackTrace(throwable));
                throw new BizException(BizErrorCodeEnum.CALLSERVICCE_ERROR, throwable.getMessage(), throwable);
            }
        };
    }
}
