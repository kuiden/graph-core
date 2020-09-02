package com.tuhu.store.saas.marketing.service;

import com.github.pagehelper.PageInfo;
import com.tuhu.store.saas.marketing.dataobject.CustomerCardOrder;
import com.tuhu.store.saas.marketing.request.card.AddCardOrderReq;
import com.tuhu.store.saas.marketing.request.card.ListCardOrderReq;
import com.tuhu.store.saas.marketing.request.card.QueryCardOrderReq;
import com.tuhu.store.saas.marketing.response.ComputeMarktingCustomerForReportResp;
import com.tuhu.store.saas.marketing.response.card.CardOrderDetailResp;
import com.tuhu.store.saas.marketing.response.card.CardOrderResp;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author wangyuqing
 * @since 2020/8/4 16:40
 */
public interface ICardOrderService {
    /*
     * 开卡
     */
    String addCardOrder(AddCardOrderReq req);

    /*
     * 获取开卡单列表
     */
    PageInfo<CardOrderResp> getCardOrderList(ListCardOrderReq req);

   /*
    * 更新卡支付状态
    */
   void updateCardPaymentStatus(String orderNo, Long storeId, Long tenantId, Long amount);


   /*
    * 卡详情
    */
    CardOrderDetailResp queryCardOrder(QueryCardOrderReq req);


    List<CustomerCardOrder> getCustomersForCusGroup(Long storeId, Date beginTime);

    Map<String, List<ComputeMarktingCustomerForReportResp>> ComputeMarktingCustomerForReport(Long storeId, Long tenantId);
}
