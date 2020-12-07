package com.tuhu.store.saas.marketing.controller.seckill;


import com.baomidou.mybatisplus.plugins.Page;
import com.tuhu.boot.common.facade.BizBaseResponse;
import com.tuhu.store.saas.marketing.controller.BaseApi;
import com.tuhu.store.saas.marketing.request.seckill.SeckillActivityQrCodeReq;
import com.tuhu.store.saas.marketing.request.seckill.SeckillActivityReq;
import com.tuhu.store.saas.marketing.response.seckill.SeckillActivityResp;
import com.tuhu.store.saas.marketing.response.seckill.SeckillActivityStatisticsResp;
import com.tuhu.store.saas.marketing.response.seckill.SeckillRegistrationRecordResp;
import com.tuhu.store.saas.marketing.service.seckill.SeckillActivityService;
import com.tuhu.store.saas.marketing.service.seckill.SeckillRegistrationRecordService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

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
@RequestMapping("/mini/seckill/activity")
public class SeckillActivityController extends BaseApi {
    @Autowired
    private SeckillActivityService seckillActivityService;

    @Autowired
    private SeckillRegistrationRecordService seckillRegistrationRecordService;

    @PostMapping(value = "/pageList")
    @ApiOperation(value = "秒杀活动列表 status 0未开始、1进行中、9已下架 , 定向营销(未开始、进行中)-1")
    public BizBaseResponse<Page<SeckillActivityResp>> pageList(@RequestBody SeckillActivityReq req) {
        req.setStoreId(super.getStoreId());
        req.setTenantId(super.getTenantId());
        return new BizBaseResponse(seckillActivityService.pageList(req));
    }

    @GetMapping(value = "/dataStatistics")
    @ApiOperation(value = "活动数据-数据统计")
    public BizBaseResponse<SeckillActivityStatisticsResp> dataStatistics(@Param("seckillActivityId") String seckillActivityId) {
        return new BizBaseResponse(seckillRegistrationRecordService.dataStatistics(seckillActivityId));
    }

    @PostMapping(value = "/pageBuyOrBrowseList")
    @ApiOperation(value = "活动数据-已购客户、浏览未购买客户分页列表 状态 0已购客户、1浏览未购买")
    public BizBaseResponse<Page<SeckillActivityResp>> pageBuyOrBrowseList(@RequestBody SeckillActivityReq req) {
        req.setStoreId(super.getStoreId());
        req.setTenantId(super.getTenantId());
        return new BizBaseResponse(seckillActivityService.pageBuyOrBrowseList(req));
    }


    @GetMapping(value = "/participateDetail")
    @ApiOperation(value = "参与详情")
    public BizBaseResponse<List<SeckillRegistrationRecordResp>> participateDetail(@Param("customersId") String customersId) {
        return new BizBaseResponse(seckillRegistrationRecordService.participateDetail(customersId));
    }

    @GetMapping(value = "/offShelf")
    @ApiOperation(value = "活动下架")
    public BizBaseResponse<Boolean> offShelf(@Param("seckillActivityId") String seckillActivityId) {
        return new BizBaseResponse(seckillActivityService.offShelf(seckillActivityId));
    }

    @GetMapping(value = "/poster")
    @ApiOperation(value = "活动海报")
    public BizBaseResponse<SeckillActivityResp> poster(@Validated @RequestBody SeckillActivityQrCodeReq request){
        return new BizBaseResponse(seckillActivityService.poster(request));
    }

    @GetMapping(value = "/qrCodeUrl")
    @ApiOperation(value = "活动二维码url")
    public BizBaseResponse<String> qrCodeUrl(@Validated @RequestBody SeckillActivityQrCodeReq request){
        return new BizBaseResponse(seckillActivityService.qrCodeUrl(request));
    }

    @PostMapping(value = "/onShelf")
    @ApiOperation(value = "编辑上架")
    public BizBaseResponse onShelf(@Validated @RequestBody SeckillActivityReq req) {
        return new BizBaseResponse();
    }
}

