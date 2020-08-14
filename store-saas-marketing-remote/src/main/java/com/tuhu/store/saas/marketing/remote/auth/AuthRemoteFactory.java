package com.tuhu.store.saas.marketing.remote.auth;

import com.tuhu.boot.common.enums.BizErrorCodeEnum;
import com.tuhu.boot.common.exceptions.BizException;
import com.tuhu.boot.common.facade.BizBaseResponse;
import com.tuhu.store.saas.marketing.remote.CustomerAuthDto;
import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

@Component
@Slf4j
public class AuthRemoteFactory implements FallbackFactory<AuthClient> {
    @Override
    public AuthClient create(Throwable throwable) {
        return new AuthClient() {
            @Override
            public BizBaseResponse<CustomerAuthDto> getUserByToken() {
                log.error("getUserByToken error,request={},error={}", ExceptionUtils.getStackTrace(throwable));
                throw new BizException(BizErrorCodeEnum.CALLSERVICCE_ERROR, throwable.getMessage(), throwable);
            }
//            @Override
//            public BizBaseResponse bindWechatEndUserByPhone(EndUserMarketingBindRequest endUserMarketingBindRequest, @RequestParam
//                    Map<String, String> parameters){
//                log.error("bindWechatEndUserByPhone error,request={},error={}", ExceptionUtils.getStackTrace(throwable));
//                throw new BizException(BizErrorCodeEnum.CALLSERVICCE_ERROR, throwable.getMessage(), throwable);
//            }


        };
    }
}
