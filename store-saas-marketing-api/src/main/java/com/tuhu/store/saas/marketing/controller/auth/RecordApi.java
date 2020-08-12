package com.tuhu.store.saas.marketing.controller.auth;

import com.tuhu.boot.common.facade.BizBaseResponse;
import com.tuhu.store.saas.marketing.dataobject.ClientEventRecordDAO;
import com.tuhu.store.saas.marketing.request.ClientEventRecordRequest;
import com.tuhu.store.saas.marketing.service.IClientEventRecordService;
import com.tuhu.store.saas.user.vo.EventTypeEnum;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;

/**
 * @time 2020-08-05
 * @auther kudeng
 */
@RestController
@RequestMapping("/record")
@Api(tags = "营销数据记录")
@Slf4j
public class RecordApi {

    @Autowired
    private IClientEventRecordService iClientEventRecordService;

    @GetMapping(value = "/user/{contentType}/record")
    public BizBaseResponse recordEndUserEvent(@PathVariable String contentType, @NotNull @Validated ClientEventRecordRequest clientEventRecordRequest) {
        clientEventRecordRequest.setContentType(contentType);
        clientEventRecordRequest.setEventType(EventTypeEnum.VISIT.getCode());
        iClientEventRecordService.recordEndUserEvent(clientEventRecordRequest);
        return BizBaseResponse.success();
    }

    @GetMapping(value = "/user/{contentType}/forward")
    public BizBaseResponse recordEndUserForwardEvent(@PathVariable String contentType, @NotNull @Validated ClientEventRecordRequest clientEventRecordRequest) {
        clientEventRecordRequest.setContentType(contentType);
        clientEventRecordRequest.setEventType(EventTypeEnum.WECHATFORWARD.getCode());
        iClientEventRecordService.recordEndUserEvent(clientEventRecordRequest);
        return BizBaseResponse.success();
    }

}
