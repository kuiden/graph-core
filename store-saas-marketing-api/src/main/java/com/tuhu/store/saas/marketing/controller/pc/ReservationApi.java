package com.tuhu.store.saas.marketing.controller.pc;

import com.tuhu.boot.common.facade.BizBaseResponse;
import com.tuhu.store.saas.marketing.controller.BaseApi;
import com.tuhu.store.saas.marketing.enums.MarketingBizErrorCodeEnum;
import com.tuhu.store.saas.marketing.request.NewReservationReq;
import com.tuhu.store.saas.marketing.request.ReservePeriodReq;
import com.tuhu.store.saas.marketing.response.ReservationPeriodResp;
import com.tuhu.store.saas.marketing.service.INewReservationService;
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
@RequestMapping("/order/reservation")
@Slf4j
public class ReservationApi extends BaseApi {

    @Autowired
    INewReservationService iNewReservationService;

//    @PostMapping(value = "/dateList")
//    @ApiOperation(value = "预约日期list")
//    public BizBaseResponse<List<ReservationDateResp>> getReserveDateList(){
//        BizBaseResponse<List<ReservationDateResp>> result = BizBaseResponse.success();
//        return result;
//    }

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
        req.setTeminal(0);
        result.setData(iNewReservationService.addReservation(req));
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
        result.setData(iNewReservationService.addReservation(req));
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
        req.setTeminal(2);
        result.setData(iNewReservationService.addReservation(req));
        return result;
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
