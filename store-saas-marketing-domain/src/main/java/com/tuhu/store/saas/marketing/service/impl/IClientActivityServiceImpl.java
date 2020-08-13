/*
 * Copyright 2020 tuhu.cn All right reserved. This software is the
 * confidential and proprietary information of tuhu.cn ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with Tuhu.cn
 */
package com.tuhu.store.saas.marketing.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.tuhu.boot.common.facade.BizBaseResponse;
import com.tuhu.store.saas.crm.dto.CustomerDTO;
import com.tuhu.store.saas.crm.vo.CustomerSourceEnumVo;
import com.tuhu.store.saas.crm.vo.CustomerVO;
import com.tuhu.store.saas.marketing.enums.CustomTypeEnumVo;
import com.tuhu.store.saas.marketing.enums.MarketingBizErrorCodeEnum;
import com.tuhu.store.saas.marketing.exception.MarketingException;
import com.tuhu.store.saas.marketing.mysql.marketing.write.dao.ActivityMapper;
import com.tuhu.store.saas.marketing.po.Activity;
import com.tuhu.store.saas.marketing.po.ActivityExample;
import com.tuhu.store.saas.marketing.remote.crm.CustomerClient;
import com.tuhu.store.saas.marketing.remote.request.AddVehicleReq;
import com.tuhu.store.saas.marketing.remote.request.CustomerReq;
import com.tuhu.store.saas.marketing.remote.storeuser.StoreUserClient;
import com.tuhu.store.saas.marketing.request.ActivityApplyReq;
import com.tuhu.store.saas.marketing.request.ActivityCustomerReq;
import com.tuhu.store.saas.marketing.response.ActivityApplyResp;
import com.tuhu.store.saas.marketing.response.ActivityCustomerResp;
import com.tuhu.store.saas.marketing.response.CommonResp;
import com.tuhu.store.saas.marketing.service.IActivityService;
import com.tuhu.store.saas.marketing.service.IClientActivityService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.scmc.arch.model.exception.BizException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author xiesisi
 * @date 2020/8/1311:55
 */
@Slf4j
@Service
public class IClientActivityServiceImpl  implements IClientActivityService {

    @Autowired
    CustomerClient customerClient;

    @Autowired
    IActivityService iActivityService;
    
    @Autowired
    StoreUserClient storeUserClient;

    @Autowired
    private ActivityMapper activityMapper;

    @Override
    @Transactional
    public ActivityApplyResp clientActivityApply(ActivityApplyReq applyReq){
        //获取当前的客户信息
        ActivityExample activityExample = new ActivityExample();
        ActivityExample.Criteria activityCriteria = activityExample.createCriteria();
        activityCriteria.andEncryptedCodeEqualTo(applyReq.getEncryptedCode());
        List<Activity> activities = activityMapper.selectByExample(activityExample);
        if(activities.size()<1){
            throw new MarketingException(MarketingBizErrorCodeEnum.AC_ORDER_NOT_EXIST.getDesc());
        }
        applyReq.setStoreId(activities.get(0).getStoreId());
        applyReq.setTenantId(activities.get(0).getTenantId());
        ActivityApplyResp activityApplyResp = new ActivityApplyResp();
        CustomerVO customerVO = new CustomerVO();
        customerVO.setPhone(applyReq.getTelephone());
        customerVO.setStoreId(applyReq.getStoreId());
        List<CustomerDTO> customerReqList = customerClient.getCustomer(customerVO).getData();
        if(customerReqList.size()<1){
            CustomerReq customerReq = new CustomerReq();
            customerReq.setPhoneNumber(applyReq.getTelephone());
            customerReq.setName(applyReq.getTelephone());
            customerReq.setGender("1");
            customerReq.setCustomerType(CustomTypeEnumVo.PERSON.getCode());
            customerReq.setCustomerSource(CustomerSourceEnumVo.QT.getCode());
            customerReq.setIsVip(false);
            AddVehicleReq addVehicleReq = new AddVehicleReq();
            addVehicleReq.setCustomerReq(customerReq);
            addVehicleReq.setStoreId(applyReq.getStoreId());
            addVehicleReq.setTenantId(applyReq.getTenantId());
            try{
                log.info("新增客户请求:{}",JSONObject.toJSONString(addVehicleReq));
                BizBaseResponse<AddVehicleReq> addObject = storeUserClient.addCustomerForReservation(addVehicleReq);
                log.info("新增客户返回为:{}", JSONObject.toJSONString(addObject));
                AddVehicleReq newAddVehicleReq=addObject.getData();
                if(newAddVehicleReq==null || StringUtils.isBlank(newAddVehicleReq.getCustomerReq().getId())){
                    log.error("报名-》新增客户异常，storeUserClient.addCustomerForReservation，request:{},cause:{}",addVehicleReq,addObject.getMessage());
                    throw new MarketingException(MarketingBizErrorCodeEnum.ACTIVITY_APPLY_FAILED.getDesc());
                }
                applyReq.setCustomerId(newAddVehicleReq.getCustomerReq().getId());
                applyReq.setCustomerName(applyReq.getCustomerName());
            }catch (BizException e){
                throw new MarketingException(e.getMessage());
            }
        } else{
            applyReq.setCustomerId(customerReqList.get(0).getId());
            applyReq.setCustomerName(customerReqList.get(0).getName());
        }
        //报名
        log.info("报名请求："+JSONObject.toJSONString(applyReq));
        CommonResp<String> stringCommonResp= iActivityService.applyActivity(applyReq);
        log.info("报名返回："+JSONObject.toJSONString(stringCommonResp));
        if(stringCommonResp==null ||StringUtils.isBlank(stringCommonResp.getData())){
            log.error("报名异常，iActivityService.applyActivity,request:{},cause:{}",applyReq,stringCommonResp.getMessage());
            throw new MarketingException(MarketingBizErrorCodeEnum.ACTIVITY_APPLY_FAILED.getDesc()+stringCommonResp.getMessage());
        }
        if(stringCommonResp.getCode() == 4005){
            //已报名，重复操作
            activityApplyResp.setActivityCustomerOrderCode(stringCommonResp.getData());
            activityApplyResp.setAppliedSuccess(false);
            return activityApplyResp;
        }
        ActivityCustomerReq activityCustomerReq = new ActivityCustomerReq();
        activityCustomerReq.setActivityOrderCode(stringCommonResp.getData());
        ActivityCustomerResp resp = iActivityService.getActivityCustomerDetail(activityCustomerReq);
        if(resp==null || StringUtils.isBlank(resp.getActivityOrderCode())){
            throw new MarketingException(MarketingBizErrorCodeEnum.ACTIVITY_APPLY_FAILED.getDesc());
        }
        activityApplyResp.setActivityCustomerOrderCode(resp.getActivityOrderCode());
        activityApplyResp.setAppliedSuccess(true);
        return activityApplyResp;
    }
}