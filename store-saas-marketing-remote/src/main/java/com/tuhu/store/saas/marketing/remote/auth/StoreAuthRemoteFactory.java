package com.tuhu.store.saas.marketing.remote.auth;

import com.tuhu.boot.common.facade.BizBaseResponse;
import com.tuhu.store.saas.marketing.remote.EndUser;
import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class StoreAuthRemoteFactory implements FallbackFactory<StoreAuthClient> {
    @Override
    public StoreAuthClient create(Throwable throwable) {
        return new StoreAuthClient() {
            @Override
            public BizBaseResponse<EndUser> getUserByToken() {
                return null;
            }
        };
    }
}
