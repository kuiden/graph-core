package com.tuhu.store.saas.marketing.service.customergroup.filter;

import com.google.common.collect.Lists;
import com.tuhu.store.saas.marketing.dataobject.CustomerCardOrder;
import com.tuhu.store.saas.marketing.remote.order.ServiceOrderClient;
import com.tuhu.store.saas.marketing.service.customergroup.AbstractFactorFilter;
import com.tuhu.store.saas.marketing.util.SpringApplicationContextUtil;
import com.tuhu.store.saas.order.request.serviceorder.ListCustomerInfoReq;
import com.tuhu.store.saas.order.response.serviceorder.ListCustomerInfoResp;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;

import java.util.List;

/**
 * 最近多少天有消费行为的客户id过滤
 */
@Data
@Slf4j
public class HasCustomerBehaviorFilter extends AbstractFactorFilter {

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
                    hasBehavCus.add(customerInfoResp.getCostumerId());
                }
            }
        }
        //有开卡记录的用户
        List<CustomerCardOrder> CustomerCardOrderList = getCustomersForCusGroup(storeId, recentDay);
        if(CollectionUtils.isNotEmpty(CustomerCardOrderList)){
            for(CustomerCardOrder customerCardOrder : CustomerCardOrderList){
                if(!hasBehavCus.contains(customerCardOrder.getCustomerId())) {
                    hasBehavCus.add(customerCardOrder.getCustomerId());
                }
            }
        }
        return hasBehavCus;
    }

    @Override
    public boolean isOpen() {
        return recentDay!=null;
    }
}
