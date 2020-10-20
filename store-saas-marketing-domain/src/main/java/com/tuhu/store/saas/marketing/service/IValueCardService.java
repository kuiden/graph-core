package com.tuhu.store.saas.marketing.service;

import com.github.pagehelper.PageInfo;
import com.tuhu.store.saas.marketing.request.valueCard.*;
import com.tuhu.store.saas.marketing.response.valueCard.CustomerValueCardDetailResp;
import com.tuhu.store.saas.marketing.response.valueCard.QueryValueCardListResp;
import com.tuhu.store.saas.marketing.response.valueCard.QueryValueCardRuleResp;
import com.tuhu.store.saas.marketing.response.valueCard.ValueCardChangeResp;

import java.math.BigDecimal;
import java.util.Map;

/**
 * @author wangyuqing
 * @since 2020/10/19 13:41
 */
public interface IValueCardService {

    AddValueCardRuleReq addValueCardRule(AddValueCardRuleReq req);

    QueryValueCardRuleResp queryValueCardRule(Long storeId, Long tenantId);

    Map<String, BigDecimal> queryTotalValue(Long storeId, Long tenantId);

    PageInfo<QueryValueCardListResp> queryDetailList(QueryValueCardListReq req);

    CustomerValueCardDetailResp customerValueCardDetail(CustomerValueCardDetailReq req);

    PageInfo<ValueCardChangeResp> customerValueCardChangeList(CustomerValueCardDetailReq req);

    Map<String,BigDecimal> customerValueCardAmount(CustomerValueCardDetailReq req);

    Boolean customerRechargeOrRefund(ValueCardRechargeOrRefundReq req);

    Boolean customerConsumption(ValueCardConsumptionReq req);

    PageInfo<ValueCardChangeResp> rechargeRecord(ValueCardChangeRecordReq req);

}
