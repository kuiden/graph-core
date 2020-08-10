package com.tuhu.store.saas.marketing.service.customergroup.filter;

import com.tuhu.store.saas.crm.dto.CustomerDTO;
import com.tuhu.store.saas.crm.vo.CustomerVO;
import com.tuhu.store.saas.marketing.remote.crm.CustomerClient;
import com.tuhu.store.saas.marketing.service.customergroup.AbstractFactorFilter;
import com.tuhu.store.saas.marketing.util.SpringApplicationContextUtil;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
@Data
@Slf4j
public class AllCustomerFilter extends AbstractFactorFilter {

    private Long storeId;

    @Override
    public boolean isOpen() {
        return storeId!=null;
    }

    @Override
    public List<String> filterSelf() {
        List<String> resultList = new ArrayList<>();
        CustomerClient customerClient = SpringApplicationContextUtil.getBean(CustomerClient.class);
        CustomerVO customerVO = new CustomerVO();
        customerVO.setStoreId(storeId);
        List<CustomerDTO> customerDTOS = customerClient.listCustomer(customerVO).getData();
        if(CollectionUtils.isNotEmpty(customerDTOS)){
            for(CustomerDTO customerDTO :customerDTOS){
                if(StringUtils.isNotBlank(customerDTO.getId()) && resultList.contains(customerDTO.getId())){
                    resultList.add(customerDTO.getId());
                }
            }

        }
        return resultList;
    }
}
