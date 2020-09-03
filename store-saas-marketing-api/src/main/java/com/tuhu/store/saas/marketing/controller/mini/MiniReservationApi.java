package com.tuhu.store.saas.marketing.controller.mini;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageInfo;
import com.tuhu.boot.common.facade.BizBaseResponse;
import com.tuhu.store.saas.marketing.constant.MiniNotifyConstant;
import com.tuhu.store.saas.marketing.enums.MarketingBizErrorCodeEnum;
import com.tuhu.store.saas.marketing.enums.SrvReservationChannelEnum;
import com.tuhu.store.saas.marketing.exception.StoreSaasMarketingException;
import com.tuhu.store.saas.marketing.request.CReservationListReq;
import com.tuhu.store.saas.marketing.request.CancelReservationReq;
import com.tuhu.store.saas.marketing.request.NewReservationReq;
import com.tuhu.store.saas.marketing.request.ReservePeriodReq;
import com.tuhu.store.saas.marketing.response.ReservationPeriodResp;
import com.tuhu.store.saas.marketing.response.dto.ReservationDTO;
import com.tuhu.store.saas.marketing.service.INewReservationService;
import com.tuhu.store.saas.marketing.service.ISMSService;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @Author: yanglanqing
 * @Date: 2020/8/3 16:26
 */
@RestController
@RequestMapping("/client/reservation")
@Slf4j
public class MiniReservationApi extends EndUserApi {

    @Autowired
    INewReservationService iNewReservationService;

    @Autowired
    ISMSService ismsService;

    @PostMapping(value = "/periodListForC")
    @ApiOperation(value = "C端预约时间段list")
    public BizBaseResponse getReservePeriodListForC(@RequestBody ReservePeriodReq req) {
        BizBaseResponse<List<ReservationPeriodResp>> result = BizBaseResponse.success();
        if(req.getStoreId() == null){
            return new BizBaseResponse<>(MarketingBizErrorCodeEnum.PARAM_ERROR, "门店ID不能为空");
        }
        if(req.getDate() == null){
            return new BizBaseResponse<>(MarketingBizErrorCodeEnum.PARAM_ERROR, "日期不能为空");
        }
        req.setCustomerId(this.getCustomerId());
        result.setData(iNewReservationService.getReservationPeroidList(req));
        return result;
    }

    @PostMapping(value = "/newForC")
    @ApiOperation(value = "C端新增预约单")
    public BizBaseResponse<String> newForC(@RequestBody NewReservationReq req){
        log.info("C端新增预约单入参：", JSONObject.toJSONString(req));
        BizBaseResponse<String> result = BizBaseResponse.success();
        validParam(req);
        if(req.getStoreId() == null){
            throw new StoreSaasMarketingException("门店ID不能为空");
        }
        if(StringUtils.isBlank(req.getCustomerPhoneNumber())){
            return new BizBaseResponse<>(MarketingBizErrorCodeEnum.PARAM_ERROR, "请输入您的手机号码");
        }
        if(StringUtils.isBlank(req.getCustomerId())){
            return new BizBaseResponse<>(MarketingBizErrorCodeEnum.PARAM_ERROR, "客户ID不能为空");
        }
        if(StringUtils.isBlank(req.getSourceChannel())){
            return new BizBaseResponse<>(MarketingBizErrorCodeEnum.PARAM_ERROR, "预约渠道不能为空");
        }
        req.setTeminal(MiniNotifyConstant.MINI_HOME);
        Integer teminalType = MiniNotifyConstant.MINI_HOME;
        if(SrvReservationChannelEnum.COUPON.getEnumCode().equals(req.getSourceChannel()) ||
                SrvReservationChannelEnum.ACTIVITY.getEnumCode().equals(req.getSourceChannel())){
            teminalType = MiniNotifyConstant.MINI_MARKETING;
        }
        result.setData(iNewReservationService.addReservation(req,teminalType));
        return result;
    }

    @PostMapping(value = "/updateForC")
    @ApiOperation(value = "车主小程序端修改预约单")
    public BizBaseResponse<Boolean> updateForC(@RequestBody NewReservationReq req){
        log.info("车主小程序端修改预约单入参：", JSONObject.toJSONString(req));
        BizBaseResponse<Boolean> result = BizBaseResponse.success();
        validParam(req);
        if(StringUtils.isBlank(req.getId())){
            return new BizBaseResponse<>(MarketingBizErrorCodeEnum.PARAM_ERROR, "预约单id不能为空");
        }
        if(StringUtils.isBlank(req.getCustomerPhoneNumber())){
            return new BizBaseResponse<>(MarketingBizErrorCodeEnum.PARAM_ERROR, "客户手机号不能为空");
        }
        if(StringUtils.isBlank(req.getCustomerId())){
            return new BizBaseResponse<>(MarketingBizErrorCodeEnum.PARAM_ERROR, "客户ID不能为空");
        }
        req.setTeminal(2);
        result.setData(iNewReservationService.updateReservation(req));
        return result;
    }

    @PostMapping(value = "/getCReservationList")
    @ApiOperation(value = "获取车主小程序预约列表")
    public BizBaseResponse getCReservationList(@RequestBody CReservationListReq req){
        BizBaseResponse<PageInfo<ReservationDTO>> result = BizBaseResponse.success();
        req.setCustomerId(this.getCustomerId());
        if(req.getStoreId() == null){
            throw new StoreSaasMarketingException("门店ID不能为空");
        }
        result.setData(iNewReservationService.getCReservationList(req));
        return result;
    }

    @PostMapping(value = "/getCReservationDetail")
    @ApiOperation(value = "获取车主小程序预约单详情")
    public BizBaseResponse<ReservationDTO> getCReservationDetail(@RequestBody CReservationListReq req){
        BizBaseResponse<ReservationDTO> result = BizBaseResponse.success();
        if(req.getId() == null){
            throw new StoreSaasMarketingException("预约单ID不能为空");
        }
        req.setStoreId(this.getStoreId());
        result.setData(iNewReservationService.getCReservationDetail(req));
        return result;
    }


    @PostMapping(value = "/cancelReservationForC")
    @ApiOperation(value = "C端取消预约")
    public BizBaseResponse cancelReservationForC(@RequestBody CancelReservationReq req){
        if(StringUtils.isBlank(req.getId())){
            return new BizBaseResponse<>(MarketingBizErrorCodeEnum.PARAM_ERROR, "预约单id不能为空");
        }
        req.setStoreId(this.getStoreId());
        req.setTeminal(2);
        iNewReservationService.cancelReservation(req);
        return new BizBaseResponse("取消成功");
    }


    //新增预约单公共校验
    private void validParam(NewReservationReq req){
        req.setTenantId(this.getTenantId());
        req.setUserId(this.getUserId());
        req.setCustomerId(this.getCustomerId());
        if(req.getEstimatedArriveTime() == null){
            throw new StoreSaasMarketingException("请选择到店时间");
        }
    }
}
