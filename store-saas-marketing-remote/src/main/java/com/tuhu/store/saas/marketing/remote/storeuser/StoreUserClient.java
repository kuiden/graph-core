package com.tuhu.store.saas.marketing.remote.storeuser;


import com.tuhu.boot.common.facade.BizBaseResponse;
import com.tuhu.store.saas.marketing.remote.reponse.CustomerDTO;
import com.tuhu.store.saas.marketing.remote.reponse.StoreInfoDTO;
import com.tuhu.store.saas.marketing.remote.reponse.UserDTO;
import com.tuhu.store.saas.marketing.remote.request.AddVehicleReq;
import com.tuhu.store.saas.marketing.remote.request.BaseIdReqVO;
import com.tuhu.store.saas.marketing.remote.request.StoreInfoVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

@FeignClient(name = "${feign.application.crmServer.name}", fallbackFactory = StoreUserRemoteFactory.class)
public interface StoreUserClient {
    /**
     * 根据组织机构用户ID获取门店用户信息
     *
     * @param sysUserId
     * @return
     */
    @GetMapping("/feign/user/storeInfo/getStoreUserInfoBySysUserId")
    BizBaseResponse<UserDTO> getStoreUserInfoBySysUserId(@RequestParam("sysUserId") Long sysUserId);

    /**
     * 根据门店用户ID获取门店用户信息
     *
     * @param userId
     * @return
     */
    @GetMapping("/feign/user/storeInfo/getUserInfoById")
    BizBaseResponse<UserDTO> getUserInfoById(@RequestParam("userId") String userId);

    /**
     * 根据用户ids查询用户
     */
    @PostMapping("/feign/user/storeInfo/getUserInfoMapByIdList")
    BizBaseResponse<Map<String, UserDTO>> getUserInfoMapByIdList(@RequestBody List<String> list);

    @PostMapping("/feign/user/storeInfo/getStoreInfo")
    BizBaseResponse<StoreInfoDTO> getStoreInfo(@RequestBody StoreInfoVO storeInfoVO);

    @PostMapping(value = "/feign/crm/Customer/addCustomerForReservation")
    BizBaseResponse<AddVehicleReq> addCustomerForReservation(@RequestBody AddVehicleReq addVehicleReq);

    @PostMapping(value = "/feign/crm/Customer/getCustomerById")
    BizBaseResponse<CustomerDTO> getCustomerById(@RequestBody BaseIdReqVO baseIdReqVO);

}
