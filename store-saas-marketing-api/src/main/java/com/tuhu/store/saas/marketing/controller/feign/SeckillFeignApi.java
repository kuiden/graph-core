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
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.concurrent.TimeUnit;

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

    @Autowired
    private RedisTemplate redisTemplate;

    private String cacheKeyPre = "SECKILLACTIVITY:";

    @PostMapping("/detail")
    @ApiOperation("秒杀活动详情")
    public BizBaseResponse<SeckillActivityDetailResp> activityDetail(@RequestBody SeckillActivityDetailReq req, HttpServletRequest request) {
        SeckillActivityDetailResp seckillActivityDetailResp = null;
        String ip = getIpAddress(request);
        String cacheKey = cacheKeyPre.concat("openGetActivityDetail:").concat(StringUtils.isNotBlank(ip) ? ip : "").concat(req.getSeckillActivityId());
        String key = cacheKey.concat("num");
        Long num = redisTemplate.opsForValue().increment(key, 1L);
        if (num.equals(1L)) {  //第一次访问
            redisTemplate.expire(key, 2, TimeUnit.SECONDS);
        }
        if (!redisTemplate.hasKey(cacheKey)) {
            seckillActivityDetailResp = seckillActivityService.clientActivityDetail(req);
            if (num.equals(20L)) {  //2秒内访问20次，判定该ip为恶意刷接口，进缓存
                redisTemplate.opsForValue().set(cacheKey, seckillActivityDetailResp, 30, TimeUnit.MINUTES);
            }
        } else {
            seckillActivityDetailResp = (SeckillActivityDetailResp) redisTemplate.opsForValue().get(cacheKey);
        }
        return new BizBaseResponse(seckillActivityDetailResp);
    }

    @PostMapping("/recordList")
    @ApiOperation("秒杀活动参与记录")
    public BizBaseResponse<PageInfo<SeckillRecordListResp>> activityRecordList(@RequestBody SeckillActivityDetailReq req, HttpServletRequest request) {
        PageInfo<SeckillRecordListResp> recordListRespPageInfo = new PageInfo<>();
        String ip = getIpAddress(request);
        String cacheKey = cacheKeyPre.concat("openGetActivityRecordList:").concat(StringUtils.isNotBlank(ip) ? ip : "").concat(req.getSeckillActivityId());
        String key = cacheKey.concat("num");
        Long num = redisTemplate.opsForValue().increment(key, 1L);
        if (num.equals(1L)) {
            redisTemplate.expire(key, 2, TimeUnit.SECONDS);
        }
        if (num.compareTo(20L) < 0) {
            recordListRespPageInfo = seckillActivityService.clientActivityRecordList(req);
        }
        return new BizBaseResponse(recordListRespPageInfo);
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

    public String getIpAddress(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (StringUtils.isNotEmpty(ip) && !"unKnown".equalsIgnoreCase(ip)) {
            //多次反向代理后会有多个ip值，第一个ip才是真实ip
            int index = ip.indexOf(",");
            if (index != -1) {
                return ip.substring(0, index);
            } else {
                return ip;
            }
        }
        ip = request.getHeader("X-Real-IP");
        if (StringUtils.isNotEmpty(ip) && !"unKnown".equalsIgnoreCase(ip)) {
            return ip;
        }
        return request.getRemoteAddr();
    }

}
