package com.tuhu.store.saas.marketing.controller.seckill;

import com.github.pagehelper.PageInfo;
import com.tuhu.boot.common.facade.BizBaseResponse;
import com.tuhu.store.saas.marketing.context.EndUserContextHolder;
import com.tuhu.store.saas.marketing.controller.BaseApi;
import com.tuhu.store.saas.marketing.exception.StoreSaasMarketingException;
import com.tuhu.store.saas.marketing.remote.EndUser;
import com.tuhu.store.saas.marketing.request.seckill.SeckillActivityDetailReq;
import com.tuhu.store.saas.marketing.request.seckill.SeckillRecordAddReq;
import com.tuhu.store.saas.marketing.request.seckill.SeckillRemindAddReq;
import com.tuhu.store.saas.marketing.response.seckill.CustomerActivityOrderListResp;
import com.tuhu.store.saas.marketing.response.seckill.SeckillActivityListResp;
import com.tuhu.store.saas.marketing.response.seckill.SeckillRegistrationRecordResp;
import com.tuhu.store.saas.marketing.service.seckill.SeckillActivityService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
public class SeckillActivityMinController extends BaseApi {

    @Autowired
    private SeckillActivityService seckillActivityService;

    @GetMapping("/list")
    @ApiOperation("秒杀活动列表")
    public BizBaseResponse<List<SeckillActivityListResp>> activityList(){
        EndUser endUser = EndUserContextHolder.getUser();
        if (null == endUser || StringUtils.isBlank(endUser.getStoreId()) || StringUtils.isBlank(endUser.getTenantId())){
            throw new StoreSaasMarketingException("未获取到门店信息");
        }
        return new BizBaseResponse(seckillActivityService.clientActivityList(EndUserContextHolder.getStoreId(),EndUserContextHolder.getTenantId()));
    }

    @PostMapping("/detail")
    @ApiOperation("秒杀活动详情")
    public BizBaseResponse activityDetail(@RequestBody SeckillActivityDetailReq req){

        return new BizBaseResponse();
    }

    @PostMapping("/recordList")
    @ApiOperation("秒杀活动参与记录")
    public BizBaseResponse<PageInfo<SeckillRegistrationRecordResp>> activityRecordList(@RequestBody SeckillActivityDetailReq req){

        return new BizBaseResponse();
    }

    @PostMapping("/customer/orderList")
    @ApiOperation("秒杀订单列表")
    public BizBaseResponse<PageInfo<CustomerActivityOrderListResp>> CustomerActivityOrderList(){

        return new BizBaseResponse();
    }

    @PostMapping("/customer/orderDetail")
    @ApiOperation("秒杀订单详情")
    public BizBaseResponse CustomerActivityOrderDetail(){

        return new BizBaseResponse();
    }

    @PostMapping("/customer/orderAdd")
    @ApiOperation("创建秒杀订单")
    public BizBaseResponse CustomerActivityOrderAdd(@RequestBody SeckillRecordAddReq req){
        //创建活动订单、待收单

        return new BizBaseResponse();
    }

    @PostMapping("/customer/remindAdd")
    @ApiOperation("添加开抢提醒")
    public BizBaseResponse<Boolean> CustomerActivityRemindAdd(@RequestBody SeckillRemindAddReq req){

        return new BizBaseResponse();
    }

}

