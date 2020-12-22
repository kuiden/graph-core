package com.tuhu.store.saas.marketing.controller.seckill;


import com.tuhu.boot.common.facade.BizBaseResponse;
import com.tuhu.boot.common.facade.response.BizResponse;
import com.tuhu.store.saas.marketing.controller.BaseApi;
import com.tuhu.store.saas.marketing.exception.MarketingException;
import com.tuhu.store.saas.marketing.exception.StoreSaasMarketingException;
import com.tuhu.store.saas.marketing.request.seckill.SeckillClassificationModel;
import com.tuhu.store.saas.marketing.service.seckill.SeckillClassificationService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 秒杀活动分类表 前端控制器
 * </p>
 *
 * @author yangshengyong
 * @since 2020-12-02
 */
@RestController
@RequestMapping("/seckill/classification")
public class SeckillClassificationController extends BaseApi {

    @Autowired
    private SeckillClassificationService service;

    @RequestMapping(value = "/save", method = RequestMethod.POST)
    @ApiOperation(value = "保存活动分类")
    public BizBaseResponse<Integer> save(@RequestBody @Validated SeckillClassificationModel req) {
        if (req == null) {
            throw new StoreSaasMarketingException("参数验证失败");
        }
        if (!super.getUserCore().getSystemCode().equals(Integer.valueOf(2))){
            throw new StoreSaasMarketingException("越权访问");
        }
        req.setTenantId(super.getTenantId());
        req.setCreateUser(super.getTenantUserId());
        return new BizBaseResponse<>(service.save(req));
    }

    @RequestMapping(value = "/getList", method = RequestMethod.GET)
    @ApiOperation(value = "获取分类列表")
    public BizBaseResponse<List<SeckillClassificationModel>> getList() {
        return new BizBaseResponse<>(service.getList(super.getTenantId()));
    }

    @RequestMapping(value = "/del", method = RequestMethod.GET)
    @ApiOperation(value = "删除分类")
    public BizBaseResponse<Boolean> del(@RequestParam("id") Integer id) {
        return new BizBaseResponse<>(service.del(id, super.getTenantId()));
    }

    @RequestMapping(value = "/swapPriority", method = RequestMethod.GET)
    @ApiOperation(value = "较换排序")
    public BizBaseResponse<List<SeckillClassificationModel>> swapPriority(@RequestParam("fromId") Integer fromId, @RequestParam("toId") Integer toId) {
        return new BizBaseResponse<>(service.swapPriority(super.getTenantId(), fromId, toId));
    }
}

