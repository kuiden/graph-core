/*
 * Copyright 2018 tuhu.cn All right reserved. This software is the
 * confidential and proprietary information of tuhu.cn ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with Tuhu.cn
 */
package com.tuhu.store.saas.marketing.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.tuhu.store.saas.crm.dto.CustomerDTO;
import com.tuhu.store.saas.crm.vo.BaseIdsReqVO;
import com.tuhu.store.saas.marketing.dataobject.MessageQuantity;
import com.tuhu.store.saas.marketing.dataobject.MessageRemind;
import com.tuhu.store.saas.marketing.dataobject.MessageTemplateLocal;
import com.tuhu.store.saas.marketing.enums.MessageStatusEnum;
import com.tuhu.store.saas.marketing.exception.StoreSaasMarketingException;
import com.tuhu.store.saas.marketing.remote.crm.CustomerClient;
import com.tuhu.store.saas.marketing.remote.crm.StoreInfoClient;
import com.tuhu.store.saas.marketing.request.CustomerAndVehicleReq;
import com.tuhu.store.saas.marketing.request.SendRemindReq;
import com.tuhu.store.saas.marketing.service.IMessageQuantityService;
import com.tuhu.store.saas.marketing.service.IMessageRemindService;
import com.tuhu.store.saas.marketing.service.IMessageTemplateLocalService;
import com.tuhu.store.saas.marketing.service.IRemindService;
import com.tuhu.store.saas.marketing.util.DateUtils;
import com.tuhu.store.saas.marketing.util.IdKeyGen;
import com.tuhu.store.saas.user.dto.StoreDTO;
import com.tuhu.store.saas.user.vo.StoreInfoVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * @author xuechaofu
 * @date 2018/11/13 19:42
 */
@Service
@Slf4j
public class RemindServiceImpl implements IRemindService {

    @Value("${marketing.message.try.times:0}")
    private Integer tryTimes;

    @Autowired
    private IMessageTemplateLocalService templateLocalService;

    @Autowired
    private IMessageQuantityService messageQuantityService;

    @Autowired
    private IMessageRemindService remindService;

    @Autowired
    private CustomerClient customerClient;

    @Autowired
    private StoreInfoClient storeInfoClient;

    @Autowired
    private IdKeyGen idKeyGen;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean send(SendRemindReq sendRemindReq, boolean releaseOccupy) {
        log.info("短信发送接口，入参:{}" + JSONObject.toJSONString(sendRemindReq));
        int messageTimes = sendRemindReq.getCustomerList().size();
        MessageQuantity select = new MessageQuantity();
        select.setStoreId(sendRemindReq.getStoreId());
        select.setTenantId(sendRemindReq.getTenantId());
        select.setCreateUser(sendRemindReq.getUserId());
//        //判断剩余提醒次数
//        Long availableNum = messageQuantityService.selectQuantityByTenantIdAndStoreId(select);
//        if (availableNum < messageTimes) {
//            throw new StoreSaasMarketingException("用户剩余提醒数量不足!");
//        }
        //保存提醒记录
        this.saveMessageRemind(sendRemindReq);
        //更新租户提醒次数
        messageQuantityService.reduceQuantity(select, messageTimes, sendRemindReq.getUserId(),releaseOccupy);
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean sendWithPhone(SendRemindReq sendRemindReq, String phone, boolean releaseOccupy) {
        log.info("短信发送接口，入参:{},手机号:{}", JSONObject.toJSONString(sendRemindReq), phone);
        MessageQuantity select = new MessageQuantity();
        select.setStoreId(sendRemindReq.getStoreId());
        select.setTenantId(sendRemindReq.getTenantId());
        select.setCreateUser(sendRemindReq.getUserId());
//        //判断剩余提醒次数
//        MessageQuantity messageQuantity = messageQuantityService.selectQuantityByTenantIdAndStoreId(select);
//        if (messageQuantity.getRemainderQuantity() < 1) {
//            throw new StoreSaasMarketingException("用户剩余提醒数量不足!");
//        }
        //保存提醒记录
        this.saveMessageRemindWithPhone(sendRemindReq, phone);
        //更新租户提醒次数
        messageQuantityService.reduceQuantity(select, 1, sendRemindReq.getUserId(), releaseOccupy);
        return true;
    }

    /**
     * 给指定的手机号发送短信
     *
     * @param sendRemindReq
     * @param phone
     */
    private void saveMessageRemindWithPhone(SendRemindReq sendRemindReq, String phone) {
        Date now = new Date();
        //获取短信模板参数集合datas
        String datas = sendRemindReq.getDatas();
        MessageTemplateLocal messageTemplateLocal = templateLocalService.getTemplateLocalById(sendRemindReq.getMessageTemplateId());
        if (messageTemplateLocal == null) {
            throw new StoreSaasMarketingException("该客户提醒模板不存在!");
        }
        MessageRemind remind = new MessageRemind();
        String remindId = idKeyGen.generateId(sendRemindReq.getTenantId());
        remind.setId(remindId);
        remind.setStoreId(sendRemindReq.getStoreId());
        remind.setTenantId(sendRemindReq.getTenantId());
        remind.setCreateTime(now);
        remind.setCreateUser(sendRemindReq.getUserId());
        //填装用户信息
        remind.setPhoneNumber(phone);
        //填装提醒信息
        remind.setTemplateId(messageTemplateLocal.getTemplateId());
        remind.setMessageTemplateId(messageTemplateLocal.getId());
        remind.setMessageTemplateCode(messageTemplateLocal.getTemplateCode());
        remind.setMessageContent(messageTemplateLocal.getTemplateContent());
        remind.setStatus(MessageStatusEnum.MESSAGE_WAIT.getCode());
        remind.setTryTime(tryTimes);
        remind.setDatas(datas);
        remind.setSource(sendRemindReq.getSource());
        remind.setSourceId(sendRemindReq.getSourceId());
        remindService.insertMessageRemindList(Collections.singletonList(remind));
    }

    /**
     * 存入需要信息提醒的记录信息
     *
     * @param sendRemindReq
     */
    private void saveMessageRemind(SendRemindReq sendRemindReq) {
        Date now = new Date();
        List<String> customerIdList = new ArrayList<>();
        Map<String,CustomerAndVehicleReq> customerAndInfos = new HashMap<>();
        for (CustomerAndVehicleReq req : sendRemindReq.getCustomerList()) {
            customerIdList.add(req.getCustomerId());
            customerAndInfos.put(req.getCustomerId(),req);
        }
        //获取短信模板参数集合datas(2019-05-27 Add)
        String datas = sendRemindReq.getDatas();

        //获取需发短信用户信息
        BaseIdsReqVO baseIdsReqVO = new BaseIdsReqVO();
        baseIdsReqVO.setId(customerIdList);
        baseIdsReqVO.setTenantId(sendRemindReq.getTenantId());
        baseIdsReqVO.setStoreId(sendRemindReq.getStoreId());
        List<CustomerDTO> customerDTOS = customerClient.getCustomerByIds(baseIdsReqVO).getData();
        MessageTemplateLocal messageTemplateLocal = templateLocalService.getTemplateLocalById(sendRemindReq.getMessageTemplateId());
        if (messageTemplateLocal == null) {
            throw new StoreSaasMarketingException("该客户提醒模板不存在!");
        }
        List<MessageRemind> remindList = new ArrayList<>();
        for (CustomerDTO customer : customerDTOS) {
            MessageRemind remind = new MessageRemind();
            String remindId = idKeyGen.generateId(sendRemindReq.getTenantId());
            remind.setId(remindId);
            remind.setStoreId(sendRemindReq.getStoreId());
            remind.setTenantId(sendRemindReq.getTenantId());
            remind.setCreateTime(now);
            remind.setCreateUser(sendRemindReq.getUserId());
            //填装用户信息
            remind.setCustomerId(customer.getId());
            remind.setCustomerName(customer.getName());
            remind.setPhoneNumber(customer.getPhoneNumber());
            CustomerAndVehicleReq req = customerAndInfos.get(customer.getId());
            if(req!=null){
                //车辆信息不为空
                remind.setVehicleId(req.getVehicleId());
                if (StringUtils.isNotEmpty(req.getNextDate())) {
                    remind.setExpiryDate(DateUtils.parseDate(req.getNextDate()));
                }
            }
            //填装提醒信息
            remind.setTemplateId(messageTemplateLocal.getTemplateId());
            remind.setMessageTemplateId(messageTemplateLocal.getId());
            remind.setMessageTemplateCode(messageTemplateLocal.getTemplateCode());
            remind.setMessageContent(messageTemplateLocal.getTemplateContent());
            remind.setStatus(MessageStatusEnum.MESSAGE_WAIT.getCode());
            remind.setTryTime(tryTimes);
            // 拼接短信平台短信参数
            if (datas == null || "".equals(datas)) {
                //dubbo接口获取当前用户门店信息
                StoreInfoVO storeInfoVO = new StoreInfoVO();
                storeInfoVO.setCompanyId(sendRemindReq.getCompanyId());
                storeInfoVO.setStoreId(sendRemindReq.getStoreId());
                storeInfoVO.setTanentId(sendRemindReq.getTenantId());
                StoreDTO storeDTO = storeInfoClient.getStoreInfo(storeInfoVO).getData();
                List<String> params = new ArrayList<>();
                params.add(req.getLicensePlateNumber());
                params.add(storeDTO.getMobilePhone());
                params.add(storeDTO.getAddress());
                String data = this.getJson(params);
                remind.setDatas(data);
            } else {
                remind.setDatas(datas);
            }
            remind.setSource(sendRemindReq.getSource());
            remind.setSourceId(sendRemindReq.getSourceId());
            remindList.add(remind);
        }
        if (CollectionUtils.isNotEmpty(remindList)) {
            remindService.insertMessageRemindList(remindList);
        }
    }

    private String getJson(List<String> params) {
        if (params == null || params.isEmpty()) {
            return null;
        }
        String paramStr = JSONObject.toJSONString(params);
        return paramStr;
    }
}
