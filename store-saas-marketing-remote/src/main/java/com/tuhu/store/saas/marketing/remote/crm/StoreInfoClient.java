package com.tuhu.store.saas.marketing.remote.crm;

import com.tuhu.boot.common.facade.BizBaseResponse;
import com.tuhu.store.saas.crm.dto.StoreInfoRelatedDTO;
import com.tuhu.store.saas.user.dto.ClientStoreDTO;
import com.tuhu.store.saas.user.dto.StoreDTO;
import com.tuhu.store.saas.user.dto.UserDTO;
import com.tuhu.store.saas.user.vo.ClientStoreVO;
import com.tuhu.store.saas.user.vo.StoreInfoVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

/**
 * @time 2020-08-04
 * @auther kudeng
 */
@FeignClient(name = "${feign.application.crmServer.name}", fallbackFactory = StoreInfoRemoteFactory.class)
public interface StoreInfoClient {

    @PostMapping("/feign/user/storeInfo/getStoreInfo")
    BizBaseResponse<StoreDTO> getStoreInfo(@RequestBody StoreInfoVO storeInfoVO);


    @PostMapping("/feign/user/storeInfo/getStoreInfoForClient")
    BizBaseResponse<ClientStoreDTO> getStoreInfoForClient(@RequestBody ClientStoreVO clientStoreVO);

    @PostMapping("/feign/crm/storeInfoRelated/getRelatedInfoByStoreId")
    BizBaseResponse<StoreInfoRelatedDTO> getRelatedInfoByStoreId(@RequestParam("storeId") Long storeId);

    @PostMapping("/feign/user/storeInfo/getUserInfoMapByIdList")
    BizBaseResponse<Map<String, UserDTO>> getUserInfoMapByIdList(@RequestBody List<String> list);
}
