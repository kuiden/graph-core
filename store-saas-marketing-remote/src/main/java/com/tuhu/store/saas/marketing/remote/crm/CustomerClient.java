package com.tuhu.store.saas.marketing.remote.crm;

import com.tuhu.boot.common.facade.BizBaseResponse;
import com.tuhu.store.saas.crm.dto.CustomerDTO;
import com.tuhu.store.saas.crm.vo.*;
import com.tuhu.store.saas.marketing.remote.request.AddVehicleReq;
import com.tuhu.store.saas.user.dto.UserDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

@FeignClient(name = "${feign.application.crmServer.name}", fallbackFactory = CustomerRemoteFactory.class)
public interface CustomerClient {

    @PostMapping("/feign/crm/Customer/getCustomer")
    public BizBaseResponse<List<CustomerDTO>> getCustomer(@RequestBody CustomerVO customerVO);

    @PostMapping("/feign/crm/Customer/getCustomerById")
    public BizBaseResponse<CustomerDTO> getCustomerById(@RequestBody BaseIdReqVO baseIdReqVO);

    @PostMapping("/feign/crm/Customer/getCustomerByIds")
    public BizBaseResponse<List<CustomerDTO>> getCustomerByIds(@RequestBody BaseIdsReqVO baseIdsReqVO);

    @PostMapping("/feign/crm/Customer/listCustomer")
    public BizBaseResponse<List<CustomerDTO>> listCustomer(@RequestBody CustomerVO customerVO);

    @PostMapping("/feign/crm/Customer/getCustomerByQuery")
    BizBaseResponse<List<CustomerDTO>> getCustomerByQuery(@RequestBody CustomerVO customerVO);

    @PostMapping("/feign/crm/Customer/getCustomerByVehicleMaintenance")
    BizBaseResponse<List<CustomerDTO>> getCustomerByVehicleMaintenance(@RequestBody VehicleMaintenanceVo vehicleMaintenanceVo);

    @PostMapping("/feign/crm/Customer/getCustomerListByIdList")
    BizBaseResponse<List<CustomerDTO>> getCustomerListByIdList(@RequestBody BaseIdsReqVO baseIdsReqVO);

    @PostMapping("/feign/crm/Customer/getCustomerListByPhoneOrName")
    BizBaseResponse<List<CustomerDTO>> getCustomerListByPhoneOrName(@RequestBody CustomerSearchVO customerSearchVO);

    @PostMapping("feign/user/storeInfo/getUserInfoMapByIdList")
    BizBaseResponse<Map<String, UserDTO>> getUserInfoMapByIdList(@RequestBody List<String> list);

    @PostMapping(value = "/feign/crm/Customer/addCustomerForOrder")
    BizBaseResponse<AddVehicleVO> addCustomerForOrder(AddVehicleReq addVehicleReq);

}
