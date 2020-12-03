package com.tuhu.store.saas.marketing.controller.seckill;


import com.baomidou.mybatisplus.plugins.Page;
import com.tuhu.boot.common.facade.BizBaseResponse;
import com.tuhu.store.saas.marketing.controller.BaseApi;
import com.tuhu.store.saas.marketing.request.seckill.SeckillActivityReq;
import com.tuhu.store.saas.marketing.response.seckill.SeckillActivityResp;
import com.tuhu.store.saas.marketing.service.seckill.SeckillActivityService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 秒杀活动表 前端控制器
 * </p>
 *
 * @author yangshengyong
 * @since 2020-12-02
 */
@RestController
@RequestMapping("/seckill/activity")
public class SeckillActivityController extends BaseApi {
    @Autowired
    private SeckillActivityService seckillActivityService;

    @PostMapping(value = "/pageList")
    @ApiOperation(value = "秒杀活动列表 1未开始、2进行中、9已下架")
    public BizBaseResponse<Page<SeckillActivityResp>> pageList(@RequestBody SeckillActivityReq req){
        return new BizBaseResponse(seckillActivityService.pageList(req));
    }

    @PostMapping(value = "/dataStatistics")
    @ApiOperation(value = "活动数据-数据统计")
    public BizBaseResponse dataStatistics(){
        return new BizBaseResponse();
    }
    //TODO 活动数据-已购客户、浏览未购买客户分页列表

    //TODO 活动海报

    @PostMapping(value = "/onShelf")
    @ApiOperation(value = "编辑上架")
    public BizBaseResponse onShelf(@Validated @RequestBody SeckillActivityReq req){
        return new BizBaseResponse();
    }

    @PostMapping(value = "/offShelf")
    @ApiOperation(value = "活动下架")
    public BizBaseResponse offShelf(@Validated @RequestBody SeckillActivityReq req){
        return new BizBaseResponse();
    }

    @PostMapping(value = "/detail")
    @ApiOperation(value = "活动详情")
    public BizBaseResponse detail(){
        return new BizBaseResponse();
    }
    //TODO 活动数据-客户参与详情
}

