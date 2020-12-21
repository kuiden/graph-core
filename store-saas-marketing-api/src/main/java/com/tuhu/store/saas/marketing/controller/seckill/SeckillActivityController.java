package com.tuhu.store.saas.marketing.controller.seckill;


import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageInfo;
import com.tuhu.boot.common.facade.BizBasePageResponse;
import com.tuhu.boot.common.facade.BizBaseResponse;
import com.tuhu.boot.common.facade.response.BizResponse;
import com.tuhu.store.saas.marketing.controller.BaseApi;
import com.tuhu.store.saas.marketing.exception.StoreSaasMarketingException;
import com.tuhu.store.saas.marketing.request.seckill.SeckillActivityModel;
import com.tuhu.store.saas.marketing.request.seckill.SeckillActivityQrCodeReq;
import com.tuhu.store.saas.marketing.request.seckill.SeckillActivityReq;
import com.tuhu.store.saas.marketing.response.seckill.SeckillActivityResp;
import com.tuhu.store.saas.marketing.response.seckill.SeckillActivityStatisticsResp;
import com.tuhu.store.saas.marketing.response.seckill.SeckillRegistrationRecordResp;
import com.tuhu.store.saas.marketing.response.seckill.SeckillTempPicResp;
import com.tuhu.store.saas.marketing.service.seckill.SeckillActivityService;
import com.tuhu.store.saas.marketing.service.seckill.SeckillRegistrationRecordService;
import com.tuhu.store.saas.marketing.service.seckill.SeckillTemplateService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.bouncycastle.cms.PasswordRecipientId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

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

    @Autowired
    private SeckillRegistrationRecordService seckillRegistrationRecordService;

    @Autowired
    private SeckillTemplateService seckillTemplateService;

    @PostMapping(value = "/save")
    @ApiOperation(value = "保存活动")
    public BizBaseResponse<String> save(@RequestBody SeckillActivityModel req) {
        req.setStoreId(super.getStoreId());
        req.setTenantId(super.getTenantId());
        req.setUpdateUser(super.getUserId());
        return new BizBaseResponse(seckillActivityService.saveSeckillActivity(req));
    }

    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @PostMapping(value = "/setCache")
    @ApiOperation(value = "保持预览")
    public BizBaseResponse<String> setCache(@RequestBody SeckillActivityModel req) {
        String result = UUID.randomUUID().toString();
        if (req != null) {
            stringRedisTemplate.opsForValue().set(result, JSON.toJSONString(req));
        }
        return new BizBaseResponse<String>(result);
    }

    @GetMapping(value = "/getModelById")
    @ApiOperation(value = "获取活动详情")
    public BizBaseResponse<SeckillActivityModel> getModelById(@RequestParam String id) {
        if(StringUtils.isBlank(id)){
            throw  new StoreSaasMarketingException("参数验证失败");
        }
        return new BizBaseResponse(seckillActivityService.getSeckillActivityModelById(id, super.getStoreId()));
    }

    @PostMapping(value = "/pageList")
    @ApiOperation(value = "秒杀活动列表 status 0未开始、1进行中、9已下架 , 定向营销(未开始、进行中)-1")
    public BizBaseResponse<PageInfo<SeckillActivityResp>> pageList(@RequestBody SeckillActivityReq req) {
        req.setStoreId(super.getStoreId());
        req.setTenantId(super.getTenantId());
        return new BizBaseResponse(seckillActivityService.pageList(req));
    }

    @GetMapping(value = "/dataStatistics")
    @ApiOperation(value = "活动数据-数据统计")
    public BizBaseResponse<SeckillActivityStatisticsResp> dataStatistics(@RequestParam("seckillActivityId") String seckillActivityId) {
        return new BizBaseResponse(seckillRegistrationRecordService.dataStatistics(seckillActivityId));
    }

    @PostMapping(value = "/pageBuyOrBrowseList")
    @ApiOperation(value = "活动数据-已购客户、浏览未购买客户分页列表 状态 status 0已购客户、1浏览未购买")
    public BizBaseResponse<PageInfo<SeckillRegistrationRecordResp>> pageBuyOrBrowseList(@RequestBody SeckillActivityReq req) {
        req.setStoreId(super.getStoreId());
        req.setTenantId(super.getTenantId());
        return new BizBaseResponse(seckillActivityService.pageBuyOrBrowseList(req));
    }

    @PostMapping(value = "/pageBuyRecodeList")
    @ApiOperation(value = "参与记录、活动购买记录")
    public BizBaseResponse<PageInfo<SeckillRegistrationRecordResp>> pageBuyRecodeList(@RequestBody SeckillActivityReq req) {
        req.setStoreId(super.getStoreId());
        req.setTenantId(super.getTenantId());
        return new BizBaseResponse(seckillRegistrationRecordService.pageBuyRecodeList(req));
    }

    @GetMapping(value = "/participateDetail")
    @ApiOperation(value = "参与详情")
    public BizBaseResponse<List<SeckillRegistrationRecordResp>> participateDetail(@RequestParam("customersId") String customersId) {
        return new BizBaseResponse(seckillRegistrationRecordService.participateDetail(customersId));
    }

    @GetMapping(value = "/offShelf")
    @ApiOperation(value = "活动下架")
    public BizBaseResponse<Boolean> offShelf(@RequestParam("seckillActivityId") String seckillActivityId) {
        return new BizBaseResponse(seckillActivityService.offShelf(seckillActivityId));
    }

    @GetMapping(value = "/onShelf")
    @ApiOperation(value = "活动上架")
    public BizBaseResponse<SeckillActivityResp> onShelf(@RequestParam("seckillActivityId") String seckillActivityId){
        return new BizBaseResponse(seckillActivityService.onShelf(seckillActivityId));
    }

    @PostMapping(value = "/poster")
    @ApiOperation(value = "活动海报")
    public BizBaseResponse<SeckillActivityResp> poster(@Validated @RequestBody SeckillActivityQrCodeReq request){
        return new BizBaseResponse(seckillActivityService.poster(request));
    }

    @PostMapping(value = "/qrCodeUrl")
    @ApiOperation(value = "活动二维码url")
    public BizBaseResponse<String> qrCodeUrl(@Validated @RequestBody SeckillActivityQrCodeReq request){
        return new BizBaseResponse(seckillActivityService.qrCodeUrl(request));
    }

    @PostMapping(value = "/editOnShelf")
    @ApiOperation(value = "编辑上架")
    public BizBaseResponse editOnShelf(@Validated @RequestBody SeckillActivityModel req) {
        req.setStoreId(super.getStoreId());
        req.setTenantId(super.getTenantId());
        req.setUpdateUser(super.getUserId());
        req.setId(null);
        return new BizBaseResponse(seckillActivityService.saveSeckillActivity(req));
    }

    @PostMapping(value = "/getTemplatePicList")
    @ApiOperation(value = "获取秒杀活动模板头图")
    public BizResponse<List<SeckillTempPicResp>> getTemplatePicList() {
        List<SeckillTempPicResp> picRespList = seckillTemplateService.getTempPicUrlList(this.getTenantId());
        return BizResponse.success(picRespList);
    }
}

