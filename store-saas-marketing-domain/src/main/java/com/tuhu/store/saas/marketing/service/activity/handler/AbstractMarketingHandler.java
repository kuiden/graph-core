package com.tuhu.store.saas.marketing.service.activity.handler;


import com.alibaba.fastjson.JSONObject;
import com.tuhu.boot.common.enums.BizErrorCodeEnum;
import com.tuhu.store.saas.crm.dto.CustomerDTO;
import com.tuhu.store.saas.crm.vo.BaseIdsReqVO;
import com.tuhu.store.saas.marketing.context.UserContextHolder;
import com.tuhu.store.saas.marketing.dataobject.*;
import com.tuhu.store.saas.marketing.exception.StoreSaasMarketingException;
import com.tuhu.store.saas.marketing.mysql.marketing.write.dao.CustomerMarketingMapper;
import com.tuhu.store.saas.marketing.mysql.marketing.write.dao.StoreCustomerGroupRelationMapper;
import com.tuhu.store.saas.marketing.remote.crm.CustomerClient;
import com.tuhu.store.saas.marketing.request.CustomerAndVehicleReq;
import com.tuhu.store.saas.marketing.request.MarketingAddReq;
import com.tuhu.store.saas.marketing.service.IMarketingSendRecordService;
import com.tuhu.store.saas.marketing.service.IMessageQuantityService;
import com.tuhu.store.saas.marketing.service.activity.MarketingResult;
import com.tuhu.store.saas.marketing.util.DateUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 *  营销活动
 * </p>
 *
 * @author yangshengyong
 * @since 2020-12-10
 */
@Slf4j
@Component
public abstract class AbstractMarketingHandler implements MarketingComHandler {
    @Autowired
    private CustomerMarketingMapper customerMarketingMapper;

    @Autowired
    private IMarketingSendRecordService iMarketingSendRecordService;

    @Autowired
    private IMessageQuantityService iMessageQuantityService;

    @Autowired
    private StoreCustomerGroupRelationMapper storeCustomerGroupRelationMapper;

    @Autowired
    private CustomerClient customerClient;


    //根据任务中记录的发送对象信息查询出客户列表
    public List<CustomerAndVehicleReq> getCustomerAndVehicleReqList(MarketingAddReq addReq, List<String> customerIds) {
        List<CustomerAndVehicleReq> customerList = analyseCustomer(addReq, customerIds);
        if (CollectionUtils.isEmpty(customerList)) {
            throw new StoreSaasMarketingException(BizErrorCodeEnum.OPERATION_FAILED, "发送对象不能为空");
        }
        return customerList;
    }

    /**
     * 公共处理逻辑
     *
     * @param addReq
     * @param result
     */
    @Transactional(rollbackFor = Exception.class)
    public void handler(MarketingAddReq addReq, MarketingResult result) {
        String currentUser = UserContextHolder.getUser() == null ? "system" : UserContextHolder.getUserName();
        List<CustomerAndVehicleReq> customerList = result.getCustomerList();
        //构造发送任务和发送记录
        CustomerMarketing customerMarketing = result.getCustomerMarketing();
        String messageData = result.getMessageData();
        //messageData
        customerMarketing.setMessageDatas(messageData);
        insert(customerMarketing);
        //写入记录表并将状态设为未发送
        List<MarketingSendRecord> records = new ArrayList();
        for (CustomerAndVehicleReq customerAndVehicleReq : customerList) {
            MarketingSendRecord marketingSendRecord = new MarketingSendRecord();
            marketingSendRecord.setMarketingId(customerMarketing.getId().toString());
            marketingSendRecord.setCustomerId(customerAndVehicleReq.getCustomerId());
            marketingSendRecord.setCustomerName(customerAndVehicleReq.getCustomerName());
            marketingSendRecord.setPhoneNumber(customerAndVehicleReq.getCustomerPhone());
            marketingSendRecord.setSendType(Byte.valueOf("0"));
            marketingSendRecord.setSendTime(customerMarketing.getSendTime());
            marketingSendRecord.setStoreId(customerMarketing.getStoreId());
            marketingSendRecord.setTenantId(customerMarketing.getTenantId());
            marketingSendRecord.setCreateTime(new Date());
            marketingSendRecord.setCreateUser(currentUser);
            records.add(marketingSendRecord);
        }
        iMarketingSendRecordService.batchInsertMarketingSendRecord(records);
        //占用短信额度
        MessageQuantity select = new MessageQuantity();
        select.setTenantId(addReq.getTenantId());
        select.setStoreId(addReq.getStoreId());
        iMessageQuantityService.setStoreOccupyQuantity(select, Long.valueOf(customerList.size()), currentUser, true);
    }

    public CustomerMarketing buildCustomerMarketing(MarketingAddReq addReq) {
        String currentUser = UserContextHolder.getUser() == null ? "system" : UserContextHolder.getUserName();
        //构造发送任务和发送记录
        CustomerMarketing customerMarketing = new CustomerMarketing();
        BeanUtils.copyProperties(addReq, customerMarketing);
        customerMarketing.setCouponMessageFlag(addReq.isMessageFlag());
        customerMarketing.setCreateTime(DateUtils.now());
        customerMarketing.setCreateUser(currentUser);
        customerMarketing.setUpdateTime(DateUtils.now());
        customerMarketing.setUpdateUser(currentUser);
        customerMarketing.setCustomerGroupId(addReq.getCustomerGroupIds());
        customerMarketing.setCustomerId(addReq.getCustomerIds());
        customerMarketing.setMarketingMethod(addReq.getMarketingMethod());
        customerMarketing.setSendTime(addReq.getSendTime());
        customerMarketing.setRemark(addReq.getRemark());
        customerMarketing.setSendObject(this.getSendObject(addReq));//客户群名称
        customerMarketing.setTaskType(Byte.valueOf("0"));
        customerMarketing.setIsDelete(Byte.valueOf("0"));
        return customerMarketing;
    }

    public String getSendObject(MarketingAddReq addReq) {
        String sendObject = "";
        if (StringUtils.isNotEmpty(addReq.getCustomerGroupIds())) {
            String[] groupIds = addReq.getCustomerGroupIds().split(",");
            List<Long> groupList = new ArrayList<>();
            for (int i = 0; i < groupIds.length; i++) {
                groupList.add(Long.valueOf(groupIds[i]));
            }
            StoreCustomerGroupRelationExample storeCustomerGroupRelationExample = new StoreCustomerGroupRelationExample();
            StoreCustomerGroupRelationExample.Criteria criteria = storeCustomerGroupRelationExample.createCriteria();
            criteria.andGroupIdIn(groupList);
            criteria.andStoreIdEqualTo(addReq.getStoreId());
            List<StoreCustomerGroupRelation> groups = storeCustomerGroupRelationMapper.selectByExample(storeCustomerGroupRelationExample);
            if (CollectionUtils.isEmpty(groups)) {
                throw new StoreSaasMarketingException(BizErrorCodeEnum.OPERATION_FAILED, "请选择客群");
            }
            sendObject = StringUtils.join(groups.stream().map(x -> x.getGroupName()).collect(Collectors.toList()), ",");
        } else {
            sendObject = "指定客户";
        }
        return sendObject;
    }

    public String setALabel(String shortUrl) {
        return "<a href=\"javascript:void(0);\" style=\"color:#1b88ee\">" + shortUrl + "</a>";
    }


    private void insert(CustomerMarketing customerMarketing) {
        if (customerMarketing != null) {
            customerMarketing.setCreateTime(new Date());
            customerMarketing.setUpdateTime(new Date());
            String md = customerMarketing.getMessageDatas();
            if (md != null && !"".equals(md)) {
                String[] strArray = md.split(",");
                List<String> list = Arrays.asList(strArray);
                customerMarketing.setMessageDatas(this.getJson(list));
            }
            customerMarketingMapper.insertSelective(customerMarketing);
        }
    }

    private String getJson(List<String> params) {
        if (params == null || params.isEmpty()) {
            return null;
        }
        String paramStr = JSONObject.toJSONString(params);
        return paramStr;
    }


    /**
     * 获取发送对象中的真实用户信息
     * @param addReq
     * @return
     */
    private List<CustomerAndVehicleReq> analyseCustomer(MarketingAddReq addReq,List<String> customerIdList){
        //根据任务中记录的发送对象信息查询出客户列表
        List<CustomerAndVehicleReq> customeList = new ArrayList();

        List<String> customerIds = new ArrayList<>();
        //客群客户
        if (StringUtils.isNotBlank(addReq.getCustomerGroupIds())){
            customerIds  = customerIdList;
            if(CollectionUtils.isEmpty(customerIds)) {
                return customeList;
            }

        }else if(StringUtils.isNotBlank(addReq.getCustomerIds())){
            //指定客户
            String[] strArray = addReq.getCustomerIds().split(",");
            for(int i=0;i<=strArray.length-1;i++){
                customerIds.add(strArray[i]);
            }
        }

        BaseIdsReqVO baseIdsReqVO = new BaseIdsReqVO();
        baseIdsReqVO.setId(customerIds);
        baseIdsReqVO.setStoreId(addReq.getStoreId());
        baseIdsReqVO.setTenantId(addReq.getTenantId());
        List<CustomerDTO> customerDTOS = customerClient.getCustomerByIds(baseIdsReqVO).getData();
        if(CollectionUtils.isEmpty(customerDTOS)){
            return customeList;
        }
        for(CustomerDTO customerDTO : customerDTOS){
            CustomerAndVehicleReq cavReq = new CustomerAndVehicleReq();
            //客户详情
            cavReq.setCustomerId(customerDTO.getId());
            cavReq.setCustomerName(customerDTO.getName());
            cavReq.setCustomerPhone(customerDTO.getPhoneNumber());
            customeList.add(cavReq);
        }
        return customeList;
    }

}
