package com.tuhu.store.saas.marketing.controller.pc;

import com.github.pagehelper.PageInfo;
import com.tuhu.boot.common.facade.BizBaseResponse;
import com.tuhu.store.saas.marketing.controller.BaseApi;
import com.tuhu.store.saas.marketing.enums.MarketingBizErrorCodeEnum;
import com.tuhu.store.saas.marketing.enums.SrvReservationChannelEnum;
import com.tuhu.store.saas.marketing.request.*;
import com.tuhu.store.saas.marketing.response.BReservationListResp;
import com.tuhu.store.saas.marketing.response.ReservationDateResp;
import com.tuhu.store.saas.marketing.response.ReservationPeriodResp;
import com.tuhu.store.saas.marketing.response.dto.ReservationDTO;
import com.tuhu.store.saas.marketing.service.INewReservationService;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Date;
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

    @PostMapping(value = "/periodList")
    @ApiOperation(value = "预约时间段list")
    public BizBaseResponse getReservePeriodList(@RequestBody ReservePeriodReq req) {
        BizBaseResponse<List<ReservationPeriodResp>> result = BizBaseResponse.success();
        if(req.getStoreId() == null){
            return new BizBaseResponse<>(MarketingBizErrorCodeEnum.PARAM_ERROR, "门店ID不能为空");
        }
        if(req.getDate() == null){
            return new BizBaseResponse<>(MarketingBizErrorCodeEnum.PARAM_ERROR, "日期不能为空");
        }
        result.setData(iNewReservationService.getReservationPeroidList(req));
        return result;
    }

    @PostMapping(value = "/newForH5")
    @ApiOperation(value = "H5新增预约单")
    public BizBaseResponse<String> newForH5(@RequestBody NewReservationReq req){
        BizBaseResponse<String> result = BizBaseResponse.success();
        String error = validParam(req);
        if(StringUtils.isNotBlank(error)){
            return new BizBaseResponse<>(MarketingBizErrorCodeEnum.PARAM_ERROR, error);
        }
        if(StringUtils.isBlank(req.getCustomerPhoneNumber())){
            return new BizBaseResponse<>(MarketingBizErrorCodeEnum.PARAM_ERROR, "客户手机号不能为空");
        }
        if(StringUtils.isBlank(req.getVerificationCode())){
            return new BizBaseResponse<>(MarketingBizErrorCodeEnum.PARAM_ERROR, "验证码不能为空");
        }
        if(StringUtils.isBlank(req.getSourceChannel())){
            return new BizBaseResponse<>(MarketingBizErrorCodeEnum.PARAM_ERROR, "预约渠道不能为空");
        }
        req.setTeminal(0);
        result.setData(iNewReservationService.addReservation(req,1));
        return result;
    }

    @PostMapping(value = "/newForB")
    @ApiOperation(value = "B端新增预约单")
    public BizBaseResponse<String> newForB(@RequestBody NewReservationReq req){
        BizBaseResponse<String> result = BizBaseResponse.success();
        String error = validParam(req);
        if(StringUtils.isNotBlank(error)){
            return new BizBaseResponse<>(MarketingBizErrorCodeEnum.PARAM_ERROR, error);
        }
        if(StringUtils.isBlank(req.getCustomerId())){
            return new BizBaseResponse<>(MarketingBizErrorCodeEnum.PARAM_ERROR, "客户ID不能为空");
        }
        req.setTeminal(1);
        req.setSourceChannel(SrvReservationChannelEnum.MD.getEnumCode());
        result.setData(iNewReservationService.addReservation(req,0));
        return result;
    }

    @PostMapping(value = "/newForC")
    @ApiOperation(value = "C端新增预约单")
    public BizBaseResponse<String> newForC(@RequestBody NewReservationReq req){
        BizBaseResponse<String> result = BizBaseResponse.success();
        String error = validParam(req);
        if(StringUtils.isNotBlank(error)){
            return new BizBaseResponse<>(MarketingBizErrorCodeEnum.PARAM_ERROR, error);
        }
        if(StringUtils.isBlank(req.getCustomerPhoneNumber())){
            return new BizBaseResponse<>(MarketingBizErrorCodeEnum.PARAM_ERROR, "客户手机号不能为空");
        }
        if(StringUtils.isBlank(req.getCustomerId())){
            return new BizBaseResponse<>(MarketingBizErrorCodeEnum.PARAM_ERROR, "客户ID不能为空");
        }
        if(StringUtils.isBlank(req.getSourceChannel())){
            return new BizBaseResponse<>(MarketingBizErrorCodeEnum.PARAM_ERROR, "预约渠道不能为空");
        }
        req.setTeminal(2);
        result.setData(iNewReservationService.addReservation(req,1));
        return result;
    }

    @PostMapping(value = "/updateForC")
    @ApiOperation(value = "车主小程序端修改预约单")
    public BizBaseResponse<Boolean> updateForC(@RequestBody NewReservationReq req){
        BizBaseResponse<Boolean> result = BizBaseResponse.success();
        String error = validParam(req);
        if(StringUtils.isNotBlank(error)){
            return new BizBaseResponse<>(MarketingBizErrorCodeEnum.PARAM_ERROR, error);
        }
        if(StringUtils.isBlank(req.getCustomerPhoneNumber())){
            return new BizBaseResponse<>(MarketingBizErrorCodeEnum.PARAM_ERROR, "客户手机号不能为空");
        }
        if(StringUtils.isBlank(req.getCustomerId())){
            return new BizBaseResponse<>(MarketingBizErrorCodeEnum.PARAM_ERROR, "客户ID不能为空");
        }
        if(StringUtils.isBlank(req.getSourceChannel())){
            return new BizBaseResponse<>(MarketingBizErrorCodeEnum.PARAM_ERROR, "预约渠道不能为空");
        }
        req.setTeminal(2);
        result.setData(true);
        return result;
    }

    @PostMapping(value = "/dateList")
    @ApiOperation(value = "获取门店预约日期")
    public BizBaseResponse<List<ReservationDateResp>> getReserveDateList(){
        BizBaseResponse<List<ReservationDateResp>> result = BizBaseResponse.success();
        List<ReservationDateResp> list = new ArrayList<>();
        ReservationDateResp resp = new ReservationDateResp();
        resp.setCount(2);
        resp.setReservationDate(new Date().getTime());
        list.add(resp);
        result.setData(list);
        return result;
    }

    @PostMapping(value = "/getBReservationList")
    @ApiOperation(value = "获取门店预约列表")
    public BizBaseResponse<List<BReservationListResp>> getBReservationList(@RequestBody BReservationListReq req){
        BizBaseResponse<List<BReservationListResp>> result= BizBaseResponse.success();
        List<BReservationListResp> list = new ArrayList<>();
        BReservationListResp resp = new BReservationListResp();
        resp.setPeriodName("9:00-10:00");
        resp.setReservationStartTime(new Date().getTime());
        resp.setReservationEndTime(new Date().getTime());
        List<ReservationDTO> dtos = new ArrayList<>();
        ReservationDTO dto = new ReservationDTO();
        dto.setCustomerName("杨澜清");
        dto.setCustomerPhoneNumber("18011111111");
        dto.setDescription("备注");
        dto.setId("111111");
        dto.setSourceChannel("ZXYY");
        dto.setStatus("UNCONFIRMED");
        dtos.add(dto);
        resp.setReservationDTOs(dtos);
        list.add(resp);
        result.setData(list);
        return result;
    }

    @PostMapping(value = "/getCReservationList")
    @ApiOperation(value = "获取车主小程序预约列表")
    public BizBaseResponse getCReservationList(@RequestBody CReservationListReq req){
        BizBaseResponse<PageInfo<ReservationDTO>> result = BizBaseResponse.success();
        return result;
    }

    @PostMapping(value = "/getCReservationDetail")
    @ApiOperation(value = "获取车主小程序预约单详情")
    public BizBaseResponse<ReservationDTO> getCReservationDetail(@RequestBody CReservationListReq req){
        BizBaseResponse<ReservationDTO> result = BizBaseResponse.success();
        ReservationDTO dto = new ReservationDTO();
        dto.setStatus("ORDERED");
        dto.setReservationTime(new Date().getTime());
        dto.setDescription("备注");
        dto.setCustomerPhoneNumber("18011111111");
        result.setData(dto);
        return result;
    }

    @PostMapping(value = "/confirmReservation")
    @ApiOperation(value = "确认预约")
    public BizBaseResponse confirmReservation(@RequestBody CReservationListReq req){
        BizBaseResponse rs = new BizBaseResponse("确认预约成功");
        return rs;
    }

    @PostMapping(value = "/cancelReservation")
    @ApiOperation(value = "取消预约")
    public BizBaseResponse cancelReservation(@RequestBody CancelReservationReq req){
        BizBaseResponse rs = new BizBaseResponse("取消成功");
        return rs;
    }


    //新增预约单公共校验
    private String validParam(NewReservationReq req){
        req.setTenantId(super.getTenantId());
        req.setUserId(super.getUserId());
        String result = "";
        if(req.getStoreId() == null){
            result = "门店ID不能为空";
        }
        if(req.getEstimatedArriveTime() == null){
            result = "预计到店时间不能为空";
        }
        return result;
    }
}
