package com.tuhu.store.saas.marketing.service;

import com.tuhu.store.saas.marketing.request.card.AddCardOrderReq;

/**
 * @author wangyuqing
 * @since 2020/8/4 16:40
 */
public interface ICardOrderService {
    /*
     * 开卡
     */
    Long addCardOrder(AddCardOrderReq req);
}
