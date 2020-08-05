package com.tuhu.store.saas.marketing.remote.crm;

import com.tuhu.boot.common.facade.BizBaseResponse;
import com.tuhu.store.saas.user.dto.StoreDTO;
import com.tuhu.store.saas.user.vo.StoreInfoVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @time 2020-08-04
 * @auther kudeng
 */
@FeignClient(name = "${feign.application.crmServer.name}", fallbackFactory = StoreInfoRemoteFactory.class)
public interface StoreInfoClient {

    @PostMapping("/feign/user/storeInfo/getStoreInfo")
    BizBaseResponse<StoreDTO> getStoreInfo(@RequestBody StoreInfoVO storeInfoVO);

}
