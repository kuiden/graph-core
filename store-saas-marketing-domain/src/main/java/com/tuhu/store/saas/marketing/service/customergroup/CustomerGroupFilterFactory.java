package com.tuhu.store.saas.marketing.service.customergroup;

import com.google.common.collect.Lists;
import com.tuhu.store.saas.marketing.constant.CustomerGroupConstant;
import com.tuhu.store.saas.marketing.response.dto.CustomerGroupDto;
import com.tuhu.store.saas.marketing.response.dto.CustomerGroupRuleAttributeDto;
import com.tuhu.store.saas.marketing.response.dto.CustomerGroupRuleDto;
import com.tuhu.store.saas.marketing.service.customergroup.filter.*;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.List;

/**
 * 根据过滤条件生成过滤器
 */
public class CustomerGroupFilterFactory {

    /**
     * 根据客户groupDto生成filter
     * 来过滤客户群id
     * @param customerGroupDto
     * @return
     */
    public static CustomerGroupFactorFilter createFilter(CustomerGroupDto customerGroupDto){
        if(customerGroupDto==null||customerGroupDto.getStoreId()==null||customerGroupDto.getCustomerGroupRuleReqList()==null||customerGroupDto.getCustomerGroupRuleReqList().size()<=0){
            return null;
        }
        List<AbstractFactorFilter> cugFilters = Lists.newArrayList();
        Long storeId = customerGroupDto.getStoreId();
        List<CustomerGroupRuleDto> customerGroupRuleDtos = customerGroupDto.getCustomerGroupRuleReqList();
        for(CustomerGroupRuleDto customerGroupRuleDto : customerGroupRuleDtos){
            String cgrule = customerGroupRuleDto.getCgRuleFactor();
            List<CustomerGroupRuleAttributeDto> attributeReqList = customerGroupRuleDto.getAttributeReqList();
            if(CustomerGroupConstant.NO_CONSUMER_BEHAVIOR_FACTOR.equalsIgnoreCase(cgrule)){
                //无消费行为
                NoCustomerBehaviorFilter customerBehaviorFilter = new NoCustomerBehaviorFilter();
                customerBehaviorFilter.setStoreId(storeId);
                for(CustomerGroupRuleAttributeDto attributeDto : attributeReqList){
                    if(CustomerGroupConstant.RECENT_DAYS.equalsIgnoreCase(attributeDto.getAttribute())){
                        customerBehaviorFilter.setRecentDay(Integer.valueOf(attributeDto.getAttributeValue()));
                    }
                }
                cugFilters.add(customerBehaviorFilter);
            }else if(CustomerGroupConstant.HAS_CONSUMER_FACTOR.equalsIgnoreCase(cgrule)){
                //有消费行为
                HasCustomerBehaviorFilter customerBehaviorFilter = new HasCustomerBehaviorFilter();
                customerBehaviorFilter.setStoreId(storeId);
                for(CustomerGroupRuleAttributeDto attributeDto : attributeReqList){
                    if(CustomerGroupConstant.RECENT_DAYS.equalsIgnoreCase(attributeDto.getAttribute())){
                        customerBehaviorFilter.setRecentDay(Integer.valueOf(attributeDto.getAttributeValue()));
                    }
                }
                cugFilters.add(customerBehaviorFilter);
            }else if(CustomerGroupConstant.CONSUMER_TIME_FACTOR.equalsIgnoreCase(cgrule)){
                //有消费行为次数限制
                HasCustomerBehaviorTimesFilter customerBehaviorFilter = new HasCustomerBehaviorTimesFilter();
                customerBehaviorFilter.setStoreId(storeId);
                for(CustomerGroupRuleAttributeDto attributeDto : attributeReqList){
                    if(CustomerGroupConstant.LEAST_TIME.equalsIgnoreCase(attributeDto.getAttribute())){
                        customerBehaviorFilter.setGreaterThanTimes(Long.valueOf(attributeDto.getAttributeValue()));
                    }else if(CustomerGroupConstant.MAX_TIME.equalsIgnoreCase(attributeDto.getAttribute())){
                        customerBehaviorFilter.setLessThanTimes(Long.valueOf(attributeDto.getAttributeValue()));
                    }else if(CustomerGroupConstant.RECENT_DAYS.equalsIgnoreCase(attributeDto.getAttribute())){
                        customerBehaviorFilter.setRecentDay(Integer.valueOf(attributeDto.getAttributeValue()));
                    }
                }
                cugFilters.add(customerBehaviorFilter);
            }else if(CustomerGroupConstant.CONSUMER_AMOUNT_FACTOR.equalsIgnoreCase(cgrule)){
                //有消费行为金额限制
                HasCustomerBehaviorMoneyFilter customerBehaviorFilter = new HasCustomerBehaviorMoneyFilter();
                customerBehaviorFilter.setStoreId(storeId);
                for(CustomerGroupRuleAttributeDto attributeDto : attributeReqList){
                    if(CustomerGroupConstant.LEAST_AMOUNT.equalsIgnoreCase(attributeDto.getAttribute())){
                        customerBehaviorFilter.setGreaterThanMoney(Long.valueOf(attributeDto.getAttributeValue()));
                    }else if(CustomerGroupConstant.MAX_AMOUNT.equalsIgnoreCase(attributeDto.getAttribute())){
                        customerBehaviorFilter.setLessThanMoney(Long.valueOf(attributeDto.getAttributeValue()));
                    }else if(CustomerGroupConstant.RECENT_DAYS.equalsIgnoreCase(attributeDto.getAttribute())){
                        customerBehaviorFilter.setRecentDay(Integer.valueOf(attributeDto.getAttributeValue()));
                    }
                }
                cugFilters.add(customerBehaviorFilter);
            }else if(CustomerGroupConstant.CREATED_TIME_FACTOR.equalsIgnoreCase(cgrule)){
                //创建时间过滤
                CustomerCreateTimeFilter customerCreateTimeFilter = new CustomerCreateTimeFilter();
                customerCreateTimeFilter.setStoreId(storeId);
                for(CustomerGroupRuleAttributeDto attributeDto : attributeReqList){
                    if(CustomerGroupConstant.CREATED_TIME_LEAST_DAY.equalsIgnoreCase(attributeDto.getAttribute())){
                        customerCreateTimeFilter.setGreaterThanDay(Integer.valueOf(attributeDto.getAttributeValue()));
                    }else if(CustomerGroupConstant.CREATED_TIME_MAX_DAY.equalsIgnoreCase(attributeDto.getAttribute())){
                        customerCreateTimeFilter.setLessThanDay(Integer.valueOf(attributeDto.getAttributeValue()));
                    }
                }
                cugFilters.add(customerCreateTimeFilter);
            }else if(CustomerGroupConstant.BRITHDAY_FACTOR.equalsIgnoreCase(cgrule)){
                //生日过滤
                CustomerBirthdayFilter birthdayFilter = new CustomerBirthdayFilter();
                birthdayFilter.setStoreId(storeId);
                for(CustomerGroupRuleAttributeDto attributeDto : attributeReqList){
                    if(CustomerGroupConstant.BRITHDAY_LEAST_MONTH.equalsIgnoreCase(attributeDto.getAttribute())){
                        birthdayFilter.setLeastMonth(Integer.valueOf(attributeDto.getAttributeValue()));
                    }else if(CustomerGroupConstant.BRITHDAY_MAX_MONTH.equalsIgnoreCase(attributeDto.getAttribute())){
                        birthdayFilter.setMaxMonth(Integer.valueOf(attributeDto.getAttributeValue()));
                    }
                }
                cugFilters.add(birthdayFilter);
            }else if(CustomerGroupConstant.MAINTENANCE_FACTOR.equalsIgnoreCase(cgrule)){

                MaintenanceDayFilter maintenanceDayFilter = new MaintenanceDayFilter();
                maintenanceDayFilter.setStoreId(storeId);
                for(CustomerGroupRuleAttributeDto attributeDto : attributeReqList){
                    if(CustomerGroupConstant.MAINTENANCE_LEAST_DAY.equalsIgnoreCase(attributeDto.getAttribute())){
                        maintenanceDayFilter.setDayStart(Integer.valueOf(attributeDto.getAttributeValue()));
                    }else if(CustomerGroupConstant.BRITHDAY_MAX_MONTH.equalsIgnoreCase(attributeDto.getAttribute())){
                        maintenanceDayFilter.setDayEnd(Integer.valueOf(attributeDto.getAttributeValue()));
                    }
                }
                cugFilters.add(maintenanceDayFilter);
                //保养过滤

            }else if(CustomerGroupConstant.CONSUMER_SERVER_FACTOR.equalsIgnoreCase(cgrule)){
                //指定服务过滤
                ConsumerServerListFilter consumerServerListFilter = new ConsumerServerListFilter();
                consumerServerListFilter.setStoreId(storeId);
                for(CustomerGroupRuleAttributeDto attributeDto : attributeReqList){
                    if(CustomerGroupConstant.RECENT_DAYS.equalsIgnoreCase(attributeDto.getAttribute())){
                        consumerServerListFilter.setRecentDay(Integer.valueOf(attributeDto.getAttributeValue()));
                    }
                    if(CustomerGroupConstant.SPECIFIED_SERVER.equalsIgnoreCase(attributeDto.getAttribute())){
                        String serverIds = attributeDto.getAttributeValue();
                        if(StringUtils.isNotBlank(serverIds)){
                            String[] serverIdArray = serverIds.split(",");
                            List<String> serverIdList = Arrays.asList(serverIdArray);
                            consumerServerListFilter.setServerIdList(serverIdList);
                        }
                    }
                }
                cugFilters.add(consumerServerListFilter);
            }
        }
        return getFinalGroupFilter(cugFilters);
    }

    /**
     * 组织过滤客户id
     * @param cugFilters
     * @return
     */
    private static CustomerGroupFactorFilter getFinalGroupFilter(List<AbstractFactorFilter> cugFilters){
        if(cugFilters==null||cugFilters.size()<=0){
            return null;
        }
        AbstractFactorFilter filter = cugFilters.get(0);
        for(int i=1;i<cugFilters.size();i++){
            cugFilters.get(i-1).setFilter(cugFilters.get(i));
        }
        return filter;
    }
}
