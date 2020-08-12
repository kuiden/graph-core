package com.tuhu.store.saas.marketing.service.customergroup.filter;

import com.google.common.collect.Lists;
import com.tuhu.store.saas.marketing.remote.order.ServiceOrderClient;
import com.tuhu.store.saas.marketing.service.customergroup.AbstractFactorFilter;
import com.tuhu.store.saas.marketing.util.SpringApplicationContextUtil;
import com.tuhu.store.saas.order.request.serviceorder.ListCustomerInfoReq;
import com.tuhu.store.saas.order.response.serviceorder.ListCustomerInfoResp;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
@Data
@Slf4j
public class ConsumerServerListFilter extends AbstractFactorFilter {
    /**
     * 消费指定服务
     */
    private List<String> serverIdList;

    /**
     * 最近几天
     */
    private Integer recentDay;

    /**
     * 门店id
     */
    private Long storeId;

    @Override
    public boolean isOpen() {
        return  recentDay!=null && CollectionUtils.isNotEmpty(serverIdList);
    }

    @Override
    public List<String> filterSelf() {
        ServiceOrderClient serviceOrderClient = SpringApplicationContextUtil.getBean(ServiceOrderClient.class);
        ListCustomerInfoReq req = new ListCustomerInfoReq();
        req.setStoreId(storeId);
        req.setRecentDays(recentDay);


        req.setGoodsList(serverIdList);
        //有消费记录的客户
        List<String> customerIdList = Lists.newArrayList();
        List<ListCustomerInfoResp> customers = serviceOrderClient.listCustomerInfoForGoods(req).getData();
        if(customers!=null){
            for(ListCustomerInfoResp customerInfoResp : customers){
                if(StringUtils.isNotBlank(customerInfoResp.getCostumerId()) && !customerIdList.contains(customerInfoResp.getCostumerId())){
                    customerIdList.add(customerInfoResp.getCostumerId());
                }
            }
        }
        return customerIdList;
    }
}
