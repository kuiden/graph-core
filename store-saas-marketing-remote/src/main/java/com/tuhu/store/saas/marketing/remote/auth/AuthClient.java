package com.tuhu.store.saas.marketing.remote.auth;

import com.tuhu.boot.common.facade.BizBaseResponse;
import com.tuhu.store.saas.marketing.remote.CustomerAuthDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

@FeignClient(name = "${feign.application.saas.auth.name}", fallbackFactory = AuthRemoteFactory.class)
public interface AuthClient {

    @PostMapping("/feign/auth/login/getUserByToken")
    BizBaseResponse<CustomerAuthDto> getUserByToken();

//    @PostMapping("/bind/endUser/byPhone")
//    public BizBaseResponse bindWechatEndUserByPhone(EndUserMarketingBindRequest endUserMarketingBindRequest, @RequestParam
//            Map<String, String> parameters);

}
