package com.tuhu.store.saas.marketing.controller.seckill;


import com.baomidou.mybatisplus.plugins.Page;
import com.tuhu.boot.common.facade.BizBaseResponse;
import com.tuhu.store.saas.marketing.controller.BaseApi;
import com.tuhu.store.saas.marketing.request.seckill.SeckillActivityReq;
import com.tuhu.store.saas.marketing.response.seckill.SeckillActivityResp;
import com.tuhu.store.saas.marketing.response.seckill.SeckillActivityStatisticsResp;
import com.tuhu.store.saas.marketing.response.seckill.SeckillRegistrationRecordResp;
import com.tuhu.store.saas.marketing.service.seckill.SeckillActivityService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * <p>
 * 秒杀活动表 前端控制器
 * </p>
 *
 * @author yangshengyong
 * @since 2020-12-02
 */
@Api(value = "SeckillActivityController", tags = "秒杀活动")
@RestController
@RequestMapping("/seckill/activity")
public class SeckillActivityController extends BaseApi {
    @Autowired
    private SeckillActivityService seckillActivityService;

    @PostMapping(value = "/pageList")
    @ApiOperation(value = "秒杀活动列表 status 1未开始、2进行中、9已下架")
    public BizBaseResponse<Page<SeckillActivityResp>> pageList(@RequestBody SeckillActivityReq req) {
        req.setStoreId(super.getStoreId());
        req.setTenantId(super.getTenantId());
        return new BizBaseResponse(seckillActivityService.pageList(req));
    }

    @PostMapping(value = "/dataStatistics")
    @ApiOperation(value = "活动数据-数据统计")
    public BizBaseResponse<SeckillActivityStatisticsResp> dataStatistics(@Param("activityId") String activityId) {
        return new BizBaseResponse(seckillActivityService.dataStatistics(activityId));
    }

    @PostMapping(value = "/pageList")
    @ApiOperation(value = "活动数据-已购客户、浏览未购买客户分页列表")
    public BizBaseResponse<Page<SeckillActivityResp>> pageBuyList(@RequestBody SeckillActivityReq req) {
        req.setStoreId(super.getStoreId());
        req.setTenantId(super.getTenantId());
        return new BizBaseResponse(seckillActivityService.pageBuyList(req));
    }


    @PostMapping(value = "/participateDetail")
    @ApiOperation(value = "参与详情")
    public BizBaseResponse<List<SeckillRegistrationRecordResp>> participateDetail(@Param("customersId") String customersId) {
        return new BizBaseResponse(seckillActivityService.participateDetail(customersId));
    }


    @PostMapping(value = "/onShelf")
    @ApiOperation(value = "编辑上架")
    public BizBaseResponse onShelf(@Validated @RequestBody SeckillActivityReq req) {
        return new BizBaseResponse();
    }

    @PostMapping(value = "/offShelf")
    @ApiOperation(value = "活动下架")
    public BizBaseResponse<Boolean> offShelf(@Param("activityId") String activityId) {
        return new BizBaseResponse(seckillActivityService.offShelf(activityId));
    }
    //TODO 活动海报
}

