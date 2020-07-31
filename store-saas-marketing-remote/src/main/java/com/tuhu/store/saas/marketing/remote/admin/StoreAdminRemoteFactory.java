package com.tuhu.store.saas.marketing.remote.admin;

import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class StoreAdminRemoteFactory implements FallbackFactory<StoreAdminClient> {
    @Override
    public StoreAdminClient create(Throwable throwable) {
        return () -> {
            log.error("根据Token获取用户信息异常", throwable);
            return null;
        };
    }
}
