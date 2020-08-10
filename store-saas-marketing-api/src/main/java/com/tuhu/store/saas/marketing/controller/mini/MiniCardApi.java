package com.tuhu.store.saas.marketing.controller.mini;

import com.tuhu.boot.common.facade.BizBaseResponse;
import com.tuhu.store.saas.marketing.controller.BaseApi;
import com.tuhu.store.saas.marketing.request.card.MiniQueryCardReq;
import com.tuhu.store.saas.marketing.service.ICardService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author wangyuqing
 * @since 2020/8/7 17:35
 */

@Slf4j
@Api("次卡API")
@RestController
@RequestMapping("/mini/card")
public class MiniCardApi extends BaseApi {

    @Autowired
    private ICardService iCardService;

    @PostMapping("/list")
    @ApiOperation("客户次卡列表")
    public BizBaseResponse list(@Validated @RequestBody MiniQueryCardReq req){


        return new BizBaseResponse();
    }






}
