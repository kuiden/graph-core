package com.tuhu.store.saas.marketing.controller;

import com.github.pagehelper.PageInfo;
import com.tuhu.boot.common.facade.BizBaseResponse;
import com.tuhu.store.saas.marketing.request.AttachedInfoAddReq;
import com.tuhu.store.saas.marketing.request.AttachedInfoPageReq;
import com.tuhu.store.saas.marketing.request.card.CardTemplateModel;
import com.tuhu.store.saas.marketing.response.AttachedInfoResp;
import com.tuhu.store.saas.marketing.service.AttachedInfoService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/attachedInfo")
@Api(tags = "营销附加信息相关")
public class AttachedInfoApi extends BaseApi {
    @Autowired
    private AttachedInfoService service;

    /**
     * 附加信息新增
     *
     * @param
     * @return
     */
    @PostMapping(value = "/add")
    @ApiOperation(value = "附加信息保存")
    public BizBaseResponse<String> saveCardTemplate(@Validated @RequestBody AttachedInfoAddReq req) {
        return new BizBaseResponse<String>(service.add(req, super.getStoreId(), super.getTenantId(), super.getUserId()));
    }

    @PostMapping(value = "/getListByQuery")
    @ApiOperation(value = "获取分页信息")
    public BizBaseResponse<PageInfo<AttachedInfoResp>> getListByQuery(@Validated @RequestBody AttachedInfoPageReq req) {
        req.setStoreId(super.getStoreId());
        req.setTenantId(super.getTenantId());
        return new BizBaseResponse<PageInfo<AttachedInfoResp>>(service.getListByQuery(req));
    }


    @GetMapping(value = "/getAttachedInfoById")
    @ApiOperation(value = "获取详情")
    public BizBaseResponse<AttachedInfoResp> getListByQuery(@Validated @RequestParam String id) {

        return new BizBaseResponse<AttachedInfoResp>(service.getAttachedInfoById(id, super.getStoreId()));
    }
    @GetMapping(value = "/del")
    @ApiOperation(value = "删除")
    public BizBaseResponse<Boolean> del(@Validated @RequestParam String id) {

        return new BizBaseResponse<Boolean>(service.del(id, super.getStoreId()));
    }


}
