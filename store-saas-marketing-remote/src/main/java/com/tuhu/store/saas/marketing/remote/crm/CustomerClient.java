package com.tuhu.store.saas.marketing.remote.crm;

import com.tuhu.boot.common.facade.BizBaseResponse;
import com.tuhu.store.saas.crm.dto.CustomerDTO;
import com.tuhu.store.saas.crm.vo.BaseIdReqVO;
import com.tuhu.store.saas.crm.vo.BaseIdsReqVO;
import com.tuhu.store.saas.crm.vo.CustomerSearchVO;
import com.tuhu.store.saas.crm.vo.CustomerVO;
import com.tuhu.store.saas.crm.vo.VehicleMaintenanceVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

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

}
