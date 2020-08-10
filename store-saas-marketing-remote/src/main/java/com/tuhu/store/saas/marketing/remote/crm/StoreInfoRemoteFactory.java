package com.tuhu.store.saas.marketing.remote.crm;

import com.tuhu.boot.common.enums.BizErrorCodeEnum;
import com.tuhu.boot.common.exceptions.BizException;
import com.tuhu.boot.common.facade.BizBaseResponse;
import com.tuhu.store.saas.marketing.remote.product.StoreProductClient;
import com.tuhu.store.saas.user.dto.ClientStoreDTO;
import com.tuhu.store.saas.user.dto.StoreDTO;
import com.tuhu.store.saas.user.vo.ClientStoreVO;
import com.tuhu.store.saas.user.vo.StoreInfoVO;
import com.tuhu.store.saas.vo.product.IssuedVO;
import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * @time 2020-08-04
 * @auther kudeng
 */
@Component
@Slf4j
public class StoreInfoRemoteFactory implements FallbackFactory<StoreInfoClient> {

    @Override
    public StoreInfoClient create(Throwable throwable) {
        return new StoreInfoClient() {
            @Override
            public BizBaseResponse<StoreDTO> getStoreInfo(@RequestBody StoreInfoVO storeInfoVO) {
                log.error("getStoreInfo error,request={},error={}", storeInfoVO, ExceptionUtils.getStackTrace(throwable));
                throw new BizException(BizErrorCodeEnum.CALLSERVICCE_ERROR, throwable.getMessage(), throwable);
            }

            @Override
            public BizBaseResponse<ClientStoreDTO> getStoreInfoForClient(ClientStoreVO clientStoreVO) {
                log.error("getStoreInfoForClient error,request={},error={}", clientStoreVO, ExceptionUtils.getStackTrace(throwable));
                throw new BizException(BizErrorCodeEnum.CALLSERVICCE_ERROR, throwable.getMessage(), throwable);
            }
        };
    }

}
