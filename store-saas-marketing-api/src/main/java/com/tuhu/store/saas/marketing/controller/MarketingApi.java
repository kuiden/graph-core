package com.tuhu.store.saas.marketing.controller;

import com.tuhu.boot.common.facade.BizBaseResponse;
import com.tuhu.store.saas.marketing.request.MarketingSmsReq;
import com.tuhu.store.saas.marketing.service.ICustomerMarketingService;
import com.tuhu.store.saas.marketing.service.IMarketingSendRecordService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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
@RequestMapping(value = "/crm/marketing")
@Api(value = "定向营销相关api")
@Slf4j
public class MarketingApi extends BaseApi {

    @Autowired
    private IMarketingSendRecordService marketingSendRecordService;
    @Autowired
    private ICustomerMarketingService  iCustomerMarketingService;

    @RequestMapping(value = "/getSmsPreview", method = RequestMethod.POST)
    @ApiOperation(value = "根据营销方式和资源id获取短信预览")
    public BizBaseResponse getSmsPreview(@Validated @RequestBody MarketingSmsReq req) {
        req.setStoreId(getStoreId());
        String templateContent = iCustomerMarketingService.getSmsPreview(req);
        return new BizBaseResponse(templateContent);
    }





}
