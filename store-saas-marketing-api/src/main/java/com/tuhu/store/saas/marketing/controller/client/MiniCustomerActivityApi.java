package com.tuhu.store.saas.marketing.controller.client;

import com.tuhu.boot.common.facade.BizBaseResponse;
import com.tuhu.store.saas.marketing.controller.BaseApi;
import com.tuhu.store.saas.marketing.controller.mini.BaseEndUserApi;
import com.tuhu.store.saas.marketing.exception.MarketingException;
import com.tuhu.store.saas.marketing.po.Activity;
import com.tuhu.store.saas.marketing.request.*;
import com.tuhu.store.saas.marketing.response.ActivityCustomerPageResp;
import com.tuhu.store.saas.marketing.response.ActivityCustomerResp;
import com.tuhu.store.saas.marketing.response.ActivityResp;
import com.tuhu.store.saas.marketing.response.CommonResp;
import com.tuhu.store.saas.marketing.service.IActivityService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @time 2020-08-06
 * @auther kudeng
 */
@RestController
@RequestMapping("/client/activity")
@Api(tags = "C端营销活动服务")
@Slf4j
public class MiniCustomerActivityApi extends BaseEndUserApi {

    @Autowired
    private IActivityService iActivityService;

    @PostMapping(value = "/apply")
    @ApiOperation(value = "营销活动报名")
    public BizBaseResponse apply(@Validated @RequestBody ActivityApplyReq activityApplyReq) {
        activityApplyReq.setCustomerId(super.getCustomerId());
        activityApplyReq.setStoreId(super.getStoreId());
        activityApplyReq.setTenantId(super.getTenantId());
        activityApplyReq.setTelephone(super.getEndUser().getPhone());
        CommonResp<String> activityOrderCodeResp = iActivityService.applyActivity(activityApplyReq);
        BizBaseResponse resp = BizBaseResponse.success();
        if (null != activityOrderCodeResp) {
            resp.setCode(activityOrderCodeResp.getCode());
            resp.setMessage(activityOrderCodeResp.getMessage());
            resp.setData(activityOrderCodeResp.getData());
        }
        return resp;
    }

    @PostMapping(value = "/activityCustomerDetail")
    @ApiOperation(value = "客户报名营销活动详情")
    public BizBaseResponse getActivityCustomerDetail(@RequestBody ActivityCustomerReq activityCustomerReq) {
        if (StringUtils.isBlank(activityCustomerReq.getCustomerId())) {
            activityCustomerReq.setIsFromClient(Boolean.TRUE);
            activityCustomerReq.setCustomerId(super.getCustomerId());
            activityCustomerReq.setStoreId(super.getStoreId());
        }
        ActivityCustomerResp activityCustomerResp = null;
        try {
            activityCustomerResp = iActivityService.getActivityCustomerDetail(activityCustomerReq);
        } catch (MarketingException me) {
            return BizBaseResponse.operationFailed(me.getMessage());
        } catch (Exception e) {
            log.info("客户报名营销活动详情服务异常，入参：{}", activityCustomerReq, e);
            return BizBaseResponse.operationFailed("服务异常");
        }
        return BizBaseResponse.success(activityCustomerResp);
    }

//    @RequestMapping(value = "/writeOffOrCancel", method = {RequestMethod.GET, RequestMethod.POST})
//    @ApiOperation(value = "客户报名核销或取消订单")
//    public ResultObject writeOffOrCancelActivityCustomer(@RequestBody ActivityCustomerReq activityCustomerReq) {
//        activityCustomerReq.setStoreId(super.getStoreId());
//        activityCustomerReq.setTenantId(super.getTenantId());
//        activityCustomerReq.setUserId(super.getUserId());
//        ActivityCustomerResp activityCustomerResp = iActivityService.writeOffOrCancelActivityCustomer(activityCustomerReq);
//        return new ResultObject(activityCustomerResp);
//    }
//
    @PostMapping(value = "/listActivityCustomer")
    @ApiOperation(value = "营销活动参与详情查询")
    public BizBaseResponse list(@Validated @RequestBody ActivityCustomerListReq activityCustomerListReq) {
//        if (null == activityCustomerListReq.getStoreId()) {
//            activityCustomerListReq.setStoreId(super.getStoreId());
//        } else {
//            activityCustomerListReq.setIsFromClient(Boolean.TRUE);
//        }
//        if (null == activityCustomerListReq.getTenantId()) {
//            activityCustomerListReq.setTenantId(super.getTenantId());
//        }
//        PageInfo<SimpleActivityCustomerResp> activityRespPageInfo = iActivityService.listActivityCustomer(activityCustomerListReq);
//        return BizBaseResponse.success(activityRespPageInfo);
        return BizBaseResponse.success();
    }
//
    @PostMapping(value = "/list")
    @ApiOperation(value = "营销活动列表")
    public BizBaseResponse activityList(@Validated @RequestBody StoreIdRequest req) {
        List<Activity> activityRespPageInfo = null;
        try {
            activityRespPageInfo = iActivityService.getActivityListByStoreId(req.getStoreId());
        } catch (Exception e) {
            log.info("营销活动列表服务异常：入参：{}", req.getStoreId(), e);
            return BizBaseResponse.operationFailed("服务异常");
        }
        return BizBaseResponse.success(activityRespPageInfo);

    }

    @PostMapping(value = "/detail")
    @ApiOperation(value = "营销活动详情")
    public BizBaseResponse detailForClient(Long activityId, Long storeId) {
        ActivityResp activityResp = iActivityService.getActivityDetailForClient(activityId, storeId, this.getCustomerId());
        return BizBaseResponse.success(activityResp);
    }

    @PostMapping(value = "/myActivityList")
    @ApiOperation(value = "我的营销活动列表")
    public BizBaseResponse myActivityList(@RequestBody ActivityCustomerListRequest activityCustomerListRequest) {
        activityCustomerListRequest.setCustomerId(this.getCustomerId());
        ActivityCustomerPageResp resp = null;
        try {
            resp = iActivityService.getMyActivityList(activityCustomerListRequest);
        } catch (Exception e) {
            log.info("我的营销活动列表服务异常：入参：{}", activityCustomerListRequest, e);
            return BizBaseResponse.operationFailed("服务异常");
        }
        return BizBaseResponse.success(resp);
    }
//
//    @RequestMapping(value = "/useOrCancelActivityCustomer", method = {RequestMethod.GET, RequestMethod.POST})
//    @ApiOperation(value = "活动开单与取消开单")
//    public ResultObject useOrCancelActivityCustomer(@RequestBody ServiceOrderActivityUseVO serviceOrderActivityUseVO) {
//        serviceOrderActivityUseVO.setStoreId(String.valueOf(super.getStoreId()));
//        serviceOrderActivityUseVO.setCompanyId(super.getCompanyId());
//        serviceOrderActivityUseVO.setTenantId(String.valueOf(super.getTenantId()));
//        ActivityCustomerDTO activityCustomerDTO = iActivityService.useOrCancelActivityCustomerForOrder(serviceOrderActivityUseVO);
//        return new ResultObject(activityCustomerDTO);
//    }

}
