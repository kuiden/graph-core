package com.tuhu.store.saas.marketing.service.customergroup.filter;

import com.google.common.collect.Lists;
import com.tuhu.store.saas.marketing.remote.order.ServiceOrderClient;
import com.tuhu.store.saas.marketing.service.customergroup.AbstractFactorFilter;
import com.tuhu.store.saas.marketing.util.SpringApplicationContextUtil;
import com.tuhu.store.saas.order.request.serviceorder.ListCustomerInfoReq;
import com.tuhu.store.saas.order.response.serviceorder.ListCustomerInfoResp;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * 最近多少天有消费行为次数的客户id过滤
 */
@Data
@Slf4j
public class HasCustomerBehaviorMoneyFilter extends AbstractFactorFilter {

    /**
     * 少于多少金额
     */
    private Long lessThanMoney;

    /**
     * 多余多少次数
     */
    private Long greaterThanMoney;

    /**
     * 最近几天有消费行为
     */
    private Integer recentDay;

    /**
     * 门店id
     */
    private Long storeId;

    public List<String> filterSelf(){
        ServiceOrderClient serviceOrderClient = SpringApplicationContextUtil.getBean(ServiceOrderClient.class);
        ListCustomerInfoReq req = new ListCustomerInfoReq();
        req.setStoreId(storeId);
        req.setRecentDays(recentDay);
        //有消费记录的客户
        List<String> hasBehavCus = Lists.newArrayList();
        List<ListCustomerInfoResp> customers = serviceOrderClient.listCustomerInfos(req).getData();
        if(customers!=null){
            for(ListCustomerInfoResp customerInfoResp : customers){
                if(!hasBehavCus.contains(customerInfoResp.getCostumerId())){
                    Long orderActualAmount = customerInfoResp.getOrderActualAmount();
                    if(lessThanMoney!=null&&greaterThanMoney!=null){
                        if(orderActualAmount<greaterThanMoney||orderActualAmount>lessThanMoney){
                            continue;
                        }
                        //大于最少，少于最大
                        hasBehavCus.add(customerInfoResp.getCostumerId());
                    }else if(lessThanMoney!=null){
                        if(orderActualAmount>lessThanMoney){
                            continue;
                        }
                        //少于最大
                        hasBehavCus.add(customerInfoResp.getCostumerId());
                    }else if(greaterThanMoney!=null){
                        if(orderActualAmount<greaterThanMoney){
                            continue;
                        }
                        //多余最少
                        hasBehavCus.add(customerInfoResp.getCostumerId());
                    }
                }
            }
        }
        return hasBehavCus;
    }

    @Override
    public boolean isOpen() {
        return recentDay!=null&&!(lessThanMoney==null&&greaterThanMoney==null);
    }
}
