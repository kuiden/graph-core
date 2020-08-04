package com.tuhu.store.saas.marketing.service;

import com.github.pagehelper.PageInfo;
import com.tuhu.store.saas.marketing.request.card.CardTemplateModel;
import com.tuhu.store.saas.marketing.request.card.CardTemplateReq;

import java.util.function.Function;

/**
 * 次卡相关接口
 */
public  interface ICardService {

    /**
     * 新增次卡模板
     * @param req
     * @return
     */
    Long saveCardTemplate(CardTemplateModel req, String userId);


    /**
     * 获取次卡模板详情
     * @param id
     * @param tenantId
     * @param storeId
     * @return
     */
    CardTemplateModel getCardTemplateById(Long id, Long tenantId, Long storeId);

    /**
     * 卡模板分页
     * @param req
     * @return
     */
    PageInfo<CardTemplateModel> getCardTemplatePageInfo(CardTemplateReq req);
}
