package com.tuhu.store.saas.marketing.service;

import com.github.pagehelper.PageInfo;
import com.tuhu.store.saas.marketing.request.card.AddCardOrderReq;
import com.tuhu.store.saas.marketing.request.card.ListCardOrderReq;
import com.tuhu.store.saas.marketing.response.card.CardOrderResp;

/**
 * @author wangyuqing
 * @since 2020/8/4 16:40
 */
public interface ICardOrderService {
    /*
     * 开卡
     */
    String addCardOrder(AddCardOrderReq req);

    PageInfo<CardOrderResp> getCardOrderList(ListCardOrderReq req);
}
