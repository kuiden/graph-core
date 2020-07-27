package com.tuhu.store.saas.controller;

import com.tuhu.boot.common.facade.BizBaseResponse;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

/**
 * @ClassName ActivityApi
 * @Author fast
 * @Version 1.0
 * <p>
 * 营销版 - 活动相关api
 */
@RestController
@RequestMapping("/activity")
public class ActivityApi extends BaseApi {

    @RequestMapping(value = "/test", method = RequestMethod.GET)
    @ApiOperation(value = "测试接口")
    public BizBaseResponse test() {
        return new BizBaseResponse("success");
    }

}
