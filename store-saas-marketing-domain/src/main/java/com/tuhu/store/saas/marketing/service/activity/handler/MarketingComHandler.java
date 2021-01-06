package com.tuhu.store.saas.marketing.service.activity.handler;


import com.tuhu.store.saas.marketing.request.MarketingAddReq;

import java.util.List;
/**
 * <p>
 *  营销活动
 * </p>
 *
 * @author yangshengyong
 * @since 2020-12-10
 */
public interface MarketingComHandler {
    String getMarketingMethod();

    void execute(MarketingAddReq addReq, List<String> customerIds);
}
