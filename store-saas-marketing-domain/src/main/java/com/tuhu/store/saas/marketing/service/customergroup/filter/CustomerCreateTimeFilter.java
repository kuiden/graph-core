package com.tuhu.store.saas.marketing.service.customergroup.filter;

import com.google.common.collect.Lists;
import com.tuhu.store.saas.crm.dto.CustomerDTO;
import com.tuhu.store.saas.crm.vo.CustomerVO;
import com.tuhu.store.saas.marketing.remote.crm.CustomerClient;
import com.tuhu.store.saas.marketing.service.customergroup.AbstractFactorFilter;
import com.tuhu.store.saas.marketing.util.DateUtils;
import com.tuhu.store.saas.marketing.util.SpringApplicationContextUtil;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;
import java.util.List;

/**
 * 客户创建时间的客户id过滤
 */
@Data
@Slf4j
public class CustomerCreateTimeFilter extends AbstractFactorFilter {

    /**
     * 少于多少天
     */
    private Integer lessThanDay;

    /**
     * 多余多少天
     */
    private Integer greaterThanDay;

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
        List<String> result = Lists.newArrayList();
        for(CustomerDTO customerDTO : customerDTOS){
            Date createTime = customerDTO.getCreateTime();
            if(createTime==null){
                continue;
            }
            int days = DateUtils.getDiffDays(createTime,DateUtils.now());
            if(lessThanDay!=null&&days>lessThanDay){
                continue;
            }
            if(greaterThanDay!=null&&days<greaterThanDay){
                continue;
            }
            if(!result.contains(customerDTO.getId())){
                result.add(customerDTO.getId());
            }
        }
        return result;
    }

    @Override
    public boolean isOpen() {
        return !(lessThanDay==null&&greaterThanDay==null);
    }
}
