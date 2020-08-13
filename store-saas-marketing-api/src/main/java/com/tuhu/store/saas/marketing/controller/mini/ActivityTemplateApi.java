package com.tuhu.store.saas.marketing.controller.mini;

import com.tuhu.boot.common.facade.BizBaseResponse;
import com.tuhu.store.saas.marketing.controller.BaseApi;
import com.tuhu.store.saas.marketing.po.ActivityTemplate;
import com.tuhu.store.saas.marketing.request.ActivityTemplateAdd;
import com.tuhu.store.saas.marketing.request.ActivityTemplateRequest;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
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

    @PostMapping(value = "/add")
    @ApiOperation(value = "新增活动模板")
    public BizBaseResponse add(@RequestBody ActivityTemplateAdd req) {
        BizBaseResponse result = BizBaseResponse.success();
        result.setData(1111);
        return  result;
    }

    @GetMapping("/detail")
    @ApiOperation(value = "查活动模板详情")
    public BizBaseResponse detail(Long id) {
        BizBaseResponse<ActivityTemplate> result = BizBaseResponse.success();
        ActivityTemplate template = new ActivityTemplate();
        template.setId(111l);
        template.setPicUrl("www.baidu.com");
        template.setActivityTitle("标题");
        template.setStartTime(new Date());
        template.setEndTime(new Date());
        template.setActiveType(0);
        template.setActiveDays(2);
        template.setActivityIntroduce("活动介绍");
        template.setApplyNumber(10l);
        template.setStatus(true);
        result.setData(template);
        return  result;
    }

    @PostMapping(value = "/edit")
    @ApiOperation(value = "编辑活动模板")
    public BizBaseResponse edit(@RequestBody ActivityTemplateAdd req) {
        return new BizBaseResponse("编辑成功");
    }

    @GetMapping("/delete")
    @ApiOperation(value = "删除活动模板")
    public BizBaseResponse delete(Long id) {
        BizBaseResponse<ActivityTemplate> result = BizBaseResponse.success();
        return new BizBaseResponse("删除成功");
    }

    @PostMapping(value = "/queryList")
    @ApiOperation(value = "查活动模板列表")
    public BizBaseResponse queryList(@RequestBody ActivityTemplateRequest req) {
        BizBaseResponse<List<ActivityTemplate>> result = BizBaseResponse.success();
        List<ActivityTemplate> list = new ArrayList<>();
        ActivityTemplate template = new ActivityTemplate();
        template.setId(11l);
        template.setPicUrl("www.baidu.conm");
        template.setActivityTitle("假装是个标题");
        template.setStatus(true);
        template.setStartTime(new Date());
        template.setEndTime(new Date());
        list.add(template);
        result.setData(list);
        return  result;
    }
}
