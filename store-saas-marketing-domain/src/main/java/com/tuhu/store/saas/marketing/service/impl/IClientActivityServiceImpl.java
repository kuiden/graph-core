/*
 * Copyright 2020 tuhu.cn All right reserved. This software is the
 * confidential and proprietary information of tuhu.cn ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with Tuhu.cn
 */
package com.tuhu.store.saas.marketing.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.google.zxing.WriterException;
import com.tuhu.boot.common.exceptions.BizException;
import com.tuhu.boot.common.facade.BizBaseResponse;
import com.tuhu.store.saas.crm.dto.CustomerDTO;
import com.tuhu.store.saas.crm.vo.CustomerSourceEnumVo;
import com.tuhu.store.saas.crm.vo.CustomerVO;
import com.tuhu.store.saas.marketing.context.EndUserContextHolder;
import com.tuhu.store.saas.marketing.enums.CustomTypeEnumVo;
import com.tuhu.store.saas.marketing.enums.MarketingBizErrorCodeEnum;
import com.tuhu.store.saas.marketing.enums.MarketingCustomerUseStatusEnum;
import com.tuhu.store.saas.marketing.exception.MarketingException;
import com.tuhu.store.saas.marketing.mysql.marketing.write.dao.ActivityCustomerMapper;
import com.tuhu.store.saas.marketing.mysql.marketing.write.dao.ActivityItemMapper;
import com.tuhu.store.saas.marketing.mysql.marketing.write.dao.ActivityMapper;
import com.tuhu.store.saas.marketing.po.*;
import com.tuhu.store.saas.marketing.remote.auth.AuthClient;
import com.tuhu.store.saas.marketing.remote.crm.CustomerClient;
import com.tuhu.store.saas.marketing.remote.crm.StoreInfoClient;
import com.tuhu.store.saas.marketing.remote.request.AddVehicleReq;
import com.tuhu.store.saas.marketing.remote.request.CustomerReq;
import com.tuhu.store.saas.marketing.remote.request.EndUserMarketingBindRequest;
import com.tuhu.store.saas.marketing.remote.storeuser.StoreUserClient;
import com.tuhu.store.saas.marketing.request.ActivityApplyReq;
import com.tuhu.store.saas.marketing.request.ActivityContent;
import com.tuhu.store.saas.marketing.request.ActivityCustomerReq;
import com.tuhu.store.saas.marketing.request.vo.ClientStoreInfoVO;
import com.tuhu.store.saas.marketing.response.*;
import com.tuhu.store.saas.marketing.service.IActivityService;
import com.tuhu.store.saas.marketing.service.IClientActivityService;
import com.tuhu.store.saas.marketing.util.QrCode;
import com.tuhu.store.saas.user.dto.ClientStoreDTO;
import com.tuhu.store.saas.user.vo.ClientStoreVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author xiesisi
 * @date 2020/8/1311:55
 */
@Slf4j
@Service
public class IClientActivityServiceImpl  implements IClientActivityService {

    @Autowired
    private CustomerClient customerClient;

    @Autowired
    private IActivityService iActivityService;
    
    @Autowired
    private StoreUserClient storeUserClient;

    @Autowired
    private AuthClient authClient;

    @Autowired
    private ActivityMapper activityMapper;

    @Autowired
    private ActivityItemMapper activityItemMapper;

    @Autowired
    private StoreInfoClient storeInfoClient;

    @Autowired
    private ActivityCustomerMapper activityCustomerMapper;

    @Transactional
    @Override
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
        //登录
        EndUserMarketingBindRequest endUserMarketingBindRequest= new EndUserMarketingBindRequest();
        endUserMarketingBindRequest.setPhone(applyReq.getTelephone());
        endUserMarketingBindRequest.setStoreId(applyReq.getStoreId());
        endUserMarketingBindRequest.setTenantId(applyReq.getTenantId());
        Map<String,String> mapRequest = new HashMap<>();
        mapRequest.put("phone",applyReq.getTelephone());
        mapRequest.put("storeId",applyReq.getStoreId().toString());
        mapRequest.put("tenantId",applyReq.getTenantId().toString());
        mapRequest.put("userType",endUserMarketingBindRequest.getUserType());
        mapRequest.put("clientType",endUserMarketingBindRequest.getClientType());
        Map<String,Object> bindUserResp = authClient.bindWechatEndUserByPhone(endUserMarketingBindRequest,mapRequest).getData();
        if(bindUserResp.size()>0) {
            //拼接登录token
            Map tokenMap = JSONObject.parseObject(JSONObject.toJSONString(bindUserResp.get("token")));
            StringBuilder stringBuilderToken = new StringBuilder();
            stringBuilderToken.append("Bearer ");
            stringBuilderToken.append(tokenMap.get("access_token"));
            activityApplyResp.setUserLoggedToken(stringBuilderToken.toString());
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
            activityApplyResp.setAppliedSuccess(false);
            return activityApplyResp;
        }
        ActivityCustomerReq activityCustomerReq = new ActivityCustomerReq();
        activityCustomerReq.setActivityOrderCode(stringCommonResp.getData());
        ActivityCustomerResp resp = iActivityService.getActivityCustomerDetail(activityCustomerReq);
        if(resp==null || StringUtils.isBlank(resp.getActivityOrderCode())){
            throw new MarketingException(MarketingBizErrorCodeEnum.ACTIVITY_APPLY_FAILED.getDesc());
        }
        activityApplyResp.setAppliedSuccess(true);
        return activityApplyResp;
    }

    @Override
    public ActivityResp getActivityDetailByEncryptedCode(String encryptedCode){
        log.info("活动详情，入参:{}", encryptedCode);
        if (StringUtils.isBlank(encryptedCode)) {
            throw new MarketingException(MarketingBizErrorCodeEnum.ACTIVITY_ENCRYPTED_CODE_NOT_INPUT.getDesc());
        }
        ActivityExample activityExample = new ActivityExample();
        ActivityExample.Criteria activityExampleCriteria = activityExample.createCriteria();
        activityExampleCriteria.andEncryptedCodeEqualTo(encryptedCode);
        List<Activity> activityList = activityMapper.selectByExample(activityExample);
        if(activityList.size() < 1){
            throw new MarketingException(MarketingBizErrorCodeEnum.AC_ORDER_NOT_EXIST.getDesc());
        }
        Activity activity = activityList.get(0);
        //1.根据活动编码查询活动详情
        return getActivityByActivityCode(activity.getActivityCode());
    }


    @Override
    public ActivityResp getActivityByActivityCode(String activityCode) {
        if (org.apache.commons.lang3.StringUtils.isBlank(activityCode)) {
            return null;
        }
        ActivityExample activityExample = new ActivityExample();
        ActivityExample.Criteria activityExampleCriteria = activityExample.createCriteria();
        activityExampleCriteria.andActivityCodeEqualTo(activityCode);
        List<Activity> activityList = activityMapper.selectByExample(activityExample);
        if (CollectionUtils.isEmpty(activityList)) {
            return null;
        }
        Activity activity = activityList.get(0);
        ActivityResp activityResp = new ActivityResp();
        BeanUtils.copyProperties(activity, activityResp);
        if (null != activity.getActivityContent()){
            activityResp.setContents(JSONObject.parseArray(activity.getActivityContent(), ActivityContent.class));
        }
        //查询活动项目
        ActivityItemExample itemExample = new ActivityItemExample();
        ActivityItemExample.Criteria itemExampleCriteria = itemExample.createCriteria();
        itemExampleCriteria.andActivityCodeEqualTo(activity.getActivityCode());
        List<ActivityItem> activityItemList = activityItemMapper.selectByExample(itemExample);
        if (CollectionUtils.isNotEmpty(activityItemList)) {
            List<ActivityItemResp> activityItemRespList = new ArrayList<>();
            for (ActivityItem activityItem : activityItemList) {
                ActivityItemResp activityItemResp = new ActivityItemResp();
                BeanUtils.copyProperties(activityItem, activityItemResp);
                activityItemRespList.add(activityItemResp);
            }
            activityResp.setItems(activityItemRespList);
        }
        //原价计算
        Long totalPrice = 0L;
        for(ActivityItemResp item : activityResp.getItems()){
            totalPrice += item.getOriginalPrice() * item.getItemQuantity();
        }
        activityResp.setOriginalTotalPrice(new BigDecimal(totalPrice));
        //补充门店信息
        ClientStoreVO clientStoreVO = new ClientStoreVO();
        clientStoreVO.setStoreId(activity.getStoreId());
        clientStoreVO.setTenantId(activity.getTenantId());
        BizBaseResponse<ClientStoreDTO> resultData = storeInfoClient.getStoreInfoForClient(clientStoreVO);
        if (resultData != null && resultData.getData() != null) {
            ClientStoreInfoVO storeInfo = new ClientStoreInfoVO();
            BeanUtils.copyProperties(resultData.getData(),storeInfo);
            activityResp.setStoreInfo(storeInfo);
        }
        if(!activityResp.getStatus()){
            //活动已下架
            return activityResp;
        }
        //获取此活动的报名情况
        List<Byte> useStatusList = new ArrayList<>();
        useStatusList.add(MarketingCustomerUseStatusEnum.AC_ORDER_NEVER_USE.getStatusOfByte());
        useStatusList.add(MarketingCustomerUseStatusEnum.AC_ORDER_CLOSE.getStatusOfByte());
        useStatusList.add(MarketingCustomerUseStatusEnum.AC_ORDER_IS_USED.getStatusOfByte());
        ActivityCustomerExample activityCustomerExample = new ActivityCustomerExample();
        ActivityCustomerExample.Criteria acExampleCriterria = activityCustomerExample.createCriteria();
        acExampleCriterria.andActivityCodeEqualTo(activityCode);
        acExampleCriterria.andUseStatusIn(useStatusList);
        List<ActivityCustomer> activityCustomerList = activityCustomerMapper.selectByExample(activityCustomerExample);
        //当前已报名人数
        activityResp.setApplyCount(new Long(activityCustomerList.size()));
        //已核销人数
        activityResp.setWriteOffCount(activityCustomerList.stream().
                filter(x->x.getUseStatus().compareTo(MarketingCustomerUseStatusEnum.AC_ORDER_IS_USED.getStatusOfByte())>0).count());
        //报名状态
        List<String> appliedPhoneList = activityCustomerList.stream().map(x->x.getTelephone()).collect(Collectors.toList());
        //如果当前页面为用户登录访问
        if(EndUserContextHolder.getUser()!=null){
            activityResp.setApplyed(appliedPhoneList.contains(EndUserContextHolder.getUser().getPhone()));
        }
        //活动状态
        //1.活动过期
        if (activity.getEndTime().compareTo(new Date()) <= 0) {
            activityResp.setDateStatus(2);
        }
        //2.活动未开始
        else if (activity.getStartTime().compareTo(new Date()) > 0) {
             activityResp.setDateStatus(0);
        }
        else {
            activityResp.setDateStatus(1);
        }
        return activityResp;
    }

    @Override
    public ActivityCustomerResp getActivityCustomerDetail(String encryptedCode){
        ActivityCustomerResp response = new ActivityCustomerResp();
        log.info("客户活动详情，入参:{},{}",encryptedCode);
        ActivityCustomerResp activityCustomerResp = new ActivityCustomerResp();
        if (org.apache.commons.lang3.StringUtils.isBlank(encryptedCode)) {
            throw new MarketingException("活动报名订单号不能为空");
        }
        //1.根据活动编码和用户Id查询活动报名信息
        log.info("匹配活动订单，入参:{},{}",encryptedCode,EndUserContextHolder.getCustomerId());
        ActivityCustomer activityCustomer = activityCustomerMapper.selectByEncryptedCodeAndUser(encryptedCode,EndUserContextHolder.getCustomerId());
        log.info("匹配活动订单,出参:{}",JSONObject.toJSONString(activityCustomer));
        if(activityCustomer == null){
            throw new MarketingException(MarketingBizErrorCodeEnum.AC_ORDER_NOT_EXIST.getDesc());
        }
        //response-set:基本信息copy
        BeanUtils.copyProperties(activityCustomer, activityCustomerResp);
        //2.根据活动编码查询活动详情
        String activityCode = activityCustomer.getActivityCode();
        ActivityResp activityResp = this.getActivityByActivityCode(activityCode);
        //response-set:活动详情
        Long totalPrice = 0L;
        for(ActivityItemResp item : activityResp.getItems()){
            totalPrice += item.getOriginalPrice() * item.getItemQuantity();
        }
        activityCustomerResp.setActivity(activityResp);
        if(activityCustomerResp.getUseStatus().intValue() == 1){
            //未核销
            Map<String,String> codeMap = new HashMap<>(2);
            codeMap.put("type","2");
            codeMap.put("activityCustomerCode",activityCustomerResp.getActivityOrderCode());
            try {
                //添加二维码字节流
                activityCustomerResp.setQrCode(QrCode.getQRCodeImage(JSONObject.toJSONString(codeMap),500,500));
            }catch (WriterException e){
                log.warn("活动订单详情，获取二维码失败：",e);
            }catch (IOException e){
                log.warn("活动订单详情，获取二维码失败：",e);
            }
        }
        log.info("客户活动详情，出参:{}", JSONObject.toJSONString(activityCustomerResp));
        return activityCustomerResp;
    }

}