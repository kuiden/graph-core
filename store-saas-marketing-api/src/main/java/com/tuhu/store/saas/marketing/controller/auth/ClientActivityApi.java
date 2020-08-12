package com.tuhu.store.saas.marketing.controller.auth;

import com.tuhu.boot.common.facade.BizBaseResponse;
import com.tuhu.store.saas.marketing.request.GetValidCodeReq;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @time 2020-08-10
 * @auther kudeng
 */
@RestController
@RequestMapping("/client/h5")
@Api(tags = "C端H5营销活动服务")
@Slf4j
public class ClientActivityApi {

    @PostMapping("/activity/detail")
    @ApiOperation("C端H5营销活动详情")
    public BizBaseResponse detail() {
        return BizBaseResponse.success();
    }

    @PostMapping("/getValidCode")
    @ApiOperation("营销服务获取验证码")
    public BizBaseResponse getValidCode(@Validated @RequestBody GetValidCodeReq req) {
        return BizBaseResponse.success();
    }

    @PostMapping("/activity/apply")
    @ApiOperation("C端H5营销活动报名")
    public BizBaseResponse apply() {
        return BizBaseResponse.success();
    }

}
