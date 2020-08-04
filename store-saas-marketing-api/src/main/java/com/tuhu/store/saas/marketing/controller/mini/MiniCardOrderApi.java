package com.tuhu.store.saas.marketing.controller.mini;

import com.tuhu.boot.common.facade.BizBaseResponse;
import com.tuhu.store.saas.marketing.request.card.AddCardOrderReq;
import com.tuhu.store.saas.marketing.service.ICardOrderService;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author wangyuqing
 * @since 2020/8/4 16:34
 */

@Slf4j
@ApiOperation("开卡单API")
@RestController
@RequestMapping("/mini/cardOrder")
public class MiniCardOrderApi {

    @Autowired
    private ICardOrderService iCardOrderService;

    @PostMapping("/add")
    @ApiOperation("新建开卡单")
    public BizBaseResponse add(@RequestBody AddCardOrderReq req){

        return new BizBaseResponse();
    }



}
