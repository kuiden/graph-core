package com.tuhu.store.saas.marketing.remote.crm;

import com.tuhu.boot.common.enums.BizErrorCodeEnum;
import com.tuhu.boot.common.exceptions.BizException;
import com.tuhu.boot.common.facade.BizBaseResponse;
import com.tuhu.store.saas.crm.dto.StoreInfoRelatedDTO;
import com.tuhu.store.saas.marketing.remote.product.StoreProductClient;
import com.tuhu.store.saas.user.dto.ClientStoreDTO;
import com.tuhu.store.saas.user.dto.StoreDTO;
import com.tuhu.store.saas.user.dto.UserDTO;
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
import java.util.Map;

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

            @Override
            public BizBaseResponse<StoreInfoRelatedDTO> getRelatedInfoByStoreId(Long storeId) {
                log.error("getRelatedInfoByStoreId error,request={},error={}", storeId, org.apache.commons.lang.exception.ExceptionUtils.getStackTrace(throwable));
                throw new BizException(BizErrorCodeEnum.CALLSERVICCE_ERROR, throwable.getMessage(), throwable);
            }

            @Override
            public BizBaseResponse<Map<String, UserDTO>> getUserInfoMapByIdList(List<String> list) {
                log.error("getUserInfoMapByIdList error,request={},error={}", list, org.apache.commons.lang.exception.ExceptionUtils.getStackTrace(throwable));
                throw new BizException(BizErrorCodeEnum.CALLSERVICCE_ERROR, throwable.getMessage(), throwable);
            }
        };
    }

}
