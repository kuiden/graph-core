package com.tuhu.store.saas.marketing.controller.pc;

import com.alibaba.fastjson.JSONObject;
import com.tuhu.boot.common.facade.BizBaseResponse;
import com.tuhu.store.saas.marketing.controller.BaseApi;
import com.tuhu.store.saas.marketing.enums.MarketingBizErrorCodeEnum;
import com.tuhu.store.saas.marketing.enums.SrvReservationChannelEnum;
import com.tuhu.store.saas.marketing.exception.StoreSaasMarketingException;
import com.tuhu.store.saas.marketing.request.*;
import com.tuhu.store.saas.marketing.response.BReservationListResp;
import com.tuhu.store.saas.marketing.response.ReservationDateResp;
import com.tuhu.store.saas.marketing.service.INewReservationService;
import com.tuhu.store.saas.marketing.service.ISMSService;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @Author: yanglanqing
 * @Date: 2020/8/3 16:26
 */
@RestController
@RequestMapping("/order/reservation")
@Slf4j
public class ReservationApi extends BaseApi {

    @Autowired
    INewReservationService iNewReservationService;

    @Autowired
    ISMSService ismsService;

    @PostMapping(value = "/newForB")
    @ApiOperation(value = "B端新增预约单")
    public BizBaseResponse<String> newForB(@RequestBody NewReservationReq req){
        BizBaseResponse<String> result = BizBaseResponse.success();
        req.setTenantId(super.getTenantId());
        req.setStoreId(super.getStoreId());
        req.setUserId(super.getUserId());
        log.info("B端新增预约单入参：", JSONObject.toJSONString(req));
        if(req.getEstimatedArriveTime() == null){
            throw new StoreSaasMarketingException("请选择到店时间");
        }
        if(StringUtils.isBlank(req.getCustomerId())){
            return new BizBaseResponse<>(MarketingBizErrorCodeEnum.PARAM_ERROR, "客户ID不能为空");
        }
        req.setTeminal(1);
        req.setSourceChannel(SrvReservationChannelEnum.MD.getEnumCode());
        result.setData(iNewReservationService.addReservation(req,0));
        return result;
    }

    @PostMapping(value = "/dateList")
    @ApiOperation(value = "获取门店预约日期")
    public BizBaseResponse<List<ReservationDateResp>> getReserveDateList(){
        BizBaseResponse<List<ReservationDateResp>> result = BizBaseResponse.success();
        result.setData(iNewReservationService.getReserveDateList(super.getStoreId()));
        return result;
    }

    @PostMapping(value = "/getBReservationList")
    @ApiOperation(value = "获取门店预约列表")
    public BizBaseResponse<List<BReservationListResp>> getBReservationList(@RequestBody BReservationListReq req){
        BizBaseResponse<List<BReservationListResp>> result= BizBaseResponse.success();
        if(req.getReservationDate() == null){
            return new BizBaseResponse<>(MarketingBizErrorCodeEnum.PARAM_ERROR, "预约日期不能为空");
        }
        req.setStoreId(super.getStoreId());
        result.setData(iNewReservationService.getBReservationList(req));
        return result;
    }

    @PostMapping(value = "/confirmReservation")
    @ApiOperation(value = "确认预约")
    public BizBaseResponse confirmReservation(@RequestBody CReservationListReq req){
        if(StringUtils.isBlank(req.getId())){
            return new BizBaseResponse<>(MarketingBizErrorCodeEnum.PARAM_ERROR, "预约单id不能为空");
        }
        req.setStoreId(super.getStoreId());
        iNewReservationService.confirmReservation(req);
        return new BizBaseResponse("确认预约成功");
    }

    @PostMapping(value = "/cancelReservation")
    @ApiOperation(value = "B端取消预约")
    public BizBaseResponse cancelReservation(@RequestBody CancelReservationReq req){
        if(StringUtils.isBlank(req.getId())){
            return new BizBaseResponse<>(MarketingBizErrorCodeEnum.PARAM_ERROR, "预约单id不能为空");
        }
        req.setTeminal(1);
        req.setStoreId(super.getStoreId());
        iNewReservationService.cancelReservation(req);
        return new BizBaseResponse("取消成功");
    }

}
