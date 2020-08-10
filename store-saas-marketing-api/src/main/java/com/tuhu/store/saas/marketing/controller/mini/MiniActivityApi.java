package com.tuhu.store.saas.marketing.controller.mini;

import com.github.pagehelper.PageInfo;
import com.tuhu.boot.common.facade.BizBaseResponse;
//import com.tuhu.saas.auth.annotatioin.AllowAnonymous;
//import com.tuhu.saas.crm.bo.response.resp.CommonResp;
//import com.tuhu.saas.crm.po.Activity;
//import com.tuhu.saas.crm.rpc.dto.ActivityCustomerDTO;
//import com.tuhu.saas.crm.rpc.vo.ServiceOrderActivityUseVO;
import com.tuhu.store.saas.marketing.controller.BaseApi;
import com.tuhu.store.saas.marketing.exception.MarketingException;
import com.tuhu.store.saas.marketing.po.Activity;
import com.tuhu.store.saas.marketing.request.*;
import com.tuhu.store.saas.marketing.response.ActivityResp;
import com.tuhu.store.saas.marketing.response.QrCodeResp;
import com.tuhu.store.saas.marketing.response.SimpleActivityCustomerResp;
import com.tuhu.store.saas.marketing.service.IActivityService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 营销活动相关Controller
 */
@RestController
@RequestMapping("/mini/activity")
@Api(tags = "B端营销活动服务")
@Slf4j
public class MiniActivityApi extends BaseApi {

    @Autowired
    private IActivityService iActivityService;

    @PostMapping(value = "/add")
    @ApiOperation(value = "营销活动新增")
    public BizBaseResponse<AddActivityReq> add(@Validated @RequestBody AddActivityReq addActivityReq) {
        addActivityReq.setCreateUser(super.getUserId());
        addActivityReq.setStoreId(super.getStoreId());
        addActivityReq.setTenantId(super.getTenantId());
        addActivityReq.setCompanyId(super.getCompanyId());
        try {
            addActivityReq = iActivityService.addNewActivity(addActivityReq);
        } catch (MarketingException me) {
            return BizBaseResponse.operationFailed(me.getMessage());
        } catch (Exception e) {
            log.info("营销活动新增服务异常，入参：{}", addActivityReq, e);
            return BizBaseResponse.operationFailed("服务异常");
        }
        return BizBaseResponse.success(addActivityReq);
    }

    @PostMapping(value = "/detail")
    @ApiOperation(value = "营销活动详情")
    public BizBaseResponse<ActivityResp> detail(@Validated @RequestBody ActivityDetailReq req) {
        ActivityResp activityResp = iActivityService.getActivityDetailById(req.getActivityId(), super.getStoreId());
        return BizBaseResponse.success(activityResp);
    }

    @PostMapping(value = "/changeStatus")
    @ApiOperation(value = "营销活动上下架")
    public BizBaseResponse<ActivityChangeStatusReq> changeStatus(@Validated @RequestBody ActivityChangeStatusReq activityChangeStatusReq) {
        activityChangeStatusReq.setStoreId(super.getStoreId());
        activityChangeStatusReq.setUserId(super.getUserId());
        try {
            activityChangeStatusReq = iActivityService.changeActivityStatus(activityChangeStatusReq);
        } catch (MarketingException me) {
            return BizBaseResponse.operationFailed(me.getMessage());
        } catch (Exception e) {
            log.info("营销活动上下架服务异常，入参：{}", activityChangeStatusReq, e);
            return BizBaseResponse.operationFailed("服务异常");
        }
        return BizBaseResponse.success(activityChangeStatusReq);
    }


    @PostMapping(value = "/list")
    @ApiOperation(value = "营销活动查询")
    public BizBaseResponse<PageInfo<ActivityResp>> list(@Validated @RequestBody ActivityListReq activityListReq) {
        activityListReq.setUserId(this.getUserId());
        activityListReq.setStoreId(this.getStoreId());
        activityListReq.setTenantId(this.getTenantId());
        PageInfo<ActivityResp> activityRespPageInfo = iActivityService.listActivity(activityListReq);
        return BizBaseResponse.success(activityRespPageInfo);
    }

    @PostMapping(value = "/edit")
    @ApiOperation(value = "营销活动编辑")
    public BizBaseResponse<EditActivityReq> edit(@Validated @RequestBody EditActivityReq editActivityReq) {
        editActivityReq.setUpdateUser(super.getUserId());
        editActivityReq.setStoreId(super.getStoreId());
        editActivityReq.setTenantId(super.getTenantId());
        editActivityReq.setCompanyId(super.getCompanyId());
        try {
            editActivityReq = iActivityService.editActivity(editActivityReq);
        } catch (MarketingException me) {
            return BizBaseResponse.operationFailed(me.getMessage());
        } catch (Exception e) {
            log.info("营销活动编辑服务异常，入参：{}", editActivityReq, e);
            return BizBaseResponse.operationFailed("服务异常");
        }
        return BizBaseResponse.success(editActivityReq);
    }

    @GetMapping("/getQrCode")
    @ApiOperation(value = "获取小程序码图片")
    public BizBaseResponse<QrCodeResp> getActivityQrUrl(@Validated ActivityQrCodeRequest req) {
        String url = iActivityService.getQrCodeForActivity(req);
        QrCodeResp resp = new QrCodeResp();
        resp.setUrl(url);
        return BizBaseResponse.success(resp);
    }

    @PostMapping(value = "/getActivityStatistics")
    @ApiOperation(value = "获取活动数据")
    public BizBaseResponse getActivityStatistics(@RequestBody ActivityDetailReq req) {
        Map<String, Object> activityStatistics = null;
        try {
            activityStatistics = iActivityService.getActivityStatistics(req.getActivityId(), super.getStoreId());
        } catch (MarketingException me) {
            return BizBaseResponse.operationFailed(me.getMessage());
        } catch (Exception e) {
            log.info("获取活动数据服务异常，入参：{}", req.getActivityId(), e);
            return BizBaseResponse.operationFailed("服务异常");
        }
        return new BizBaseResponse(activityStatistics);
    }

    @PostMapping(value = "/listActivityCustomer")
    @ApiOperation(value = "营销活动参与详情查询")
    public BizBaseResponse list(@Validated @RequestBody ActivityCustomerListReq activityCustomerListReq) {
        if (null == activityCustomerListReq.getStoreId()) {
            activityCustomerListReq.setStoreId(super.getStoreId());
        } else {
            activityCustomerListReq.setIsFromClient(Boolean.TRUE);
        }
        if (null == activityCustomerListReq.getTenantId()) {
            activityCustomerListReq.setTenantId(super.getTenantId());
        }
        PageInfo<SimpleActivityCustomerResp> activityRespPageInfo = iActivityService.listActivityCustomer(activityCustomerListReq);
        return BizBaseResponse.success(activityRespPageInfo);
    }
}
