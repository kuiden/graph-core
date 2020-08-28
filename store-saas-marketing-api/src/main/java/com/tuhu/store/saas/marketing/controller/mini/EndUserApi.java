package com.tuhu.store.saas.marketing.controller.mini;

import com.google.common.collect.Maps;
import com.tuhu.boot.common.facade.BizBaseResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;

@RestController
@Slf4j
@ApiOperation("C端接口")
@RequestMapping("/endUser")
public class EndUserApi extends BaseEndUserApi {

    @RequestMapping(value = "/test", method = {RequestMethod.POST, RequestMethod.GET})
    @ResponseBody
    public BizBaseResponse findAllVisitedStoresByOpenIdCode() {
        HashMap map = Maps.newHashMap();
        map.put("storeId", this.getStoreId());
        map.put("tenantId", this.getTenantId());
        map.put("customerId", this.getCustomerId());
        return new BizBaseResponse(map);
    }


}
