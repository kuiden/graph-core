package com.tuhu.store.saas.marketing.service.customergroup;

import com.google.common.collect.Lists;
import com.tuhu.store.saas.marketing.dataobject.CustomerCardOrder;
import com.tuhu.store.saas.marketing.remote.crm.CustomerClient;
import com.tuhu.store.saas.marketing.service.ICardOrderService;
import com.tuhu.store.saas.marketing.util.DateUtils;
import com.tuhu.store.saas.marketing.util.SpringApplicationContextUtil;

import java.util.Date;
import java.util.List;

import static java.util.stream.Collectors.toList;

public abstract class AbstractFactorFilter implements CustomerGroupFactorFilter{

    private CustomerGroupFactorFilter filter;

    public CustomerGroupFactorFilter getFilter() {
        return filter;
    }

    public void setFilter(CustomerGroupFactorFilter filter) {
        this.filter = filter;
    }

    /**
     * 取两个集合的交集
     * @param list1
     * @param list2
     * @return
     */
    public List<String> getInterSections(List<String> list1,List<String> list2){
        return list1.stream().filter(item -> list2.contains(item)).collect(toList());
    }

    /**
     * 是否启用
     * 根据过滤条件自己判断
     * @return
     */
    public abstract boolean isOpen();

    /**
     * 过滤自身条件
     * @return
     */
    public abstract List<String> filterSelf();

    /**
     * 过滤逻辑
     * 1.根据条件判断是否启用自身过滤
     * 2.根据是否有前置过滤
     * 3.合并取交集
     * @return
     */
    @Override
    public List<String> filterProcess() {
        List<String> customerIds = Lists.newArrayList();
        if(isOpen()){
            customerIds = filterSelf();
            if(customerIds==null||customerIds.size()<=0){
                return customerIds;
            }
            //取交集
            if(getFilter()!=null){
                List<String> tempCustomerIds = getFilter().filterProcess();
                return getInterSections(customerIds,tempCustomerIds);
            }
        }else if(getFilter()!=null){
            return getFilter().filterProcess();
        }
        return customerIds;
    }

    /**
     * 查看近几天有开卡的用户列表
     * @param storeId
     * @param recentDay
     * @return
     */
    public List<CustomerCardOrder> getCustomersForCusGroup(Long storeId, Integer recentDay){
        ICardOrderService iCardOrderService = SpringApplicationContextUtil.getBean(ICardOrderService.class);
        Date dayBegin = DateUtils.getDayBegin();
        Date beginTime = DateUtils.getNextDay(dayBegin,1-recentDay);
        return iCardOrderService.getCustomersForCusGroup(storeId,beginTime);
    }

}
