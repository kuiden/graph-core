package com.tuhu.store.saas.marketing.controller.mini;

import com.tuhu.boot.common.facade.BizBaseResponse;
import com.tuhu.store.saas.marketing.controller.BaseApi;
import com.tuhu.store.saas.marketing.exception.StoreSaasMarketingException;
import com.tuhu.store.saas.marketing.po.ActivityTemplate;
import com.tuhu.store.saas.marketing.request.ActivityTemplateAdd;
import com.tuhu.store.saas.marketing.request.ActivityTemplateRequest;
import com.tuhu.store.saas.marketing.service.IActivityTemplateService;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @Author: yanglanqing
 * @Date: 2020/8/12 17:13
 */
@RestController
@RequestMapping("/activityTemplate")
@Slf4j
public class ActivityTemplateApi  extends BaseApi {

    @Autowired
    IActivityTemplateService activityTemplateService;

    @PostMapping(value = "/add")
    @ApiOperation(value = "新增活动模板")
    public BizBaseResponse add(@Validated @RequestBody ActivityTemplateAdd req) {
        BizBaseResponse result = BizBaseResponse.success();
        if(req.getActiveType() == 0 && req.getActiveDays() == null){
            throw new StoreSaasMarketingException("报名活动后有效天数不能为空");
        }
        if(req.getActiveType() == 1 && req.getActiveDate() == null){
            throw new StoreSaasMarketingException("活动截止日期不能为空");
        }
        result.setData(activityTemplateService.insert(req));
        return result;
    }

    @GetMapping("/detail")
    @ApiOperation(value = "查活动模板详情")
    public BizBaseResponse detail(Long id) {
        BizBaseResponse<ActivityTemplate> result = BizBaseResponse.success();
        if(id == null){
            throw new StoreSaasMarketingException("活动模板id不能为空");
        }
        result.setData(activityTemplateService.queryDetailById(id));
        return result;
    }

    @PostMapping(value = "/edit")
    @ApiOperation(value = "编辑活动模板")
    public BizBaseResponse edit(@Validated @RequestBody ActivityTemplateAdd req) {
        if(req.getId() == null){
            throw new StoreSaasMarketingException("活动模板id不能为空");
        }
        activityTemplateService.updateById(req);
        return new BizBaseResponse("编辑成功");
    }

    @GetMapping("/delete")
    @ApiOperation(value = "删除活动模板")
    public BizBaseResponse delete(Long id) {
        if(id == null){
            throw new StoreSaasMarketingException("活动模板id不能为空");
        }
        activityTemplateService.delete(id);
        return new BizBaseResponse("删除成功");
    }

    @PostMapping(value = "/queryList")
    @ApiOperation(value = "查活动模板列表")
    public BizBaseResponse queryList(@RequestBody ActivityTemplateRequest req) {
        BizBaseResponse<List<ActivityTemplate>> result = BizBaseResponse.success();
        List<ActivityTemplate> list = activityTemplateService.queryList(req);
        result.setData(list);
        return result;
    }
}
