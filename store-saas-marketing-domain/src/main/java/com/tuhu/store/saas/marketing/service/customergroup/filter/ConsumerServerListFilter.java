package com.tuhu.store.saas.marketing.service.customergroup.filter;

import com.google.common.collect.Lists;
import com.tuhu.boot.common.facade.BizBaseResponse;
import com.tuhu.store.saas.dto.product.ServiceGoodDTO;
import com.tuhu.store.saas.marketing.remote.order.ServiceOrderClient;
import com.tuhu.store.saas.marketing.remote.product.StoreProductClient;
import com.tuhu.store.saas.marketing.service.customergroup.AbstractFactorFilter;
import com.tuhu.store.saas.marketing.util.SpringApplicationContextUtil;
import com.tuhu.store.saas.order.request.serviceorder.ListCustomerInfoReq;
import com.tuhu.store.saas.order.response.serviceorder.ListCustomerInfoResp;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Data
@Slf4j
public class ConsumerServerListFilter extends AbstractFactorFilter {
    /**
     * 消费指定服务
     */
    private List<String> serverCodeList;

    /**
     * 最近几天
     */
    private Integer recentDay;

    /**
     * 门店id
     */
    private Long storeId;

    private Long tenantId;

    @Override
    public boolean isOpen() {
        return  recentDay!=null && CollectionUtils.isNotEmpty(serverCodeList) && storeId!=null && tenantId!=null;
    }

    @Override
    public List<String> filterSelf() {
        ServiceOrderClient serviceOrderClient = SpringApplicationContextUtil.getBean(ServiceOrderClient.class);
        ListCustomerInfoReq req = new ListCustomerInfoReq();
        req.setStoreId(storeId);
        req.setRecentDays(recentDay);
        List<String> customerIdList = Lists.newArrayList();
        //根据goodCode查询goodId
        StoreProductClient storeProductClient = SpringApplicationContextUtil.getBean(StoreProductClient.class);
        List<ServiceGoodDTO> serviceGoodDTOList = storeProductClient.queryBatchGoods(serverCodeList, storeId, tenantId, "").getData();
        if(CollectionUtils.isNotEmpty(serviceGoodDTOList)){
            List<String> goodIdList = serviceGoodDTOList.stream().map(x -> x.getId()).distinct().collect(Collectors.toList());
            req.setGoodsList(goodIdList);
            //消费了指定服务的客户
            List<ListCustomerInfoResp> customers = serviceOrderClient.listCustomerInfoForGoods(req).getData();
            if(customers!=null){
                for(ListCustomerInfoResp customerInfoResp : customers){
                    if(StringUtils.isNotBlank(customerInfoResp.getCostumerId()) && !customerIdList.contains(customerInfoResp.getCostumerId())){
                        customerIdList.add(customerInfoResp.getCostumerId());
                    }
                }
            }
        }
        return customerIdList;

    }
}
