package com.tuhu.store.saas.marketing.remote.storeuser;

import com.alibaba.fastjson.JSONObject;
import com.tuhu.boot.common.enums.BizErrorCodeEnum;
import com.tuhu.boot.common.exceptions.BizException;
import com.tuhu.boot.common.facade.BizBaseResponse;
import com.tuhu.store.saas.marketing.remote.reponse.StoreInfoDTO;
import com.tuhu.store.saas.marketing.remote.reponse.UserDTO;
import com.tuhu.store.saas.marketing.remote.request.StoreInfoVO;
import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class StoreUserRemoteFactory implements FallbackFactory<StoreUserClient> {
    @Override
    public StoreUserClient create(Throwable throwable) {
        return new StoreUserClient() {

            @Override
            public BizBaseResponse<UserDTO> getStoreUserInfoBySysUserId(Long sysUserId) {
                log.error("getCustomerIdsByCarInfo error,request={},error={}", sysUserId, ExceptionUtils.getStackTrace(throwable));
                throw new BizException(BizErrorCodeEnum.CALLSERVICCE_ERROR, throwable.getMessage(), throwable);
            }

            @Override
            public BizBaseResponse<UserDTO> getUserInfoById(String userId) {
                log.error("getUserInfoById error,request={},error={}", userId, ExceptionUtils.getStackTrace(throwable));
                throw new BizException(BizErrorCodeEnum.CALLSERVICCE_ERROR, throwable.getMessage(), throwable);
            }

            @Override
            public BizBaseResponse<Map<String, UserDTO>> getUserInfoMapByIdList(List<String> list) {
                log.error("getUserInfoMapByIdList error,request={},error={}", JSONObject.toJSONString(list), ExceptionUtils.getStackTrace(throwable));
                throw new BizException(BizErrorCodeEnum.CALLSERVICCE_ERROR, throwable.getMessage(), throwable);
            }

            @Override
            public BizBaseResponse<StoreInfoDTO> getStoreInfo(StoreInfoVO storeInfoVO) {
                log.error("getStoreInfo error,request={},error={}", JSONObject.toJSONString(storeInfoVO), ExceptionUtils.getStackTrace(throwable));
                throw new BizException(BizErrorCodeEnum.CALLSERVICCE_ERROR, throwable.getMessage(), throwable);
            }

        };
    }
}
