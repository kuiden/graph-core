package com.tuhu.store.saas.marketing.controller.auth;

import com.tuhu.boot.common.facade.BizBaseResponse;
import com.tuhu.store.saas.marketing.exception.MarketingException;
import com.tuhu.store.saas.marketing.request.GetValidCodeReq;
import com.tuhu.store.saas.marketing.response.ActivityResp;
import com.tuhu.store.saas.marketing.service.IActivityService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * @time 2020-08-10
 * @auther kudeng
 */
@RestController
@RequestMapping("/client/h5")
@Api(tags = "C端H5营销活动服务")
@Slf4j
public class ClientActivityApi {

    @Autowired
    private IActivityService activityService;

    @GetMapping("/activity/detail")
    @ApiOperation("C端H5营销活动详情")
    public BizBaseResponse detail(@RequestParam String encryptedCode) {
        ActivityResp resp = null;
        try {
            resp = activityService.getActivityDetailByEncryptedCode(encryptedCode);
            if(resp ==null) {
               return BizBaseResponse.operationFailed("查不到此活动，请检查编码！");
            }
        }catch (MarketingException e){
            log.error("活动详情请求失败, 入参{}",e.getMessage());
        }catch (Exception e){
            log.error("活动详情，服务异常");
        }
        return BizBaseResponse.success(resp);
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
