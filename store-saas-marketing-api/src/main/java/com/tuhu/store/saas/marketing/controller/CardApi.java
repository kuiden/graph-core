package com.tuhu.store.saas.marketing.controller;

 
import com.github.pagehelper.PageInfo;
import com.tuhu.boot.common.facade.BizBaseResponse;
import com.tuhu.store.saas.marketing.request.card.CardTemplateModel;
import com.tuhu.store.saas.marketing.request.card.CardTemplateReq;
import com.tuhu.store.saas.marketing.service.ICardService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/card")
@Api(value = "次卡相关")
public class CardApi extends BaseApi {

    @Autowired
    private ICardService cardService;

    /**
     * 卡模板保存
     *
     * @param cardTemplateModel
     * @return
     */
    @PostMapping(value = "/saveCardTemplate")
    @ApiOperation(value = "卡模板保存")
    public BizBaseResponse<Long> saveCardTemplate(@Validated @RequestBody CardTemplateModel cardTemplateModel) {
        cardTemplateModel.setTenantId(super.getTenantId());
        cardTemplateModel.setStoreId(super.getStoreId());
        cardTemplateModel.setUserName(super.getUserName());
        return new BizBaseResponse<>(cardService.saveCardTemplate(cardTemplateModel, super.getUserId()));
    }


    @GetMapping(value = "/getCardTemplateInfoById")
    @ApiOperation(value = "卡模板详情")
    public BizBaseResponse<CardTemplateModel> getCardTemplateInfoById(@RequestParam Long id) {
        return new BizBaseResponse<>(cardService.getCardTemplateById(id, super.getTenantId(), super.getStoreId()));
    }


    @PostMapping(value = "/getCardTemplateList")
    @ApiOperation(value = "卡模板详情")
    public BizBaseResponse<PageInfo<CardTemplateModel>> getCardTemplateList(@RequestBody CardTemplateReq req) {
        req.setTenantId(super.getTenantId());
        req.setStoreId(super.getStoreId());
        return new BizBaseResponse<>(cardService.getCardTemplatePageInfo(req));
    }
}
