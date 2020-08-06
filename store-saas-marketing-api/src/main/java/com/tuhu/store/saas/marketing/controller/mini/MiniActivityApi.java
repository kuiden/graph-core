package com.tuhu.store.saas.marketing.controller.mini;

import com.github.pagehelper.PageInfo;
import com.tuhu.boot.common.facade.BizBaseResponse;
//import com.tuhu.saas.auth.annotatioin.AllowAnonymous;
//import com.tuhu.saas.crm.bo.response.resp.CommonResp;
//import com.tuhu.saas.crm.po.Activity;
//import com.tuhu.saas.crm.rpc.dto.ActivityCustomerDTO;
//import com.tuhu.saas.crm.rpc.vo.ServiceOrderActivityUseVO;
import com.tuhu.store.saas.marketing.controller.BaseApi;
import com.tuhu.store.saas.marketing.request.*;
import com.tuhu.store.saas.marketing.response.ActivityCustomerResp;
import com.tuhu.store.saas.marketing.response.ActivityResp;
import com.tuhu.store.saas.marketing.response.QrCodeResp;
import com.tuhu.store.saas.marketing.service.IActivityService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
        addActivityReq = iActivityService.addNewActivity(addActivityReq);
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
        activityChangeStatusReq = iActivityService.changeActivityStatus(activityChangeStatusReq);
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
        editActivityReq = iActivityService.editActivity(editActivityReq);
        return BizBaseResponse.success(editActivityReq);
    }


    @PostMapping(value = "/scanForWriteOff")
    @ApiModelProperty(value = "客户核销信息查询")
    public BizBaseResponse<ActivityCustomerResp> scanForWriteOff(@Validated @RequestBody ActivityCustomerReq req){
        ActivityCustomerResp resp = iActivityService.getActivityCustomerDetail(req);
        return BizBaseResponse.success(resp);
    }


    @PostMapping(value = "/writeOffOrCancel")
    @ApiModelProperty(value = "活动核销或取消")
    public BizBaseResponse<String> writeOffOrCacel(@Validated @RequestBody ActivityWriteOffOrCancelReq req){

        return BizBaseResponse.success();
    }

//    @RequestMapping(value = "/client/apply", method = RequestMethod.POST)
//    @ApiOperation(value = "营销活动报名")
//    public BizBaseResponse apply(@Validated @RequestBody ActivityApplyReq activityApplyReq) {
//        activityApplyReq.setCustomerId(super.getCustomerId());
//        activityApplyReq.setStoreId(super.getStoreId());
//        activityApplyReq.setTenantId(super.getTenantId());
//        activityApplyReq.setTelephone(super.getClientUserCore().getPhone());
//        CommonResp<String> activityOrderCodeResp = iActivityService.applyActivity(activityApplyReq);
//        BizBaseResponse resp = BizBaseResponse.success();
//        if (null != activityOrderCodeResp) {
//            resp.setCode(activityOrderCodeResp.getCode());
//            resp.setMessage(activityOrderCodeResp.getMessage());
//            resp.setData(activityOrderCodeResp.getData());
//        }
//        return resp;
//    }

//    @RequestMapping(value = "/activityCustomerDetail", method = {RequestMethod.GET, RequestMethod.POST})
//    @ApiOperation(value = "客户报名营销活动详情")
//    public BizBaseResponse getActivityCustomerDetail(ActivityCustomerReq activityCustomerReq) {
//        if (StringUtils.isBlank(activityCustomerReq.getCustomerId())) {
//            activityCustomerReq.setIsFromClient(Boolean.TRUE);
//            activityCustomerReq.setCustomerId(super.getCustomerId());
//            activityCustomerReq.setStoreId(super.getStoreId());
//        }
//        ActivityCustomerResp activityCustomerResp = iActivityService.getActivityCustomerDetail(activityCustomerReq);
//        return BizBaseResponse.success(activityCustomerResp);
//    }

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
//    @RequestMapping(value = "/listActivityCustomer", method = {RequestMethod.GET, RequestMethod.POST})
//    @ApiOperation(value = "营销活动参与详情查询")
//    @AllowAnonymous
//    public ResultObject list(@Validated @RequestBody ActivityCustomerListReq activityCustomerListReq) {
//        if (null == activityCustomerListReq.getStoreId()) {
//            activityCustomerListReq.setStoreId(super.getStoreId());
//        } else {
//            activityCustomerListReq.setIsFromClient(Boolean.TRUE);
//        }
//        if (null == activityCustomerListReq.getTenantId()) {
//            activityCustomerListReq.setTenantId(super.getTenantId());
//        }
//        PageInfo<SimpleActivityCustomerResp> activityRespPageInfo = iActivityService.listActivityCustomer(activityCustomerListReq);
//        return new ResultObject(activityRespPageInfo);
//    }
//
//
//    @RequestMapping(value = "/client/list", method = RequestMethod.GET)
//    @ApiOperation(value = "营销活动列表")
//    public ResultObject activityList(Long storeId) {
//        if (storeId == null) {
//            return new ResultObject();
//        }
//        List<Activity> activityRespPageInfo = iActivityService.getActivityListByStoreId(storeId);
//
//        return new ResultObject(activityRespPageInfo);
//
//    }
//
//    @RequestMapping(value = "/client/detail", method = RequestMethod.GET)
//    @AllowAnonymous
//    @ApiOperation(value = "营销活动详情")
//    public ResultObject detailForClient(Long activityId, Long storeId) {
//        ActivityResp activityResp = iActivityService.getActivityDetailForClient(activityId, storeId, this.getCustomerId());
//        return new ResultObject(activityResp);
//    }
//
//    @RequestMapping(value = "/client/myActivityList", method = RequestMethod.GET)
//    @ApiOperation(value = "我的营销活动列表")
//    public ResultObject myActivityList(ActivityCustomerListRequest activityCustomerListRequest) {
//        activityCustomerListRequest.setCustomerId(this.getCustomerId());
//        ActivityCustomerPageResp resp = iActivityService.getMyActivityList(activityCustomerListRequest);
//        return new ResultObject(resp);
//    }

    @GetMapping("/getQrCode")
    @ApiOperation(value = "获取小程序码图片")
    public BizBaseResponse<QrCodeResp> getActivityQrUrl(@Validated ActivityQrCodeRequest req) {
        String url = iActivityService.getQrCodeForActivity(req);
        QrCodeResp resp = new QrCodeResp();
        resp.setUrl(url);
        return BizBaseResponse.success(resp);
    }

//    @RequestMapping(value = "/getActivityStatistics", method = {RequestMethod.GET, RequestMethod.POST})
//    @ApiOperation(value = "获取活动数据")
//    public ResultObject getActivityStatistics(Long activityId) {
//        Map<String, Object> activityStatistics = iActivityService.getActivityStatistics(activityId, super.getStoreId());
//        return new ResultObject(activityStatistics);
//    }
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
