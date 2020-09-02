package com.tuhu.store.saas.marketing.controller.feign;

import com.tuhu.boot.common.facade.BizBaseResponse;
import com.tuhu.store.saas.marketing.request.vo.MessageTemplateLocalVo;
import com.tuhu.store.saas.marketing.request.vo.UpdateCardVo;
import com.tuhu.store.saas.marketing.service.IMessageTemplateLocalService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/feign/messageTemplateLocal")
@Api(tags = "本地短信模版对外接口")
public class MessageTemplateLocalApi {
    @Autowired
    private IMessageTemplateLocalService iMessageTemplateLocalService;

    @PostMapping("/getSMSTemplateIdByCodeAndStoreId")
    @ApiOperation("根据短信模版CODE获取ID")
    public BizBaseResponse getSMSTemplateIdByCodeAndStoreId(@RequestBody MessageTemplateLocalVo messageTemplateLocalVo) {
        return new BizBaseResponse(iMessageTemplateLocalService.getSMSTemplateIdByCodeAndStoreId(messageTemplateLocalVo.getTemplateCode(),messageTemplateLocalVo.getStoreId()));
    }
}
