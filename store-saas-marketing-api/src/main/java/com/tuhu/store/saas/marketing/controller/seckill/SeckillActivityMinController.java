package com.tuhu.store.saas.marketing.controller.seckill;

import com.github.pagehelper.PageInfo;
import com.tuhu.boot.common.facade.BizBaseResponse;
import com.tuhu.store.saas.marketing.annotation.EndUserApiIdempotent;
import com.tuhu.store.saas.marketing.context.EndUserContextHolder;
import com.tuhu.store.saas.marketing.controller.mini.EndUserApi;
import com.tuhu.store.saas.marketing.enums.ShoppingPlatformEnum;
import com.tuhu.store.saas.marketing.exception.NoneBizException;
import com.tuhu.store.saas.marketing.remote.EndUser;
import com.tuhu.store.saas.marketing.request.seckill.SeckillActivityDetailReq;
import com.tuhu.store.saas.marketing.request.seckill.SeckillRecordAddReq;
import com.tuhu.store.saas.marketing.request.seckill.SeckillRemindAddReq;
import com.tuhu.store.saas.marketing.response.seckill.*;
import com.tuhu.store.saas.marketing.service.seckill.PayService;
import com.tuhu.store.saas.marketing.service.seckill.SeckillActivityRemindService;
import com.tuhu.store.saas.marketing.service.seckill.SeckillActivityService;
import com.tuhu.store.saas.marketing.service.seckill.SeckillRegistrationRecordService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * <p>
 * C端秒杀活动表 前端控制器
 * </p>
 *
 * @author yangshengyong
 * @since 2020-12-02
 */
@RestController
@RequestMapping("/client/seckill/activity")
@Api(tags = "C端秒杀活动API")
@Slf4j
public class SeckillActivityMinController extends EndUserApi {

    @Autowired
    private SeckillActivityService seckillActivityService;

    @Autowired
    private SeckillRegistrationRecordService seckillRegistrationRecordService;

    @Autowired
    private SeckillActivityRemindService seckillActivityRemindService;

    @Autowired
    private PayService payService;

    @GetMapping("/list")
    @ApiOperation("秒杀活动列表")
    public BizBaseResponse<List<SeckillActivityListResp>> activityList() {
        EndUser endUser = EndUserContextHolder.getUser();
        if (null == endUser || StringUtils.isBlank(endUser.getStoreId()) || StringUtils.isBlank(endUser.getTenantId())) {
            log.error("参数校验失败");
            return new BizBaseResponse<>();
        }
        return new BizBaseResponse(seckillActivityService.clientActivityList(super.getStoreId(), super.getTenantId()));
    }

    @PostMapping("/detail")
    @ApiOperation("秒杀活动详情")
    public BizBaseResponse<SeckillActivityDetailResp> activityDetail(@RequestBody SeckillActivityDetailReq req) {
        req.setStoreId(super.getStoreId());
        req.setTenantId(super.getTenantId());
        req.setCustomerId(super.getCustomerId());
        return new BizBaseResponse(seckillActivityService.clientActivityDetail(req));
    }

    @PostMapping("/recordList")
    @ApiOperation("秒杀活动参与记录")
    public BizBaseResponse<PageInfo<SeckillRecordListResp>> activityRecordList(@RequestBody SeckillActivityDetailReq req) {
        req.setStoreId(super.getStoreId());
        req.setTenantId(super.getTenantId());
        req.setCustomerId(super.getCustomerId());
        return new BizBaseResponse(seckillActivityService.clientActivityRecordList(req));
    }

    @GetMapping("/customer/orderList")
    @ApiOperation("秒杀订单列表")
    public BizBaseResponse<List<CustomerActivityOrderListResp>> customerActivityOrderList() {
        EndUser endUser = EndUserContextHolder.getUser();
        if (null == endUser || StringUtils.isBlank(endUser.getStoreId()) || StringUtils.isBlank(endUser.getTenantId())) {
            log.error("参数校验失败");
            return new BizBaseResponse<>();
        }
        return new BizBaseResponse(seckillActivityService.customerActivityOrderList(super.getCustomerId(), super.getStoreId(), super.getTenantId()));
    }

    @PostMapping("/customer/orderDetail")
    @ApiOperation("秒杀订单详情")
    public BizBaseResponse<CustomerActivityOrderDetailResp> customerActivityOrderDetail(@RequestBody SeckillActivityDetailReq req) {
        req.setStoreId(super.getStoreId());
        req.setTenantId(super.getTenantId());
        req.setCustomerId(super.getCustomerId());
        return new BizBaseResponse(seckillActivityService.customerActivityOrderDetail(req));
    }

    @PostMapping("/customer/orderAdd")
    @ApiOperation("小程序抢购、创建秒杀订单")
    @EndUserApiIdempotent(lockTime = 3)
    public BizBaseResponse customerActivityOrderAdd(@Validated @RequestBody SeckillRecordAddReq req) {
        if (Objects.isNull(super.getStoreId()) || Objects.isNull(super.getTenantId()) || Objects.isNull(super.getCustomerId()) || Objects.isNull(super.getOpenId())) {
            throw new NoneBizException("请求参数异常！");
        }
        req.setStoreId(super.getStoreId());
        req.setTenantId(super.getTenantId());
        req.setCustomerId(super.getCustomerId());
        req.setCustomerName(super.getName());
        req.setOpenId(super.getOpenId());
        //创建活动订单、待收单
        Map<String, Object> mapResult = seckillRegistrationRecordService.customerActivityOrderAdd(req, ShoppingPlatformEnum.WECHAT_APPLET);
        return new BizBaseResponse(mapResult);
    }

    @PostMapping("/customer/orderPayDetail")
    @ApiOperation("秒杀订单支付详情(前端轮询)")
    public BizBaseResponse<Map<String, Object>> customerActivityOrderPayDetail(@RequestBody SeckillActivityDetailReq seckillActivityDetailReq) {
        return new BizBaseResponse(seckillRegistrationRecordService.OrderPayDetail(seckillActivityDetailReq));
    }

    @PostMapping("/customer/remindAdd")
    @ApiOperation("添加开抢提醒")
    @EndUserApiIdempotent(lockTime = 1)
    public BizBaseResponse customerActivityRemindAdd(@RequestBody SeckillRemindAddReq req) {
        req.setStoreId(super.getStoreId());
        req.setTenantId(super.getTenantId());
        seckillActivityRemindService.customerActivityRemindAdd(req);
        return new BizBaseResponse();
    }


}

