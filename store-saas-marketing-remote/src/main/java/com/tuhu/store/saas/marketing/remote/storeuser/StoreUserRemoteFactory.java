package com.tuhu.store.saas.marketing.remote.storeuser;

import com.alibaba.fastjson.JSONObject;
import com.tuhu.boot.common.enums.BizErrorCodeEnum;
import com.tuhu.boot.common.exceptions.BizException;
import com.tuhu.boot.common.facade.BizBaseResponse;
import com.tuhu.store.saas.marketing.remote.reponse.CustomerDTO;
import com.tuhu.store.saas.marketing.remote.reponse.StoreInfoDTO;
import com.tuhu.store.saas.marketing.remote.reponse.UserDTO;
import com.tuhu.store.saas.marketing.remote.request.AddVehicleReq;
import com.tuhu.store.saas.marketing.remote.request.BaseIdReqVO;
import com.tuhu.store.saas.marketing.remote.request.EndUserVisitedStoreReq;
import com.tuhu.store.saas.marketing.remote.request.StoreInfoVO;
import com.tuhu.store.saas.user.vo.StoreSimpleInfoResp;
import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;

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
                log.error("getStoreUserInfoBySysUserId error,request={},error={}", sysUserId, ExceptionUtils.getStackTrace(throwable));
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

            @Override
            public BizBaseResponse<AddVehicleReq> addCustomerForReservation(AddVehicleReq addVehicleReq) {
                log.error("addCustomerForReservation error,request={},error={}", JSONObject.toJSONString(addVehicleReq), ExceptionUtils.getStackTrace(throwable));
                throw new BizException(BizErrorCodeEnum.CALLSERVICCE_ERROR, throwable.getMessage(), throwable);
            }

            @Override
            public BizBaseResponse<CustomerDTO> getCustomerById(BaseIdReqVO baseIdReqVO) {
                log.error("getCustomerById error,request={},error={}", JSONObject.toJSONString(baseIdReqVO), ExceptionUtils.getStackTrace(throwable));
                throw new BizException(BizErrorCodeEnum.CALLSERVICCE_ERROR, throwable.getMessage(), throwable);
            }

            @Override
            public BizBaseResponse<List<StoreSimpleInfoResp>> getHistoryStoreList(@RequestBody List<EndUserVisitedStoreReq> voList) {
                log.error("getHistoryStoreList error,request={},error={}", JSONObject.toJSONString(voList), ExceptionUtils.getStackTrace(throwable));
                throw new BizException(BizErrorCodeEnum.CALLSERVICCE_ERROR, throwable.getMessage(), throwable);
            }

        };
    }
}
