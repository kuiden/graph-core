package com.tuhu.store.saas.marketing.controller.mini;

import com.tuhu.boot.common.facade.BizBaseResponse;
import com.tuhu.store.saas.marketing.controller.BaseApi;
import com.tuhu.store.saas.marketing.request.card.AddCardOrderReq;
import com.tuhu.store.saas.marketing.service.ICardOrderService;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

/**
 * @author wangyuqing
 * @since 2020/8/4 16:34
 */

@Slf4j
@ApiOperation("开卡单API")
@RestController
@RequestMapping("/mini/cardOrder")
public class MiniCardOrderApi extends BaseApi {

    @Autowired
    private ICardOrderService iCardOrderService;

    @PostMapping("/add")
    @ApiOperation("新建开卡单")
    public BizBaseResponse add(@Validated @RequestBody AddCardOrderReq req){
        req.setStoreId(super.getStoreId());
        req.setTenantId(super.getTenantId());
        req.setCreateUser(super.getUserId());
        req.setCreateTime(new Date());
        req.setUpdateTime(new Date());
        req.setStoreNo(super.getStoreNo());
        return new BizBaseResponse(iCardOrderService.addCardOrder(req));
    }






}
