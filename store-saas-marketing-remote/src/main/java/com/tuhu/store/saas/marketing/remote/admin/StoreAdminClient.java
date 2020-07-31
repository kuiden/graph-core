package com.tuhu.store.saas.marketing.remote.admin;


import com.tuhu.boot.common.facade.BizBaseResponse;
import com.tuhu.store.saas.marketing.remote.CustomUser;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(name = "${feign.application.saas.base.user.name}")
public interface StoreAdminClient {

    @PostMapping("/login/getUserByToken")
    BizBaseResponse<CustomUser> getUserByToken();
}
