package com.tuhu.store.saas.marketing.service.customergroup.filter;

import com.google.common.collect.Lists;
import com.tuhu.store.saas.crm.dto.CustomerDTO;
import com.tuhu.store.saas.crm.vo.CustomerVO;
import com.tuhu.store.saas.marketing.remote.crm.CustomerClient;
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
 * 最近多少天无消费行为的客户id过滤
 */
@Data
@Slf4j
public class NoCustomerBehaviorFilter extends AbstractFactorFilter {

    /**
     * 最近几天无消费行为
     */
    private Integer recentDay;

    /**
     * 门店id
     */
    private Long storeId;

    public List<String> filterSelf(){
        CustomerClient customerClient = SpringApplicationContextUtil.getBean(CustomerClient.class);
        CustomerVO customerVO = new CustomerVO();
        customerVO.setStoreId(storeId);
        List<CustomerDTO> customerDTOS = customerClient.listCustomer(customerVO).getData();
        if(customerDTOS==null||customerDTOS.size()<=0){
            return null;
        }
        ServiceOrderClient serviceOrderClient = SpringApplicationContextUtil.getBean(ServiceOrderClient.class);
        ListCustomerInfoReq req = new ListCustomerInfoReq();
        req.setStoreId(storeId);
        req.setRecentDays(recentDay);
        //有消费记录的客户
        List<ListCustomerInfoResp> customers = serviceOrderClient.listCustomerInfos(req).getData();
        List<String> hasBehavCus = Lists.newArrayList();
        if(CollectionUtils.isNotEmpty(customers)){
            for(ListCustomerInfoResp customerInfoResp : customers){
                hasBehavCus.add(customerInfoResp.getCostumerId());
            }
        }
        List<String> result = Lists.newArrayList();

        if(CollectionUtils.isNotEmpty(hasBehavCus)){

        }
        for(CustomerDTO customerDTO : customerDTOS){
            if((hasBehavCus==null|| hasBehavCus!=null&&!hasBehavCus.contains(customerDTO.getId()))
                    &&!result.contains(customerDTO.getId())){
                //无消费记录的客户列表
                result.add(customerDTO.getId());
            }
        }
        return result;
    }

    @Override
    public boolean isOpen() {
        return recentDay!=null;
    }
}
