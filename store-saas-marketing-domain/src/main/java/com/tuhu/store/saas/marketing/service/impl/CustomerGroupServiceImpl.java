package com.tuhu.store.saas.marketing.service.impl;

import com.tuhu.store.saas.marketing.constant.CustomerGroupConstant;
import com.tuhu.store.saas.marketing.dataobject.CustomerGroupRule;
import com.tuhu.store.saas.marketing.dataobject.CustomerGroupRuleExample;
import com.tuhu.store.saas.marketing.dataobject.StoreCustomerGroupRelation;
import com.tuhu.store.saas.marketing.dataobject.StoreCustomerGroupRelationExample;
import com.tuhu.store.saas.marketing.exception.MarketingException;
import com.tuhu.store.saas.marketing.mysql.marketing.write.dao.CustomerGroupRuleMapper;
import com.tuhu.store.saas.marketing.mysql.marketing.write.dao.StoreCustomerGroupRelationMapper;
import com.tuhu.store.saas.marketing.request.CustomerGroupReq;
import com.tuhu.store.saas.marketing.response.CustomerGroupResp;
import com.tuhu.store.saas.marketing.response.dto.CustomerGroupDto;
import com.tuhu.store.saas.marketing.response.dto.CustomerGroupRuleAttributeDto;
import com.tuhu.store.saas.marketing.response.dto.CustomerGroupRuleDto;
import com.tuhu.store.saas.marketing.service.ICustomerGroupService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;


@Service
@Slf4j
public class CustomerGroupServiceImpl implements ICustomerGroupService {
    @Autowired
    private StoreCustomerGroupRelationMapper storeCustomerGroupRelationMapper;
    @Autowired
    private CustomerGroupRuleMapper customerGroupRuleMapper;

    @Override
    public void saveCustomerGroup(CustomerGroupReq req){
        CustomerGroupDto customerGroupDto = transferCustomerGroupDto(req);
        if(CollectionUtils.isEmpty(customerGroupDto.getCustomerGroupRuleReqList())){
            throw new MarketingException("请填写特征信息");
        }
        if(customerGroupDto.getId()==null){//新增
            customerGroupDto.setCreateUser(req.getCreateUser());
            customerGroupDto.setCreateTime(new Date());
            StoreCustomerGroupRelation record = new StoreCustomerGroupRelation();
            BeanUtils.copyProperties(customerGroupDto,record);
            int relationId = storeCustomerGroupRelationMapper.insertSelective(record);
            if(relationId>0) {
                List<CustomerGroupRuleDto> subRuleList = customerGroupDto.getCustomerGroupRuleReqList();
                List<CustomerGroupRule> customerGroupRuleList = new ArrayList<>();
                for (CustomerGroupRuleDto customerGroupRuleDto : subRuleList) {
                    List<CustomerGroupRuleAttributeDto> attributeReqList = customerGroupRuleDto.getAttributeReqList();
                    for (CustomerGroupRuleAttributeDto customerGroupRuleAttributeDto : attributeReqList) {
                        CustomerGroupRule customerGroupRule = new CustomerGroupRule();
                        customerGroupRule.setAttributeName(customerGroupRuleAttributeDto.getAttribute());
                        customerGroupRule.setAttributeValue(customerGroupRuleAttributeDto.getAttributeValue());
                        customerGroupRule.setCompareOperator(customerGroupRuleAttributeDto.getCompareOpertor());
                        customerGroupRule.setStatus("1");
                        customerGroupRule.setCgRuleFactor(customerGroupRuleDto.getCgRuleFactor());
                        customerGroupRule.setCgRuleName(customerGroupRuleDto.getCgRuleName());
                        customerGroupRule.setGroupId(Long.valueOf(relationId));
                        customerGroupRule.setStoreId(customerGroupDto.getStoreId());
                        customerGroupRuleList.add(customerGroupRule);
                    }
                }
                customerGroupRuleMapper.insertBatch(customerGroupRuleList);
            }
        }else{//更新

        }
    }


    public CustomerGroupResp getCustomerGroupDetail(CustomerGroupReq req) throws ParseException {
        CustomerGroupResp customerGroupResp = null;
        StoreCustomerGroupRelationExample storeCustomerGroupRelationExample = new StoreCustomerGroupRelationExample();
        StoreCustomerGroupRelationExample.Criteria criteria = storeCustomerGroupRelationExample.createCriteria();
        criteria.andGroupIdEqualTo(req.getId());
        criteria.andStoreIdEqualTo(req.getStoreId());
        List<StoreCustomerGroupRelation> storeCustomerGroupRelations = storeCustomerGroupRelationMapper.selectByExample(storeCustomerGroupRelationExample);
        if(CollectionUtils.isNotEmpty(storeCustomerGroupRelations)){
            StoreCustomerGroupRelation storeCustomerGroupRelation = storeCustomerGroupRelations.get(0);
            customerGroupResp = new CustomerGroupResp();
            customerGroupResp.setConsumerGroupName(storeCustomerGroupRelation.getGroupName());
            customerGroupResp.setId(storeCustomerGroupRelation.getId());
            customerGroupResp.setStoreId(storeCustomerGroupRelation.getStoreId());
        }
        if(customerGroupResp!=null && customerGroupResp.getId()!=null){
            CustomerGroupRuleExample customerGroupRuleExample = new CustomerGroupRuleExample();
            CustomerGroupRuleExample.Criteria criteria1 = customerGroupRuleExample.createCriteria();
            criteria1.andGroupIdEqualTo(customerGroupResp.getId());
            criteria1.andStoreIdEqualTo(customerGroupResp.getStoreId());
            criteria1.andStatausEqualTo("1");
            List<CustomerGroupRule> customerGroupRuleList = customerGroupRuleMapper.selectByExample(customerGroupRuleExample);
            Map<String, Map<String,String>>  amap = new HashMap<>();
            if(CollectionUtils.isNotEmpty(customerGroupRuleList)){
                for(CustomerGroupRule customerGroupRule : customerGroupRuleList){
                    if(amap.get(customerGroupRule.getCgRuleFactor())==null){
                        Map<String,String> subMap = new HashMap<>();
                        subMap.put(customerGroupRule.getAttributeName(),customerGroupRule.getAttributeValue());
                        amap.put(customerGroupRule.getCgRuleFactor(),subMap);
                    }else{
                        Map<String,String> subMap = amap.get(customerGroupRule.getCgRuleFactor());
                        subMap.put(customerGroupRule.getAttributeName(),customerGroupRule.getAttributeValue());
                    }
                }
            }
            if(!amap.isEmpty() ){
                if(amap.get(CustomerGroupConstant.NO_CONSUMER_BEHAVIOR_FACTOR)!=null){
                     if(StringUtils.isNotBlank(amap.get(CustomerGroupConstant.NO_CONSUMER_BEHAVIOR_FACTOR).get(CustomerGroupConstant.RECENT_DAYS))){
                        customerGroupResp.setNoConsumerDay(Long.valueOf(amap.get(CustomerGroupConstant.NO_CONSUMER_BEHAVIOR_FACTOR).get(CustomerGroupConstant.RECENT_DAYS)));
                    }
                }
                if(amap.get(CustomerGroupConstant.HAS_CONSUMER_FACTOR)!=null){
                    if(StringUtils.isNotBlank(amap.get(CustomerGroupConstant.HAS_CONSUMER_FACTOR).get(CustomerGroupConstant.RECENT_DAYS))) {
                        customerGroupResp.setHasConsumerDay(Long.valueOf(amap.get(CustomerGroupConstant.HAS_CONSUMER_FACTOR).get(CustomerGroupConstant.RECENT_DAYS)));
                    }
                }
                if(amap.get(CustomerGroupConstant.CONSUMER_TIME_FACTOR)!=null){
                    if(StringUtils.isNotBlank(amap.get(CustomerGroupConstant.CONSUMER_TIME_FACTOR).get(CustomerGroupConstant.RECENT_DAYS))) {
                        customerGroupResp.setConsumerTimeDay(Long.valueOf(amap.get(CustomerGroupConstant.CONSUMER_TIME_FACTOR).get(CustomerGroupConstant.RECENT_DAYS)));
                    }
                    if(StringUtils.isNotBlank(amap.get(CustomerGroupConstant.CONSUMER_TIME_FACTOR).get(CustomerGroupConstant.LEAST_TIME))) {
                        customerGroupResp.setConsumerLeastTime(Long.valueOf(amap.get(CustomerGroupConstant.CONSUMER_TIME_FACTOR).get(CustomerGroupConstant.LEAST_TIME)));
                    }
                    if(StringUtils.isNotBlank(amap.get(CustomerGroupConstant.CONSUMER_TIME_FACTOR).get(CustomerGroupConstant.MAX_TIME))) {
                        customerGroupResp.setConsumerMaxTime(Long.valueOf(amap.get(CustomerGroupConstant.CONSUMER_TIME_FACTOR).get(CustomerGroupConstant.MAX_TIME)));
                    }
                }

                if(amap.get(CustomerGroupConstant.CONSUMER_AMOUNT_FACTOR)!=null){
                    if(StringUtils.isNotBlank(amap.get(CustomerGroupConstant.CONSUMER_AMOUNT_FACTOR).get(CustomerGroupConstant.RECENT_DAYS))) {
                        customerGroupResp.setConsumerAmountDay(Long.valueOf(amap.get(CustomerGroupConstant.CONSUMER_AMOUNT_FACTOR).get(CustomerGroupConstant.RECENT_DAYS)));
                    }
                    if(StringUtils.isNotBlank(amap.get(CustomerGroupConstant.CONSUMER_AMOUNT_FACTOR).get(CustomerGroupConstant.LEAST_AMOUNT))) {
                        customerGroupResp.setConsumerLeastAmount(Long.valueOf(amap.get(CustomerGroupConstant.CONSUMER_AMOUNT_FACTOR).get(CustomerGroupConstant.LEAST_AMOUNT)));
                    }
                    if(StringUtils.isNotBlank(amap.get(CustomerGroupConstant.CONSUMER_AMOUNT_FACTOR).get(CustomerGroupConstant.MAX_AMOUNT))) {
                        customerGroupResp.setConsumerMaxAmount(Long.valueOf(amap.get(CustomerGroupConstant.CONSUMER_AMOUNT_FACTOR).get(CustomerGroupConstant.MAX_AMOUNT)));
                    }
                }
                if(amap.get(CustomerGroupConstant.CONSUMER_SERVER_FACTOR)!=null){
                    if(StringUtils.isNotBlank(amap.get(CustomerGroupConstant.CONSUMER_SERVER_FACTOR).get(CustomerGroupConstant.RECENT_DAYS))) {
                        customerGroupResp.setConsumerServeDay(Long.valueOf(amap.get(CustomerGroupConstant.CONSUMER_SERVER_FACTOR).get(CustomerGroupConstant.RECENT_DAYS)));
                    }
                    String serverArrayStr = amap.get(CustomerGroupConstant.CONSUMER_SERVER_FACTOR).get(CustomerGroupConstant.SPECIFIED_SERVER);
                    if(StringUtils.isNotBlank(serverArrayStr)){
                        List<String> serverIdList =  Arrays.asList(serverArrayStr.split(","));
                        customerGroupResp.setConsumerServeList(serverIdList);
                    }
                }
                if(amap.get(CustomerGroupConstant.CREATED_TIME_FACTOR)!=null){
                    SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    String createdTimeStartStr = amap.get(CustomerGroupConstant.CREATED_TIME_FACTOR).get(CustomerGroupConstant.CREATED_TIME_LEAST_DAY);
                    String createdTimeEndStr = amap.get(CustomerGroupConstant.CREATED_TIME_FACTOR).get(CustomerGroupConstant.CREATED_TIME_MAX_DAY);
                    if(StringUtils.isNotBlank(createdTimeStartStr)) {
                        customerGroupResp.setCreateDateStart(sf.parse(createdTimeStartStr));
                    }
                    if(StringUtils.isNotBlank(createdTimeEndStr)) {
                        customerGroupResp.setCreateDateEnd(sf.parse(createdTimeEndStr));
                    }
                }
                if(amap.get(CustomerGroupConstant.BRITHDAY_FACTOR)!=null){
                    SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    String brithdayStartStr = amap.get(CustomerGroupConstant.BRITHDAY_FACTOR).get(CustomerGroupConstant.BRITHDAY_LEAST_DAY);
                    String brithdayEndStr = amap.get(CustomerGroupConstant.BRITHDAY_FACTOR).get(CustomerGroupConstant.BRITHDAY_MAX_DAY);
                    if(StringUtils.isNotBlank(brithdayStartStr)){
                        customerGroupResp.setBrithdayStart(sf.parse(brithdayStartStr));
                    }
                    if(StringUtils.isNotBlank(brithdayEndStr)){
                        customerGroupResp.setBrithdayEnd(sf.parse(brithdayEndStr));
                    }

                }
                if(amap.get(CustomerGroupConstant.MAINTENANCE_FACTOR)!=null){
                    SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    String maintenanceStartStr = amap.get(CustomerGroupConstant.MAINTENANCE_FACTOR).get(CustomerGroupConstant.MAINTENANCE_LEAST_DAY);
                    String maintenanceEndStr = amap.get(CustomerGroupConstant.MAINTENANCE_FACTOR).get(CustomerGroupConstant.MAINTENANCE_MAX_DAY);
                    if(StringUtils.isNotBlank(maintenanceStartStr)){
                        customerGroupResp.setMaintenanceDateStart(sf.parse(maintenanceStartStr));
                    }
                    if(StringUtils.isNotBlank(maintenanceEndStr)){
                        customerGroupResp.setMaintenanceDateEnd(sf.parse(maintenanceEndStr));
                    }
                }



            }

        }


        return customerGroupResp;
    }


    private CustomerGroupDto transferCustomerGroupDto(CustomerGroupReq req) {

        CustomerGroupDto customerGroupDto = new CustomerGroupDto();
        customerGroupDto.setGroupName(req.getConsumerGroupName());
        customerGroupDto.setStoreId(req.getStoreId());
        customerGroupDto.setId(req.getId());
        List<CustomerGroupRuleDto> customerGroupRuleReqList = new ArrayList<>();
        if(req.getNoConsumerDay()!=null && req.getNoConsumerDay()>0){
            CustomerGroupRuleDto customerGroupRuleDto = pkgCustomerGroupRule(CustomerGroupConstant.NO_CONSUMER_BEHAVIOR_FACTOR,String.valueOf(req.getNoConsumerDay()),CustomerGroupConstant.RECENT_DAYS,"=");
            customerGroupRuleReqList.add(customerGroupRuleDto);
        }
        if(req.getHasConsumerDay()!=null && req.getHasConsumerDay()>0){
            CustomerGroupRuleDto customerGroupRuleDto = pkgCustomerGroupRule(CustomerGroupConstant.HAS_CONSUMER_FACTOR,String.valueOf(req.getHasConsumerDay()),CustomerGroupConstant.RECENT_DAYS,"=");
            customerGroupRuleReqList.add(customerGroupRuleDto);
        }
        if(req.getConsumerTimeDay()!=null && req.getConsumerTimeDay()>0 && ((req.getConsumerLeastTime()!=null && req.getConsumerLeastTime()>=0)|| (req.getConsumerMaxTime()!=null && req.getConsumerMaxTime()>0))){
            CustomerGroupRuleDto customerGroupRuleDto = pkgCustomerGroupRule(CustomerGroupConstant.CONSUMER_TIME_FACTOR,String.valueOf(req.getConsumerTimeDay()),CustomerGroupConstant.RECENT_DAYS,"=");
            if(req.getConsumerLeastTime()!=null && req.getConsumerLeastTime()>0){
                customerGroupRuleAddRuleAttribute(customerGroupRuleDto, String.valueOf(req.getConsumerLeastTime()),CustomerGroupConstant.LEAST_TIME,">=");
            }
            if(req.getConsumerMaxTime()!=null && req.getConsumerMaxTime()>0){
                customerGroupRuleAddRuleAttribute(customerGroupRuleDto, String.valueOf(req.getConsumerMaxTime()),CustomerGroupConstant.MAX_TIME,"<=");
            }
            customerGroupRuleReqList.add(customerGroupRuleDto);
        }
        if(req.getConsumerAmountDay()!=null && req.getConsumerAmountDay()>0 && ((req.getConsumerLeastAmount()!=null && req.getConsumerLeastAmount()>=0)|| (req.getConsumerMaxAmount()!=null && req.getConsumerMaxAmount()>0))){
            CustomerGroupRuleDto customerGroupRuleDto = pkgCustomerGroupRule(CustomerGroupConstant.CONSUMER_AMOUNT_FACTOR,String.valueOf(req.getConsumerAmountDay()),CustomerGroupConstant.RECENT_DAYS,"=");
            if(req.getConsumerLeastAmount()!=null && req.getConsumerLeastAmount()>0){
                customerGroupRuleAddRuleAttribute(customerGroupRuleDto, String.valueOf(req.getConsumerLeastAmount()),CustomerGroupConstant.LEAST_AMOUNT,">=");
            }
            if(req.getConsumerMaxAmount()!=null && req.getConsumerMaxAmount()>0){
                customerGroupRuleAddRuleAttribute(customerGroupRuleDto, String.valueOf(req.getConsumerMaxAmount()),CustomerGroupConstant.MAX_AMOUNT,"<=");
            }
            customerGroupRuleReqList.add(customerGroupRuleDto);
        }
        if (req.getConsumerServeDay() != null && req.getConsumerServeDay() > 0 && CollectionUtils.isNotEmpty(req.getConsumerServeList())) {
            CustomerGroupRuleDto customerGroupRuleDto = pkgCustomerGroupRule(CustomerGroupConstant.CONSUMER_SERVER_FACTOR,String.valueOf(req.getConsumerServeDay()),CustomerGroupConstant.RECENT_DAYS,"=");
            customerGroupRuleAddRuleAttribute(customerGroupRuleDto, StringUtils.join(req.getConsumerServeList().toArray(),","),CustomerGroupConstant.SPECIFIED_SERVER,"in");
            customerGroupRuleReqList.add(customerGroupRuleDto);
        }
        if(req.getCreateDateStart()!=null && req.getCreateDateEnd()!=null){
            SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            CustomerGroupRuleDto customerGroupRuleDto = pkgCustomerGroupRule(CustomerGroupConstant.CREATED_TIME_FACTOR,sf.format(req.getCreateDateStart()),CustomerGroupConstant.CREATED_TIME_LEAST_DAY,">=");
            customerGroupRuleAddRuleAttribute(customerGroupRuleDto, sf.format(req.getCreateDateEnd()),CustomerGroupConstant.CREATED_TIME_MAX_DAY,"<=");
            customerGroupRuleReqList.add(customerGroupRuleDto);
        }

        if(req.getBrithdayStart()!=null && req.getBrithdayEnd()!=null){
            SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            CustomerGroupRuleDto customerGroupRuleDto = pkgCustomerGroupRule(CustomerGroupConstant.BRITHDAY_FACTOR,sf.format(req.getBrithdayStart()),CustomerGroupConstant.BRITHDAY_LEAST_DAY,">=");
            customerGroupRuleAddRuleAttribute(customerGroupRuleDto, sf.format(req.getBrithdayEnd()),CustomerGroupConstant.BRITHDAY_MAX_DAY,"<=");
            customerGroupRuleReqList.add(customerGroupRuleDto);
        }
        if(req.getMaintenanceDateStart()!=null && req.getMaintenanceDateEnd()!=null){
            SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            CustomerGroupRuleDto customerGroupRuleDto = pkgCustomerGroupRule(CustomerGroupConstant.MAINTENANCE_FACTOR,sf.format(req.getMaintenanceDateStart()),CustomerGroupConstant.MAINTENANCE_LEAST_DAY,">=");
            customerGroupRuleAddRuleAttribute(customerGroupRuleDto, sf.format(req.getMaintenanceDateEnd()),CustomerGroupConstant.MAINTENANCE_MAX_DAY,"<=");
            customerGroupRuleReqList.add(customerGroupRuleDto);
        }
        customerGroupDto.setCustomerGroupRuleReqList(customerGroupRuleReqList);
        return customerGroupDto;
    }

    private CustomerGroupRuleDto pkgCustomerGroupRule(String customerFator,String attributeValue,String attribute,String compareOpertor) {
        CustomerGroupRuleDto customerGroupRuleDto = new CustomerGroupRuleDto();
        customerGroupRuleDto.setCgRuleFactor(customerFator);
        List<CustomerGroupRuleAttributeDto> attributeReqList = new ArrayList<>();
        CustomerGroupRuleAttributeDto customerGroupRuleAttributeDto = new CustomerGroupRuleAttributeDto();
        customerGroupRuleAttributeDto.setAttribute(attribute);
        customerGroupRuleAttributeDto.setAttributeValue(attributeValue);
        customerGroupRuleAttributeDto.setCompareOpertor(compareOpertor);
        attributeReqList.add(customerGroupRuleAttributeDto);
        customerGroupRuleDto.setAttributeReqList(attributeReqList);
        return customerGroupRuleDto;
    }

    private void customerGroupRuleAddRuleAttribute(CustomerGroupRuleDto customerGroupRuleDto,String attributeValue,String attribute,String compareOpertor){
        if(customerGroupRuleDto!=null){
            CustomerGroupRuleAttributeDto customerGroupRuleAttributeDto = new CustomerGroupRuleAttributeDto();
            customerGroupRuleAttributeDto.setAttribute(attribute);
            customerGroupRuleAttributeDto.setAttributeValue(attributeValue);
            customerGroupRuleAttributeDto.setCompareOpertor(compareOpertor);
            if(customerGroupRuleDto.getAttributeReqList()!=null){
                customerGroupRuleDto.getAttributeReqList().add(customerGroupRuleAttributeDto);
            }else{
                List<CustomerGroupRuleAttributeDto> attributeReqList = new ArrayList<>();
                attributeReqList.add(customerGroupRuleAttributeDto);
                customerGroupRuleDto.setAttributeReqList(attributeReqList);
            }
        }
    }


}
