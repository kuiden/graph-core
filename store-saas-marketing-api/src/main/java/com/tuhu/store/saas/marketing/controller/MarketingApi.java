package com.tuhu.store.saas.marketing.controller;

import com.github.pagehelper.PageInfo;
import com.tuhu.boot.common.facade.BizBaseResponse;
import com.tuhu.springcloud.common.util.RedisUtils;
import com.tuhu.store.saas.marketing.dataobject.CustomerMarketing;
import com.tuhu.store.saas.marketing.exception.StoreSaasMarketingException;
import com.tuhu.store.saas.marketing.job.GenerateMarketingSMSJob;
import com.tuhu.store.saas.marketing.job.SendMarketingSMSJob;
import com.tuhu.store.saas.marketing.request.MarketingAddReq;
import com.tuhu.store.saas.marketing.request.MarketingReq;
import com.tuhu.store.saas.marketing.request.MarketingSmsReq;
import com.tuhu.store.saas.marketing.service.*;
import com.tuhu.store.saas.marketing.util.StoreRedisUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author: ZhangXiao
 * @Description: 定向营销模块
 * @Date: Created in 2019/5/23
 * @ProjectName: saas-crm
 * @Version: 1.0.0
 */
@RestController
@RequestMapping(value = "/marketing")
@Api(value = "定向营销相关api")
@Slf4j
public class MarketingApi extends BaseApi {

    @Autowired
    private IUtilityService iUtilityService;

    @Autowired
    private IMessageRemindService iMessageRemindService;

    @Autowired
    private GenerateMarketingSMSJob generateMarketingSMSJob;

    @Autowired
    private SendMarketingSMSJob sendMarketingSMSJob;
    @Autowired
    private ICustomerMarketingService  iCustomerMarketingService;

    @Autowired
    private IMessageQuantityService iMessageQuantityService;

    private static final String addMarketingKeyPrefix = "addMarketingKey";

    @Autowired
    private StringRedisTemplate redisTemplate;

    @RequestMapping(value = "/getSmsPreview", method = RequestMethod.POST)
    @ApiOperation(value = "根据营销方式和资源id获取短信预览")
    public BizBaseResponse getSmsPreview(@Validated @RequestBody MarketingSmsReq req) {
        req.setStoreId(getStoreId());
        String templateContent = iCustomerMarketingService.getSmsPreview(req);
        return new BizBaseResponse(templateContent);
    }


    @RequestMapping(value = "/customerMarketingList", method = RequestMethod.POST)
    @ApiOperation(value = "分页查询定向营销列表")
    public BizBaseResponse customerMarketingList(@Validated @RequestBody MarketingReq req) {
        req.setStoreId(getStoreId());
        req.setTenantId(getTenantId());
        PageInfo<CustomerMarketing> pageList = iCustomerMarketingService.customerMarketingList(req);
        return new BizBaseResponse(pageList);
    }

    @RequestMapping(value = "/addMarketingCustomer", method = RequestMethod.POST)
    @ApiOperation(value = "创建定向营销")
    public BizBaseResponse addMarketingCustomer(@Validated @RequestBody MarketingAddReq addReq) {
        addReq.setStoreId(getStoreId());
        addReq.setTenantId(getTenantId());
        log.info("addMarketingCustomer-> req-> {} {} {}", addReq);
        Boolean success = true;
        String addMarketingKey = addMarketingKeyPrefix + "" + addReq.getStoreId() + addReq.getTenantId();
        RedisUtils redisUtils = new RedisUtils(redisTemplate,"addMarketing");
        StoreRedisUtils storeRedisUtils = new StoreRedisUtils(redisUtils, redisTemplate);
        Object value = storeRedisUtils.tryLock(addMarketingKey, 1000, 1000);
        if (value != null) {
            try {
                success = iCustomerMarketingService.addMarketingCustomer(addReq);
            } catch(StoreSaasMarketingException e) {
                throw e;
            }finally {
                storeRedisUtils.releaseLock(addMarketingKey, value.toString());
            }
        }

        return new BizBaseResponse(success);
    }

    @RequestMapping(value = "/getLastMessageCount", method = RequestMethod.GET)
    @ApiOperation(value = "获取门店剩余的短信数量额度")
    public BizBaseResponse getLastMessageCount() {
        Long num = iMessageQuantityService.getStoreMessageQuantity(getTenantId(), getStoreId());
        return new BizBaseResponse(num);
    }


    @RequestMapping(value = "/test", method = RequestMethod.GET)
    @ApiOperation(value = "test")
    public BizBaseResponse test() {
        try {
            generateMarketingSMSJob.execute("");
        }catch (Exception e) {
            log.error("定时任务报错",e);
        }

        return new BizBaseResponse(true);
    }
    @RequestMapping(value = "/test2", method = RequestMethod.GET)
    @ApiOperation(value = "test2")
    public BizBaseResponse test2() {
        try {
            sendMarketingSMSJob.execute("");
        }catch (Exception e) {
            log.error("定时任务报错",e);
        }

        return new BizBaseResponse(true);
    }



}
