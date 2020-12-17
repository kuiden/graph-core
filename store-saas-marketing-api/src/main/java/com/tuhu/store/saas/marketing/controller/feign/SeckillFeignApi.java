package com.tuhu.store.saas.marketing.controller.feign;

import com.github.pagehelper.PageInfo;
import com.tuhu.boot.common.facade.BizBaseResponse;
import com.tuhu.store.saas.marketing.request.seckill.SeckillActivityDetailReq;
import com.tuhu.store.saas.marketing.request.seckill.SeckillActivityQrCodeReq;
import com.tuhu.store.saas.marketing.response.seckill.SeckillActivityDetailResp;
import com.tuhu.store.saas.marketing.response.seckill.SeckillRecordListResp;
import com.tuhu.store.saas.marketing.service.seckill.SeckillActivityService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * @author wangyuqing
 * @since 2020/12/14 10:47
 */
@Slf4j
@RestController
@RequestMapping("/feign/seckill/activity")
@Api(tags = "秒杀活动对外API")
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

    @PostMapping(value = "/customer/qrCodeUrl")
    @ApiOperation(value = "活动二维码url")
    public BizBaseResponse<String> qrCodeUrl(@Validated @RequestBody SeckillActivityQrCodeReq request){
        return new BizBaseResponse(seckillActivityService.qrCodeUrlMin(request));
    }

    @GetMapping(value = "/getCache")
    @ApiOperation(value = "活动预览")
    public BizBaseResponse<SeckillActivityDetailResp> getCache(@RequestParam String id) {
        return new BizBaseResponse<>(seckillActivityService.activityDetailPreview(id));
    }


    @PostMapping(value = "/customer/qrCodeImage",produces = MediaType.IMAGE_JPEG_VALUE)
    @ApiOperation(value = "活动二维码图片流")
    @ResponseBody
    public byte[] qrCodeByte(@Validated @RequestBody SeckillActivityQrCodeReq request){
        return seckillActivityService.qrCodeImage(request);
    }

}
