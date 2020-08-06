package com.tuhu.store.saas.marketing.controller;

import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.tuhu.boot.common.facade.BizBaseResponse;
import com.tuhu.store.saas.marketing.dataobject.CustomerMarketing;
import com.tuhu.store.saas.marketing.parameter.SMSParameter;
import com.tuhu.store.saas.marketing.request.MarketingReq;
import com.tuhu.store.saas.marketing.request.MarketingSmsReq;
import com.tuhu.store.saas.marketing.service.ICustomerMarketingService;
import com.tuhu.store.saas.marketing.service.IMarketingSendRecordService;
import com.tuhu.store.saas.marketing.service.ISMSService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    private ISMSService ismsService;

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


    @RequestMapping(value = "/customerMarketingList", method = RequestMethod.POST)
    @ApiOperation(value = "分页查询定向营销列表")
    public BizBaseResponse customerMarketingList(@Validated @RequestBody MarketingReq req) {
        req.setStoreId(getStoreId());
        PageInfo<CustomerMarketing> pageList = iCustomerMarketingService.customerMarketingList(req);
        return new BizBaseResponse(pageList);
    }

    @GetMapping(value = "/test")
    @ApiOperation(value = "测试")
    public BizBaseResponse test() {
        SMSParameter parameter = new SMSParameter();
        List<String> datas = Lists.newArrayList();
        datas.add("1");
        datas.add("12");
        datas.add("123");
        datas.add("1234");
        parameter.setPhone("15623695619");
        parameter.setDatas(datas);
        parameter.setTemplateId("624492");
        ismsService.sendCommonSms(parameter);
        return new BizBaseResponse("");
    }



}
