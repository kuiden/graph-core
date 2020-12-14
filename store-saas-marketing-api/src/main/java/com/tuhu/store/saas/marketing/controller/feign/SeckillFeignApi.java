package com.tuhu.store.saas.marketing.controller.feign;

import com.github.pagehelper.PageInfo;
import com.tuhu.boot.common.facade.BizBaseResponse;
import com.tuhu.store.saas.marketing.request.seckill.SeckillActivityDetailReq;
import com.tuhu.store.saas.marketing.response.seckill.CustomerActivityOrderDetailResp;
import com.tuhu.store.saas.marketing.response.seckill.SeckillActivityDetailResp;
import com.tuhu.store.saas.marketing.response.seckill.SeckillRecordListResp;
import com.tuhu.store.saas.marketing.service.seckill.SeckillActivityService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author wangyuqing
 * @since 2020/12/14 10:47
 */
@Slf4j
@RestController
@RequestMapping("/feign/seckill/activity")
@Api(tags = "C端秒杀活动对外API")
public class SeckillFeignApi {

    @Autowired
    private SeckillActivityService seckillActivityService;

    @PostMapping("/detail")
    @ApiOperation("秒杀活动详情")
    public BizBaseResponse<SeckillActivityDetailResp> activityDetail(@RequestBody SeckillActivityDetailReq req) {
        return new BizBaseResponse(seckillActivityService.clientActivityDetail(req));
    }

    @PostMapping("/recordList")
    @ApiOperation("秒杀活动参与记录")
    public BizBaseResponse<PageInfo<SeckillRecordListResp>> activityRecordList(@RequestBody SeckillActivityDetailReq req) {
        return new BizBaseResponse(seckillActivityService.clientActivityRecordList(req));
    }

    @PostMapping("/customer/orderDetail")
    @ApiOperation("秒杀订单详情")
    public BizBaseResponse<CustomerActivityOrderDetailResp> customerActivityOrderDetail(@RequestBody SeckillActivityDetailReq req) {
        return new BizBaseResponse(seckillActivityService.customerActivityOrderDetail(req));
    }

}
