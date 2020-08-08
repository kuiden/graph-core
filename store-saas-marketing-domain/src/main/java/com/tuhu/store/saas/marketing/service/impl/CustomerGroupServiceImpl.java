package com.tuhu.store.saas.marketing.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.tuhu.boot.common.facade.BizBaseResponse;
import com.tuhu.store.saas.dto.product.GoodsData;
import com.tuhu.store.saas.marketing.constant.CustomerGroupConstant;
import com.tuhu.store.saas.marketing.dataobject.*;
import com.tuhu.store.saas.marketing.exception.StoreSaasMarketingException;
import com.tuhu.store.saas.marketing.mysql.marketing.write.dao.CustomerGroupRuleMapper;
import com.tuhu.store.saas.marketing.mysql.marketing.write.dao.StoreCustomerGroupRelationMapper;
import com.tuhu.store.saas.marketing.remote.product.StoreProductClient;
import com.tuhu.store.saas.marketing.request.CalculateCustomerCountReq;
import com.tuhu.store.saas.marketing.request.CustomerGroupListReq;
import com.tuhu.store.saas.marketing.request.CustomerGroupReq;
import com.tuhu.store.saas.marketing.response.CustomerGroupResp;
import com.tuhu.store.saas.marketing.response.GoodsResp;
import com.tuhu.store.saas.marketing.response.dto.CustomerGroupDto;
import com.tuhu.store.saas.marketing.response.dto.CustomerGroupRuleAttributeDto;
import com.tuhu.store.saas.marketing.response.dto.CustomerGroupRuleDto;
import com.tuhu.store.saas.marketing.service.ICustomerGroupService;
import com.tuhu.store.saas.marketing.service.customergroup.CustomerGroupFilterFactory;
import com.tuhu.store.saas.vo.product.GoodsListVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static java.util.stream.Collectors.toList;


@Service
@Slf4j
public class CustomerGroupServiceImpl implements ICustomerGroupService {
    @Autowired
    private StoreCustomerGroupRelationMapper storeCustomerGroupRelationMapper;
    @Autowired
    private CustomerGroupRuleMapper customerGroupRuleMapper;

    @Autowired
    private StoreProductClient storeProductClient;
    @Override
    @Transactional
    public void saveCustomerGroup(CustomerGroupReq req){
        CustomerGroupDto customerGroupDto = transferCustomerGroupDto(req);
        if(CollectionUtils.isEmpty(customerGroupDto.getCustomerGroupRuleReqList())){
            throw new StoreSaasMarketingException("请填写特征信息");
        }
        Long id = null;
        if(customerGroupDto.getId()==null){//新增
            customerGroupDto.setCreateUser(req.getCreateUser());
            customerGroupDto.setCreateTime(new Date());
            StoreCustomerGroupRelation record = new StoreCustomerGroupRelation();
            BeanUtils.copyProperties(customerGroupDto,record);
            storeCustomerGroupRelationMapper.insertSelective(record);
            if(record.getId()>0) {
                addCustomerGroupRuleList(customerGroupDto, Long.valueOf(record.getId()));
            }
            id = record.getId();
        }else{//更新

            StoreCustomerGroupRelation storeCustomerGroupRelation = new StoreCustomerGroupRelation();
            storeCustomerGroupRelation.setUpdateUser(req.getCreateUser());
            storeCustomerGroupRelation.setUpdateTime(new Date());
            storeCustomerGroupRelation.setGroupName(customerGroupDto.getGroupName());
            storeCustomerGroupRelation.setId(customerGroupDto.getId());
            storeCustomerGroupRelationMapper.updateByPrimaryKeySelective(storeCustomerGroupRelation);
            CustomerGroupRule customerGroupRule = new CustomerGroupRule();
            customerGroupRule.setUpdateTime(new Date());
            customerGroupRule.setUpdateUser(req.getCreateUser());
            customerGroupRule.setStatus("0");
            //失效客群规则
            CustomerGroupRuleExample example = new CustomerGroupRuleExample();
            CustomerGroupRuleExample.Criteria criteria = example.createCriteria();
            criteria.andStatausEqualTo("1");
            criteria.andStoreIdEqualTo(customerGroupDto.getStoreId());
            criteria.andGroupIdEqualTo(customerGroupDto.getId());
            customerGroupRuleMapper.updateByExampleSelective(customerGroupRule,example);
            //新增客群规则
            addCustomerGroupRuleList(customerGroupDto, customerGroupDto.getId());
            id = customerGroupDto.getId();
        }
        //计算客群数量
        updateCustomerCountInfo(req.getStoreId(), id);

    }

    private void updateCustomerCountInfo(Long storeId, Long id) {
        CalculateCustomerCountReq calculateCustomerCountReq = new CalculateCustomerCountReq();
        calculateCustomerCountReq.setStoreId(storeId);
        List<Long> groupList = new ArrayList<>();
        groupList.add(id);
        calculateCustomerCountReq.setGroupList(groupList);
        List<String> customerIdList = this.calculateCustomerCount(calculateCustomerCountReq);
        StoreCustomerGroupRelation record = new StoreCustomerGroupRelation();
        record.setId(id);
        record.setCustomerCount(Long.valueOf(customerIdList.size()));
        storeCustomerGroupRelationMapper.updateByPrimaryKeySelective(record);
    }

    private void addCustomerGroupRuleList(CustomerGroupDto customerGroupDto, Long relationId) {
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
                customerGroupRule.setTenantId(customerGroupDto.getTenantId());
                customerGroupRule.setCreateUser(customerGroupDto.getCreateUser());
                customerGroupRule.setCreateTime(new Date());
                customerGroupRuleList.add(customerGroupRule);
            }
        }
        customerGroupRuleMapper.insertBatch(customerGroupRuleList);
    }

    @Override
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
            customerGroupResp.setTenantId(req.getTenantId());
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
                convertCustomerGroupResp(customerGroupResp, amap);
            }

        }
        return customerGroupResp;
    }
    @Override
    public PageInfo<StoreCustomerGroupRelation> getCustomerGroupList(CustomerGroupListReq req) {
        PageInfo<StoreCustomerGroupRelation> result = new PageInfo<>();
        PageHelper.startPage(req.getPageNum(), req.getPageSize());
        StoreCustomerGroupRelationExample example = new StoreCustomerGroupRelationExample();
        StoreCustomerGroupRelationExample.Criteria criteria = example.createCriteria();
        criteria.andStoreIdEqualTo(req.getStoreId());
        if(StringUtils.isNotBlank(req.getQuery())){
            criteria.andGroupNameLike("%".concat(req.getQuery()).concat("%"));
        }
        List<StoreCustomerGroupRelation> storeCustomerGroupRelations = storeCustomerGroupRelationMapper.selectByExample(example);
        result.setList(storeCustomerGroupRelations);
        return result;
    }

    private void convertCustomerGroupResp(CustomerGroupResp customerGroupResp, Map<String, Map<String, String>> amap) throws ParseException {
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
                List<GoodsResp> goodsResps = queryServerList(customerGroupResp.getStoreId(), customerGroupResp.getTenantId(), serverIdList);
                if(CollectionUtils.isNotEmpty(goodsResps)){
                    customerGroupResp.setConsumerServeList(goodsResps);
                }

            }
        }
        if(amap.get(CustomerGroupConstant.CREATED_TIME_FACTOR)!=null){
            //SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String createdTimeStartStr = amap.get(CustomerGroupConstant.CREATED_TIME_FACTOR).get(CustomerGroupConstant.CREATED_TIME_LEAST_DAY);
            String createdTimeEndStr = amap.get(CustomerGroupConstant.CREATED_TIME_FACTOR).get(CustomerGroupConstant.CREATED_TIME_MAX_DAY);
            if(StringUtils.isNotBlank(createdTimeStartStr)) {
                customerGroupResp.setCreateDateStart(Long.valueOf(createdTimeStartStr));
            }
            if(StringUtils.isNotBlank(createdTimeEndStr)) {
                customerGroupResp.setCreateDateEnd(Long.valueOf(createdTimeEndStr));
            }
        }
        if(amap.get(CustomerGroupConstant.BRITHDAY_FACTOR)!=null){
          //  SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String brithdayStartStr = amap.get(CustomerGroupConstant.BRITHDAY_FACTOR).get(CustomerGroupConstant.BRITHDAY_LEAST_MONTH);
            String brithdayEndStr = amap.get(CustomerGroupConstant.BRITHDAY_FACTOR).get(CustomerGroupConstant.BRITHDAY_MAX_MONTH);
            if(StringUtils.isNotBlank(brithdayStartStr)){
                customerGroupResp.setBrithdayStart(Long.valueOf(brithdayStartStr));
            }
            if(StringUtils.isNotBlank(brithdayEndStr)){
                customerGroupResp.setBrithdayEnd(Long.valueOf(brithdayEndStr));
            }

        }
        if(amap.get(CustomerGroupConstant.MAINTENANCE_FACTOR)!=null){
           // SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String maintenanceStartStr = amap.get(CustomerGroupConstant.MAINTENANCE_FACTOR).get(CustomerGroupConstant.MAINTENANCE_LEAST_DAY);
            String maintenanceEndStr = amap.get(CustomerGroupConstant.MAINTENANCE_FACTOR).get(CustomerGroupConstant.MAINTENANCE_MAX_DAY);
            if(StringUtils.isNotBlank(maintenanceStartStr)){
                customerGroupResp.setMaintenanceDateStart(Long.valueOf(maintenanceStartStr));
            }
            if(StringUtils.isNotBlank(maintenanceEndStr)){
                customerGroupResp.setMaintenanceDateEnd(Long.valueOf(maintenanceEndStr));
            }
        }
    }

    private List<GoodsResp> queryServerList(Long storeId,Long tenantId ,List<String> serverIdList) {
        //查询服务
        List<GoodsResp> goodsResps = null;
        GoodsListVO goodsVO = new GoodsListVO();
        goodsVO.setStoreId(storeId);
        goodsVO.setTenantId(tenantId);
        goodsVO.setGoodsIdSet( new HashSet<String>(serverIdList));
        BizBaseResponse<List<GoodsData>> goodsByIDListResponse = storeProductClient.getGoodsByIDList(goodsVO);
        if(goodsByIDListResponse!=null) {
            List<GoodsData> goodsDataList = goodsByIDListResponse.getData();
            if(CollectionUtils.isNotEmpty(goodsDataList)) {
                goodsResps = new ArrayList<>();
                for(GoodsData goodsData : goodsDataList){
                    GoodsResp goodsResp = new GoodsResp();
                    BeanUtils.copyProperties(goodsData,goodsResp);
                    goodsResp.setChecked(true);
                    goodsResps.add(goodsResp);
                }
              //  customerGroupResp.setConsumerServeList(goodsResps);
            }
        }
        return goodsResps;
    }


    private CustomerGroupDto transferCustomerGroupDto(CustomerGroupReq req) {

        CustomerGroupDto customerGroupDto = new CustomerGroupDto();
        customerGroupDto.setGroupName(req.getConsumerGroupName());
        customerGroupDto.setStoreId(req.getStoreId());
        customerGroupDto.setTenantId(req.getTenantId());
        customerGroupDto.setId(req.getId());
        StringBuffer sb = new StringBuffer();
        List<CustomerGroupRuleDto> customerGroupRuleReqList = new ArrayList<>();
        if(req.getNoConsumerDay()!=null && req.getNoConsumerDay()>0){
            CustomerGroupRuleDto customerGroupRuleDto = pkgCustomerGroupRule(CustomerGroupConstant.NO_CONSUMER_BEHAVIOR_FACTOR,String.valueOf(req.getNoConsumerDay()),CustomerGroupConstant.RECENT_DAYS,"=");
            customerGroupRuleReqList.add(customerGroupRuleDto);
            sb.append(req.getNoConsumerDay()+"天内无消费").append(";");
        }
        if(req.getHasConsumerDay()!=null && req.getHasConsumerDay()>0){
            CustomerGroupRuleDto customerGroupRuleDto = pkgCustomerGroupRule(CustomerGroupConstant.HAS_CONSUMER_FACTOR,String.valueOf(req.getHasConsumerDay()),CustomerGroupConstant.RECENT_DAYS,"=");
            customerGroupRuleReqList.add(customerGroupRuleDto);
            sb.append(req.getHasConsumerDay()+"天内消费过").append(";");
        }
        if(req.getConsumerTimeDay()!=null && req.getConsumerTimeDay()>0 && ((req.getConsumerLeastTime()!=null && req.getConsumerLeastTime()>=0)|| (req.getConsumerMaxTime()!=null && req.getConsumerMaxTime()>0))){
            CustomerGroupRuleDto customerGroupRuleDto = pkgCustomerGroupRule(CustomerGroupConstant.CONSUMER_TIME_FACTOR,String.valueOf(req.getConsumerTimeDay()),CustomerGroupConstant.RECENT_DAYS,"=");
            sb.append(req.getConsumerTimeDay()+"天内消费次数");
            boolean hasLeast =false;
            boolean hasMax =false;
            if(req.getConsumerLeastTime()!=null && req.getConsumerLeastTime()>0){
                customerGroupRuleAddRuleAttribute(customerGroupRuleDto, String.valueOf(req.getConsumerLeastTime()),CustomerGroupConstant.LEAST_TIME,">=");
                hasLeast = true;
            }
            if(req.getConsumerMaxTime()!=null && req.getConsumerMaxTime()>0){
                customerGroupRuleAddRuleAttribute(customerGroupRuleDto, String.valueOf(req.getConsumerMaxTime()),CustomerGroupConstant.MAX_TIME,"<=");
                hasMax = true;
            }
            customerGroupRuleReqList.add(customerGroupRuleDto);
            if(hasLeast && hasMax){
              sb.append(req.getConsumerLeastTime()).append("-").append(req.getConsumerMaxTime()).append("次;");
            }else if(hasLeast){
                sb.append(req.getConsumerLeastTime()).append("次以上;");
            }else{
                sb.append(req.getConsumerMaxTime()).append("次以下;");
            }
        }
        if(req.getConsumerAmountDay()!=null && req.getConsumerAmountDay()>0 && ((req.getConsumerLeastAmount()!=null && req.getConsumerLeastAmount()>=0)|| (req.getConsumerMaxAmount()!=null && req.getConsumerMaxAmount()>0))){
            CustomerGroupRuleDto customerGroupRuleDto = pkgCustomerGroupRule(CustomerGroupConstant.CONSUMER_AMOUNT_FACTOR,String.valueOf(req.getConsumerAmountDay()),CustomerGroupConstant.RECENT_DAYS,"=");
            sb.append(req.getConsumerAmountDay()+"天内消费金额");
            boolean hasLeast =false;
            boolean hasMax =false;
            if(req.getConsumerLeastAmount()!=null && req.getConsumerLeastAmount()>0){
                customerGroupRuleAddRuleAttribute(customerGroupRuleDto, String.valueOf(req.getConsumerLeastAmount()),CustomerGroupConstant.LEAST_AMOUNT,">=");
                hasLeast = true;
            }
            if(req.getConsumerMaxAmount()!=null && req.getConsumerMaxAmount()>0){
                customerGroupRuleAddRuleAttribute(customerGroupRuleDto, String.valueOf(req.getConsumerMaxAmount()),CustomerGroupConstant.MAX_AMOUNT,"<=");
                hasMax = true;
            }
            customerGroupRuleReqList.add(customerGroupRuleDto);
            if(hasLeast && hasMax){
                sb.append(req.getConsumerLeastAmount()).append("-").append(req.getConsumerMaxAmount()).append("元;");
            }else if (hasLeast){
                sb.append(req.getConsumerLeastAmount()).append("元以上;");
            }else{
                sb.append(req.getConsumerMaxAmount()).append("元以下;");
            }
        }
        if (req.getConsumerServeDay() != null && req.getConsumerServeDay() > 0 && CollectionUtils.isNotEmpty(req.getConsumerServeList())) {
            CustomerGroupRuleDto customerGroupRuleDto = pkgCustomerGroupRule(CustomerGroupConstant.CONSUMER_SERVER_FACTOR,String.valueOf(req.getConsumerServeDay()),CustomerGroupConstant.RECENT_DAYS,"=");
            customerGroupRuleAddRuleAttribute(customerGroupRuleDto, StringUtils.join(req.getConsumerServeList().toArray(),","),CustomerGroupConstant.SPECIFIED_SERVER,"in");
            customerGroupRuleReqList.add(customerGroupRuleDto);
            // 查询服务
            List<GoodsResp> goodsResps = queryServerList(req.getStoreId(), req.getTenantId(), req.getConsumerServeList());
            if(CollectionUtils.isNotEmpty(goodsResps)) {
                sb.append(req.getConsumerServeDay() + "天内消费过");
                for(int i=0;i<goodsResps.size();i++){
                    sb.append(goodsResps.get(i).getGoodsName());
                    if(i<goodsResps.size()-1){
                        sb.append(",");
                    }
                }
                sb.append("服务;");
            }
        }
        if(req.getCreateDateStart()!=null || req.getCreateDateEnd()!=null){
            boolean hasLeast =false;
            boolean hasMax =false;
            if(req.getCreateDateStart()!=null) {
                hasLeast = true;
                CustomerGroupRuleDto customerGroupRuleDto = pkgCustomerGroupRule(CustomerGroupConstant.CREATED_TIME_FACTOR, String.valueOf(req.getCreateDateStart()), CustomerGroupConstant.CREATED_TIME_LEAST_DAY, ">=");
                if(req.getCreateDateEnd()!=null){
                    hasMax = true;
                    customerGroupRuleAddRuleAttribute(customerGroupRuleDto, String.valueOf(req.getCreateDateEnd()),CustomerGroupConstant.CREATED_TIME_MAX_DAY,"<=");
                }
                customerGroupRuleReqList.add(customerGroupRuleDto);
            }else{
                hasMax =true;
                CustomerGroupRuleDto customerGroupRuleDto = pkgCustomerGroupRule(CustomerGroupConstant.CREATED_TIME_FACTOR, String.valueOf(req.getCreateDateEnd()), CustomerGroupConstant.CREATED_TIME_MAX_DAY, "<=");
                customerGroupRuleReqList.add(customerGroupRuleDto);
            }
            sb.append("创建时间");
            if(hasLeast && hasMax){
                sb.append(req.getCreateDateStart()).append("-").append(req.getCreateDateEnd()).append("天;");
            }else if(hasLeast){
                sb.append("大于").append(req.getCreateDateStart()).append("天;");
            }else{
                sb.append("小于").append(req.getCreateDateEnd()).append("天;");
            }
        }

        if(req.getBrithdayStart()!=null && req.getBrithdayEnd()!=null){
            CustomerGroupRuleDto customerGroupRuleDto = pkgCustomerGroupRule(CustomerGroupConstant.BRITHDAY_FACTOR,String.valueOf(req.getBrithdayStart()),CustomerGroupConstant.BRITHDAY_LEAST_MONTH,">=");
            customerGroupRuleAddRuleAttribute(customerGroupRuleDto, String.valueOf(req.getBrithdayEnd()),CustomerGroupConstant.BRITHDAY_MAX_MONTH,"<=");
            customerGroupRuleReqList.add(customerGroupRuleDto);
            sb.append("生日在最近").append(req.getBrithdayStart()).append("~").append(req.getBrithdayEnd()).append("天的客户;");
        }
        if(req.getMaintenanceDateStart()!=null && req.getMaintenanceDateEnd()!=null){
            CustomerGroupRuleDto customerGroupRuleDto = pkgCustomerGroupRule(CustomerGroupConstant.MAINTENANCE_FACTOR,String.valueOf(req.getMaintenanceDateStart()),CustomerGroupConstant.MAINTENANCE_LEAST_DAY,">=");
            customerGroupRuleAddRuleAttribute(customerGroupRuleDto, String.valueOf(req.getMaintenanceDateEnd()),CustomerGroupConstant.MAINTENANCE_MAX_DAY,"<=");
            customerGroupRuleReqList.add(customerGroupRuleDto);
            sb.append("保养日期在最近").append(req.getBrithdayStart()).append("~").append(req.getBrithdayEnd()).append("天的客户;");
        }
        customerGroupDto.setCustomerGroupRuleReqList(customerGroupRuleReqList);
        customerGroupDto.setGroupDesc(sb.toString());
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

    /**
     * 获取封装customerGroupDto  无groupName
     * @param calculateCustomerCountReq
     * @return
     */
    @Override
    public List<CustomerGroupDto>  getCustomerGroupDto(CalculateCustomerCountReq calculateCustomerCountReq){
        List<CustomerGroupDto> result = new ArrayList<>();
        if(calculateCustomerCountReq.getStoreId()==null){
            throw new StoreSaasMarketingException("获取门店ID为空");
        }
        CustomerGroupRuleExample example = new CustomerGroupRuleExample();
        CustomerGroupRuleExample.Criteria criteria = example.createCriteria();
        criteria.andStoreIdEqualTo(calculateCustomerCountReq.getStoreId());
        criteria.andStatausEqualTo("1");
        if(CollectionUtils.isNotEmpty(calculateCustomerCountReq.getGroupList())){
            criteria.andGroupIdIn(calculateCustomerCountReq.getGroupList());
        }
        List<CustomerGroupRule> customerGroupRuleList = customerGroupRuleMapper.selectByExample(example);
        if(CollectionUtils.isNotEmpty(customerGroupRuleList)){
            Map<Long,CustomerGroupDto> customerGroupDtoMap = new HashMap<>();
            for(CustomerGroupRule customerGroupRule : customerGroupRuleList){
                CustomerGroupDto customerGroupDto = null;
                if(customerGroupDtoMap.get(customerGroupRule.getGroupId())==null){
                    customerGroupDto = new CustomerGroupDto();
                    customerGroupDto.setId(customerGroupRule.getGroupId());
                    customerGroupDto.setStoreId(customerGroupRule.getStoreId());
                    customerGroupDto.setTenantId(customerGroupRule.getTenantId());
                    result.add(customerGroupDto);
                    customerGroupDtoMap.put(customerGroupRule.getGroupId(),customerGroupDto);
                }else{
                    customerGroupDto = customerGroupDtoMap.get(customerGroupRule.getGroupId());
                }
                List<CustomerGroupRuleDto> customerGroupRuleReqList = null;
                if(CollectionUtils.isEmpty(customerGroupDto.getCustomerGroupRuleReqList())){
                    customerGroupRuleReqList = new ArrayList<>();
                    customerGroupRuleReqList.add(getCustomerGroupRuleDto(customerGroupRule, customerGroupDto));
                    customerGroupDto.setCustomerGroupRuleReqList(customerGroupRuleReqList);
                }else{
                    customerGroupRuleReqList = customerGroupDto.getCustomerGroupRuleReqList();
                    boolean inExistRule = false;
                    for(CustomerGroupRuleDto customerGroupRuleDto : customerGroupRuleReqList){
                        if(customerGroupRuleDto.getCgRuleFactor().equalsIgnoreCase(customerGroupRule.getCgRuleFactor())){
                            inExistRule = true;
                            customerGroupRuleDto.getAttributeReqList().add(getCustomerGroupRuleAttribute(customerGroupRule));
                        }
                    }
                    if(!inExistRule){
                        customerGroupRuleReqList.add(getCustomerGroupRuleDto(customerGroupRule, customerGroupDto));
                    }
                }
            }
        }

        return result;
    }

    private CustomerGroupRuleDto getCustomerGroupRuleDto(CustomerGroupRule customerGroupRule, CustomerGroupDto customerGroupDto) {
        CustomerGroupRuleDto customerGroupRuleDto = new  CustomerGroupRuleDto();
        customerGroupRuleDto.setCgRuleFactor(customerGroupRule.getCgRuleFactor());
        customerGroupRuleDto.setGroupId(customerGroupDto.getId());
        customerGroupRuleDto.setStoreId(customerGroupDto.getStoreId());
        List<CustomerGroupRuleAttributeDto> attributeReqList = new ArrayList<>();
        attributeReqList.add(getCustomerGroupRuleAttribute(customerGroupRule));
        customerGroupRuleDto.setAttributeReqList(attributeReqList);
        return customerGroupRuleDto;
    }

    private CustomerGroupRuleAttributeDto getCustomerGroupRuleAttribute(CustomerGroupRule customerGroupRule) {
        CustomerGroupRuleAttributeDto customerGroupRuleAttributeDto = new CustomerGroupRuleAttributeDto();
        customerGroupRuleAttributeDto.setAttribute(customerGroupRule.getAttributeName());
        customerGroupRuleAttributeDto.setAttributeValue(customerGroupRule.getAttributeValue());
        customerGroupRuleAttributeDto.setCompareOpertor(customerGroupRule.getCompareOperator());
        return customerGroupRuleAttributeDto;
    }

    @Override
    public List<String> calculateCustomerCount(CalculateCustomerCountReq req){
        List<String> customerIdList = new ArrayList<>();
        List<CustomerGroupDto> customerGroupDtoList = getCustomerGroupDto(req);
        if(CollectionUtils.isNotEmpty(customerGroupDtoList)){
            for(CustomerGroupDto customerGroupDto : customerGroupDtoList){
                List<String> singleCustomerIdList = CustomerGroupFilterFactory.createFilter(customerGroupDto).filterProcess();
                StoreCustomerGroupRelation record = new StoreCustomerGroupRelation();
                record.setId(customerGroupDto.getId());
                record.setTenantId(req.getTenantId());
                record.setCustomerCount(Long.valueOf(singleCustomerIdList.size()));
                storeCustomerGroupRelationMapper.updateByPrimaryKeySelective(record);
                if(CollectionUtils.isEmpty(customerIdList)){
                    customerIdList.addAll(singleCustomerIdList);
                }else{
                    customerIdList.addAll(singleCustomerIdList.stream().filter(item -> !customerIdList.contains(item)).collect(toList()));
                }
            }
        }
        return customerIdList;

    }

}
