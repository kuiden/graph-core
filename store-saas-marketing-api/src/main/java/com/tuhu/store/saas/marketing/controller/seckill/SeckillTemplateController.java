package com.tuhu.store.saas.marketing.controller.seckill;


import com.google.common.collect.Lists;
import com.tuhu.boot.common.facade.response.BizResponse;
import com.tuhu.store.saas.marketing.controller.BaseApi;
import com.tuhu.store.saas.marketing.request.seckill.AddSeckillTempReq;
import com.tuhu.store.saas.marketing.request.seckill.EditSecKillTempReq;
import com.tuhu.store.saas.marketing.request.seckill.QuerySeckillTempListReq;
import com.tuhu.store.saas.marketing.request.seckill.SortSeckillTempReq;
import com.tuhu.store.saas.marketing.response.seckill.SeckillTempDetailResp;
import com.tuhu.store.saas.marketing.service.seckill.SeckillTemplateService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

/**
 * <p>
 * 秒杀活动基础模板表 前端控制器
 * </p>
 *
 * @author yangshengyong
 * @since 2020-12-02
 */
@RestController
@RequestMapping("/seckill/template")
@Api(tags = "秒杀活动模板服务")
public class SeckillTemplateController extends BaseApi {

    @Autowired
    private SeckillTemplateService seckillTemplateService;

    @PostMapping("/list")
    @ApiOperation("查询秒杀活动模板列表")
    public BizResponse<List<SeckillTempDetailResp>> list(@RequestBody QuerySeckillTempListReq req) {
        List<SeckillTempDetailResp> resp = seckillTemplateService.getSeckillTempList(req, this.getTenantId());
        return BizResponse.success(resp);
    }

    @PostMapping("/add")
    @ApiOperation("添加秒杀活动模板")
    public BizResponse add(@Validated @RequestBody AddSeckillTempReq req) {
        seckillTemplateService.addSeckillTemplate(req, this.getTenantId(), this.getTenantUserId());
        return BizResponse.success();
    }

    @PostMapping("/edit")
    @ApiOperation("编辑秒杀活动模板")
    public BizResponse edit(@Validated @RequestBody EditSecKillTempReq req) {
        seckillTemplateService.editTemplate(req, this.getTenantId(), this.getTenantUserId());
        return BizResponse.success();
    }

    @PostMapping("/detail")
    @ApiOperation("查询秒杀活动模板详情")
    public BizResponse<SeckillTempDetailResp> detail(@RequestParam(value = "tempId") String tempId) {
        SeckillTempDetailResp resp = seckillTemplateService.getTemplateDetail(tempId, this.getTenantId());
        return BizResponse.success(resp);
    }

    @PostMapping("/sort")
    @ApiOperation("秒杀活动模板排序")
    public BizResponse sort(@Validated @RequestBody List<SortSeckillTempReq> req) {
        seckillTemplateService.updateTemplateSort(req, this.getTenantId(), this.getTenantUserId());
        return BizResponse.success();
    }

}

