package com.tuhu.store.saas.marketing.service;

import com.github.pagehelper.PageInfo;
import com.tuhu.store.saas.marketing.request.CustomerLastPurchaseRequest;
import com.tuhu.store.saas.marketing.request.card.*;
import com.tuhu.store.saas.marketing.request.vo.UpdateCardVo;
import com.tuhu.store.saas.marketing.response.card.*;
import com.tuhu.store.saas.marketing.response.dto.CustomerMarketCountDTO;

import java.util.Date;
import java.util.List;
import java.util.Map;

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


    /*
     * 更新次卡服务项目次数
     */
    Boolean updateCardQuantity(UpdateCardVo updateCardVo);

    Boolean hasCardByCustomerId(String id, Long storeId, Long tenantId);

    /*
     * 查询客户次卡
     */
    List<CardResp> queryCardRespList(MiniQueryCardReq req);

    /*
     * 查询次卡使用记录
     */
    List<CardUseRecordResp> consumptionHistory(Long id);

    /*
     * 查询次卡服务项目
     */
    List<QueryCardItemResp> queryCardItem(QueryCardItemReq req);


    CardResp clientQueryCardItem(QueryCardItemReq req);

    //查询用户次卡与优惠券总数
    CustomerMarketCountDTO queryCustomerMarketInfo(String customerId);

    List<QueryCardItemResp> queryCardItemByCustomer(QueryByCustomerIdReq req);

    List<QueryCardItemResp> allotCardItem(AllotCardItemReq req);


}
