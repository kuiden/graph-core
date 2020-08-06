package com.tuhu.store.saas.marketing.remote.auth;


import com.tuhu.boot.common.facade.BizBaseResponse;
import com.tuhu.store.saas.marketing.remote.EndUser;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(name = "${feign.application.store.saas.auth.name}")
public interface StoreAuthClient {

    @PostMapping("/feign/auth/login/getUserByToken")
    BizBaseResponse<EndUser> getUserByToken();
}
