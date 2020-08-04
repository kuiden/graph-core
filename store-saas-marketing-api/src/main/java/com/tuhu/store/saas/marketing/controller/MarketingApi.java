package com.tuhu.store.saas.marketing.controller;

import com.github.pagehelper.PageInfo;
import com.tuhu.boot.common.enums.BizErrorCodeEnum;
import com.tuhu.boot.common.facade.BizBaseResponse;
import com.tuhu.store.saas.marketing.dataobject.CustomerMarketing;
import com.tuhu.store.saas.marketing.dataobject.MarketingSendRecord;
import com.tuhu.store.saas.marketing.request.MarketingAddReq;
import com.tuhu.store.saas.marketing.request.MarketingDetailsReq;
import com.tuhu.store.saas.marketing.request.MarketingReq;
import com.tuhu.store.saas.marketing.request.MarketingUpdateReq;
import com.tuhu.store.saas.marketing.service.ICustomerMarketingService;
import com.tuhu.store.saas.marketing.service.IMarketingSendRecordService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

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
    private IMarketingSendRecordService marketingSendRecordService;
    @Autowired
    private ICustomerMarketingService  iCustomerMarketingService;

    @RequestMapping(value = "/getMarketingSendRecord", method = RequestMethod.POST)
    @ApiOperation(value = "根据营销ID 获取营销发送记录")
    public BizBaseResponse getMarketingSendRecord(@Validated @RequestBody MarketingSendRecord req) {
        if (req == null || (StringUtils.isEmpty(req.getMarketingId()) && StringUtils.isEmpty(req.getPhoneNumber()))) {
            return new BizBaseResponse(BizErrorCodeEnum.PARAM_ERROR, "参数验证失败");
        }
        List<MarketingSendRecord> result = marketingSendRecordService.getMarketingSendRecord(req.getMarketingId(), req.getPhoneNumber(), null);
        return new BizBaseResponse(result);
    }

    /**
     * @param marketingReq
     * @return
     * @author ZhangXiao
     */
    @ApiOperation(value = "定向营销任务列表")
    @RequestMapping(value = "/list", method = RequestMethod.POST)
    public BizBaseResponse marketingList(@Validated @RequestBody MarketingReq marketingReq) {
        marketingReq.setTenantId(super.getTenantId());
        marketingReq.setStoreId(super.getStoreId());
        PageInfo<CustomerMarketing> marketingList = iCustomerMarketingService.customerMarketingList(marketingReq);
        BizBaseResponse resultObject = new BizBaseResponse(marketingList);
        return resultObject;
    }

    /**
     * @param addReq
     * @return
     * @author ZhangXiao
     */
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    @ApiOperation(value = "定向营销任务新增")
    public BizBaseResponse add(@Validated @RequestBody MarketingAddReq addReq) {
        addReq.setTenantId(super.getTenantId());
        addReq.setStoreId(super.getStoreId());
        MarketingAddReq marketingAddReq = iCustomerMarketingService.addMarketingCustomer(addReq);
        BizBaseResponse resultObject = new BizBaseResponse(marketingAddReq);
        return resultObject;
    }

    /**
     * @param addReq
     * @return
     * @author ZhangXiao
     */
    @RequestMapping(value = "/updateTaskType", method = RequestMethod.POST)
    @ApiOperation(value = "更新定向营销任务状态")
    public BizBaseResponse updateTaskType(@Validated @RequestBody MarketingUpdateReq addReq) {
        addReq.setUpdateUser(this.getUserId());
        addReq.setTenantId(super.getTenantId());
        addReq.setStoreId(super.getStoreId());
        iCustomerMarketingService.updateMarketingCustomerByTaskType(addReq);
        return new BizBaseResponse();
    }


    /**
     * @param req
     * @return
     * @author ZhangXiao
     */
    @RequestMapping(value = "/details", method = RequestMethod.POST)
    @ApiOperation(value = "定向营销任务详情及营销效果显示")
    public BizBaseResponse add(@Validated @RequestBody MarketingDetailsReq req) {
        req.setTenantId(super.getTenantId());
        req.setStoreId(super.getStoreId());
//        CustomerMarketingDetailsResp customerMarketingDetailsResp = iCustomerMarketingService.customerMarketingDetails(req);
//        return new BizBaseResponse(customerMarketingDetailsResp);
        return null;
    }

/*    @Autowired
    private ICustomerMarketingRpcService iCustomerMarketingRpcService;

    @GetMapping(value = "marketingRpc")
    @ApiOperation(value = "定向营销任务详情及营销效果显示")
    public BizBaseResponse marketingRpc() {
        iCustomerMarketingRpcService.sendMarketingCouponAndMessage();
        return new BizBaseResponse();
    }*/


}
