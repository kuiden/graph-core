package com.tuhu.store.saas.marketing.controller.mini;

import com.tuhu.boot.common.facade.BizBaseResponse;
import com.tuhu.store.saas.marketing.controller.BaseApi;
import com.tuhu.store.saas.marketing.request.valueCard.AddValueCardRuleReq;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author wangyuqing
 * @since 2020/10/17 17:17
 */
@Slf4j
@Api(tags = "H5-储值卡Api")
@RestController
@RequestMapping("/mini/valueCard")
public class MiniValueCardApi extends BaseApi {

    @ApiOperation("H5-新增储值规则")
    @PostMapping("/rule/add")
    BizBaseResponse addStoredCardRule(@RequestBody AddValueCardRuleReq req){

        return new BizBaseResponse();
    }

    @ApiOperation("H5-查询储值规则")
    @PostMapping("/rule/query")
    BizBaseResponse queryStoredCardRule(){

        return new BizBaseResponse();
    }


}
