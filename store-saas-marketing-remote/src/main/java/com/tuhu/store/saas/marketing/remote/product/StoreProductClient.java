package com.tuhu.store.saas.marketing.remote.product;

import com.tuhu.boot.common.facade.BizBaseResponse;
import com.tuhu.store.saas.dto.product.IssuedDTO;
import com.tuhu.store.saas.marketing.remote.crm.CustomerRemoteFactory;
import com.tuhu.store.saas.vo.product.IssuedVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @time 2020-08-04
 * @auther kudeng
 */
@FeignClient(name = "${feign.application.store.saas.product.name:store-saas-product}", fallbackFactory = CustomerRemoteFactory.class)
public interface StoreProductClient {

    @PostMapping("/feign/product/IssuedSpu/issuedGoodOrServiceSpu")
    BizBaseResponse<IssuedDTO> issuedGoodOrServiceSpu(@RequestBody IssuedVO issuedVO);

}
