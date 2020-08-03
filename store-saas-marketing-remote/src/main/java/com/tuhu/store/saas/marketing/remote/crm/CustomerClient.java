package com.tuhu.store.saas.marketing.remote.crm;

import com.tuhu.boot.common.facade.BizBaseResponse;
import com.tuhu.store.saas.crm.dto.CustomerDTO;
import com.tuhu.store.saas.crm.vo.BaseIdReqVO;
import com.tuhu.store.saas.crm.vo.BaseIdsReqVO;
import com.tuhu.store.saas.crm.vo.CustomerVO;
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

}
