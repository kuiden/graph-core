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
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 最近多少天有消费金额的客户id过滤
 */
@Data
@Slf4j
public class HasCustomerBehaviorMoneyFilter extends AbstractFactorFilter {

    /**
     * 少于多少金额
     */
    private String lessThanMoney;

    /**
     * 多余多少金额
     */
    private String greaterThanMoney;

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
        Map<String,BigDecimal> amap = new HashMap<String,BigDecimal>();
        if(customers!=null){
            for(ListCustomerInfoResp customerInfoResp : customers){
                if(!hasBehavCus.contains(customerInfoResp.getCostumerId())){
                    Long orderActualAmount = customerInfoResp.getOrderActualAmount();
                    BigDecimal decimalAmout = new BigDecimal(orderActualAmount).divide(new BigDecimal(100));
                    amap.put(customerInfoResp.getCostumerId(),decimalAmout);
                }
            }
        }
        //有开卡记录的用户
        List<CustomerCardOrder> CustomerCardOrderList = getCustomersForCusGroup(storeId, recentDay);
        if(CollectionUtils.isNotEmpty(CustomerCardOrderList)){
            for(CustomerCardOrder customerCardOrder : CustomerCardOrderList){
                if(!hasBehavCus.contains(customerCardOrder.getCustomerId())){
                    BigDecimal carAmount = customerCardOrder.getCarAmount();
                    if(amap.get(customerCardOrder.getCustomerId())==null) {
                        amap.put(customerCardOrder.getCustomerId(), carAmount);
                    }else{
                        amap.put(customerCardOrder.getCustomerId(), carAmount.add(amap.get(customerCardOrder.getCustomerId())));
                    }
                }
            }
        }

        for (Map.Entry<String, BigDecimal> entry : amap.entrySet()) {
            if(StringUtils.isNotBlank(lessThanMoney) && (new BigDecimal(lessThanMoney).compareTo(entry.getValue())<0)){
                continue;
            }
            if(StringUtils.isNotBlank(greaterThanMoney) && (new BigDecimal(greaterThanMoney).compareTo(entry.getValue())>0)){
                continue;
            }
            hasBehavCus.add(entry.getKey());
        }

        return hasBehavCus;
    }

    @Override
    public boolean isOpen() {
        return recentDay!=null&&!(lessThanMoney==null&&greaterThanMoney==null);
    }
}
