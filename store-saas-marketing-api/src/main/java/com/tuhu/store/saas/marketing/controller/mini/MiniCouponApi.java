package com.tuhu.store.saas.marketing.controller.mini;


import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.tuhu.boot.common.facade.BizBaseResponse;
import com.tuhu.store.saas.marketing.controller.BaseApi;
import com.tuhu.store.saas.marketing.dataobject.CustomerCoupon;
import com.tuhu.store.saas.marketing.exception.StoreSaasMarketingException;
import com.tuhu.store.saas.marketing.po.CustomerCouponPO;
import com.tuhu.store.saas.marketing.remote.ResultObject;
import com.tuhu.store.saas.marketing.request.*;
import com.tuhu.store.saas.marketing.request.vo.ServiceOrderCouponUseVO;
import com.tuhu.store.saas.marketing.request.vo.ServiceOrderCouponVO;
import com.tuhu.store.saas.marketing.response.*;
import com.tuhu.store.saas.marketing.response.dto.CustomerCouponDTO;
import com.tuhu.store.saas.marketing.response.dto.ServiceOrderCouponDTO;
import com.tuhu.store.saas.marketing.service.ICouponService;
import com.tuhu.store.saas.marketing.service.IMCouponService;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 优惠券相关Controller
 */
@RestController
@RequestMapping("/mini/coupon")
public class MiniCouponApi extends BaseApi {

    @Autowired
    private ICouponService iCouponService;

    @Autowired
    private IMCouponService imCouponService;

    @RequestMapping(value = "/add", method = RequestMethod.POST)
    @ApiOperation(value = "优惠券活动新增")
    public BizBaseResponse add(@Validated @RequestBody AddCouponReq addCouponReq) {
        addCouponReq.setUserId(this.getUserId());
        addCouponReq.setTenantId(super.getTenantId());
        addCouponReq.setStoreId(super.getStoreId());
        addCouponReq = iCouponService.addNewCoupon(addCouponReq);
        return new BizBaseResponse(addCouponReq);
    }

    @RequestMapping(value = "/detail", method = {RequestMethod.GET, RequestMethod.POST})
    @ApiOperation(value = "优惠券活动详情")
    public BizBaseResponse detail(Long couponId) {
        CouponResp couponResp = iCouponService.getCouponDetailById(couponId);
        return new BizBaseResponse(couponResp);
    }

    @RequestMapping(value = "/list", method = {RequestMethod.GET, RequestMethod.POST})
    @ApiOperation(value = "优惠券活动查询")
    public BizBaseResponse list(@Validated @RequestBody CouponListReq couponListReq) {
        couponListReq.setUserId(this.getUserId());
        couponListReq.setStoreId(this.getStoreId());
        couponListReq.setTenantId(this.getTenantId());
        PageInfo<CouponResp> couponRespPage = iCouponService.listCoupon(couponListReq);
        return new BizBaseResponse(couponRespPage);
    }

    @RequestMapping(value = "/edit", method = RequestMethod.POST)
    @ApiOperation(value = "优惠券活动编辑")
    public BizBaseResponse edit(@Validated @RequestBody EditCouponReq editCouponReq) {
        editCouponReq.setUserId(this.getUserId());
        editCouponReq.setStoreId(this.getStoreId());
        editCouponReq.setTenantId(this.getTenantId());
        editCouponReq = iCouponService.editCoupon(editCouponReq);
        return new BizBaseResponse(editCouponReq);
    }

    @RequestMapping(value = "/send", method = RequestMethod.POST)
    @ApiOperation(value = "优惠券活动送券")
    public BizBaseResponse<List<CommonResp<CustomerCoupon>>> send(@Validated @RequestBody SendCouponReq sendCouponReq) {
        sendCouponReq.setUserId(this.getUserId());
        sendCouponReq.setStoreId(this.getStoreId());
        sendCouponReq.setTenantId(this.getTenantId());
        if (sendCouponReq.getReceiveType() == null) {
            sendCouponReq.setReceiveType(Integer.valueOf(1));//手动发券
        }
        List<CommonResp<CustomerCoupon>> customerCouponRespList = iCouponService.sendCoupon(sendCouponReq);
        boolean hasFailed = false;
        CommonResp<CustomerCoupon> fail = new CommonResp<CustomerCoupon>();
        for (CommonResp<CustomerCoupon> customerCouponResp : customerCouponRespList) {
            if (!customerCouponResp.isSuccess()) {
                hasFailed = true;
                fail = customerCouponResp;
                break;
            }
        }
        BizBaseResponse<List<CommonResp<CustomerCoupon>>> resultObject = new BizBaseResponse<List<CommonResp<CustomerCoupon>>>();
        if (hasFailed) {
            resultObject.setData(Lists.newArrayList(fail));
            resultObject.setCode(4000);
        } else {
            resultObject.setData(customerCouponRespList);
        }
        return resultObject;
    }


    @RequestMapping(value = "/getCouponsForServiceOrder", method = RequestMethod.POST)
    @ApiOperation(value = "根据工单查询可用优惠券")
    public ResultObject getCouponsForServiceOrder(@RequestBody ServiceOrderCouponVO serviceOrderCouponVO) {
        serviceOrderCouponVO.setStoreId(String.valueOf(this.getStoreId()));
        serviceOrderCouponVO.setTenantId(String.valueOf(this.getTenantId()));
        ServiceOrderCouponDTO serviceOrderCouponDTO = iCouponService.getCouponsForServiceOrder(serviceOrderCouponVO);
        return new ResultObject(serviceOrderCouponDTO);
    }

    @RequestMapping(value = "/writeOffCustomerCouponForServiceOrder", method = RequestMethod.POST)
    @ApiOperation(value = "根据工单核销客户优惠券")
    public ResultObject writeOffCustomerCouponForServiceOrder(@RequestBody ServiceOrderCouponUseVO serviceOrderCouponUseVO) {
        serviceOrderCouponUseVO.setStoreId(String.valueOf(this.getStoreId()));
        serviceOrderCouponUseVO.setTenantId(String.valueOf(this.getTenantId()));
        ServiceOrderCouponDTO serviceOrderCouponDTO = iCouponService.writeOffCustomerCouponForServiceOrder(serviceOrderCouponUseVO);
        return new ResultObject(serviceOrderCouponDTO);
    }

    @RequestMapping(value = "/cancelWriteOffCustomerCouponForServiceOrder", method = RequestMethod.POST)
    @ApiOperation(value = "根据工单取消核销客户优惠券")
    public ResultObject cancelWriteOffCustomerCouponForServiceOrder(@RequestBody ServiceOrderCouponUseVO serviceOrderCouponUseVO) {
        serviceOrderCouponUseVO.setStoreId(String.valueOf(this.getStoreId()));
        serviceOrderCouponUseVO.setTenantId(String.valueOf(this.getTenantId()));
        ServiceOrderCouponDTO serviceOrderCouponDTO = iCouponService.cancelWriteOffCustomerCouponForServiceOrder(serviceOrderCouponUseVO);
        return new ResultObject(serviceOrderCouponDTO);
    }

    @RequestMapping(value = "/getCouponDetailByCustomerCouponId", method = {RequestMethod.POST, RequestMethod.GET})
    @ApiOperation(value = "根据客户优惠券ID查询优惠券详情")
    public ResultObject getCouponDetailByCustomerCouponId(String customerCouponId) {
        CustomerCouponDTO customerCouponDTO = iCouponService.getCouponDetailByCustomerCouponId(customerCouponId, String.valueOf(this.getStoreId()));
        return new ResultObject(customerCouponDTO);
    }

    @GetMapping("/getQrCode")
    public ResultObject getCouponQrByStoreId(QrCodeRequest req) {
        String url = imCouponService.getQrCodeForCoupon(req);
        return new ResultObject(url);
    }


    /**
     * 整体效果
     *
     * @param req
     * @return
     */
    @GetMapping("/getOveralEffect")
    public BizBaseResponse getCouponOveral(CouponRequest req) {
        req.setStoreId(super.getStoreId());
        req.setTenantId(super.getTenantId());
        Map result = imCouponService.getOveralEffect(req);
        return new BizBaseResponse(result);
    }

    /**
     * 主动领券效果
     * @param req
     * @return
     */
//    @GetMapping("/gettingEffect")
//    public ResultObject getCouponGettingEffect(CouponRequest req) {
//        Map result = iCouponService.getGettingEffect(req);
//        return new ResultObject(result);
//    }

    /**
     * 优惠券领取列表
     *
     * @param req
     * @return
     */
    @GetMapping("/couponReceiveList")
    public BizBaseResponse getCouponReceiveList(CouponReceiveRecordRequest req) {
        req.setStoreId(super.getStoreId());
        req.setTenantId(super.getTenantId());
        CustomerCouponPageResp result = imCouponService.getCouponReceiveList(req);
        return new BizBaseResponse(result);
    }

    /**
     * 发券
     *
     * @param req
     * @return
     */
    @PostMapping("/sendCoupon")
    public ResultObject sendCoupon(@RequestBody SendCouponRequest req) {
        //TODO
        //req.setSendUser(this.getUsername());
        Map result = imCouponService.sendCoupon(req);
        return new ResultObject(result);
    }

    /**
     * 券详情
     *
     * @param req
     * @return
     */
    @GetMapping("/getCouponDetail")
    public BizBaseResponse getCouponDetail(CouponRequest req) {
        Map result = imCouponService.getCouponDetail(req);
        return new BizBaseResponse(result);
    }


    @GetMapping("/getCouponDetailV2")
    public BizBaseResponse getCouponDetailv2(CouponRequest req) {
        if (StringUtils.isBlank(req.getCustomerCouponCode())){
            throw  new StoreSaasMarketingException("参数验证失败");
        }
        req.setStoreId(super.getStoreId());
        req.setTenantId(super.getTenantId());
        CustomerCouponPO result = imCouponService.getCouponDetailv2(req);
        return new BizBaseResponse(result);
    }



    /**
     * 营销发券统计数据
     *
     * @return
     */
    @PostMapping("/getCouponStatisticsForCustomerMarket")
    public ResultObject getCouponStatisticsForCustomerMarket(@RequestBody CouponStatisticsForCustomerMarketReq couponStatisticsForCustomerMarketReq) {
        CouponStatisticsForCustomerMarketResp couponStatisticsForCustomerMarketResp = iCouponService.getCouponStatisticsForCustomerMarket(couponStatisticsForCustomerMarketReq.getCouponCode(), couponStatisticsForCustomerMarketReq.getCustomerIds());
        return new ResultObject(couponStatisticsForCustomerMarketResp);
    }


    @PostMapping("/customerCoupon")
    public ResultObject getCustomerCouponList(@RequestBody CustomerCouponRequest couponRequest){
        PageInfo<CustomerCouponResponse> customerCouponList = iCouponService.getCustomerCouponList(couponRequest);
        return new ResultObject(customerCouponList);
    }

    @GetMapping("/customerCouponDetail/{couponId}")
    public ResultObject getCustomerCouponDetail(@PathVariable Long couponId){
        iCouponService.get

    }
}
