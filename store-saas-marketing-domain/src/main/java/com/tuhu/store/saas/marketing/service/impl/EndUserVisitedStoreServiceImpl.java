package com.tuhu.store.saas.marketing.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.tuhu.base.service.impl.BaseServiceImpl;
import com.tuhu.boot.common.facade.BizBaseResponse;
import com.tuhu.store.saas.marketing.dataobject.OauthClientDetailsDAO;
import com.tuhu.store.saas.marketing.entity.EndUserVisitedStoreEntity;
import com.tuhu.store.saas.marketing.exception.StoreSaasMarketingException;
import com.tuhu.store.saas.marketing.mysql.marketing.write.dao.EndUserVisitedStoreWriteMapper;
import com.tuhu.store.saas.marketing.remote.request.EndUserVisitedStoreReq;
import com.tuhu.store.saas.marketing.remote.storeuser.StoreUserClient;
import com.tuhu.store.saas.marketing.request.EndUserVistiedStoreRequest;
import com.tuhu.store.saas.marketing.response.EndUserVisitedStoreResp;
import com.tuhu.store.saas.marketing.service.IEndUserVisitedStoreService;
import com.tuhu.store.saas.marketing.service.IOauthClientDetailsService;
import com.tuhu.store.saas.marketing.service.IWechatService;
import com.tuhu.store.saas.user.vo.StoreSimpleInfoResp;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * <p>
 * 车主端用户访问的门店记录 服务实现类
 * </p>
 *
 * @author someone
 * @since 2020-08-03
 */
@Service
@Slf4j
public class EndUserVisitedStoreServiceImpl extends BaseServiceImpl<EndUserVisitedStoreWriteMapper, EndUserVisitedStoreEntity> implements IEndUserVisitedStoreService {

//    @Autowired
//    private EndUserVisitedStoreWriteMapper endUserVisitedStoreWriteMapper;

    @Autowired
    private IOauthClientDetailsService iOauthClientDetailsService;

    @Autowired
    private IWechatService iWechatService;


    @Autowired
    private StoreUserClient storeUserClient;


    @Override
    public EndUserVisitedStoreEntity findFirstByOpenIdAndStoreId(String openId, String storeId) {
        return baseMapper.findFirstByOpenIdAndStoreId(openId, storeId);
    }

    @Override
    public Integer addNewEndUserVisitedStore(EndUserVisitedStoreEntity endUserVisitedStoreEntity) {
        return baseMapper.insert(endUserVisitedStoreEntity);
    }

    @Override
    public Integer updateVisitedTimeById(String id, Date newDate) {
        return baseMapper.updateVisitedTimeById(id, newDate);
    }

    @Override
    public List<EndUserVisitedStoreEntity> findAllByOpenId(String openId) {
        return baseMapper.findAllByOpenId(openId);
    }


    @Override
    @Transactional
    public EndUserVisitedStoreEntity recordEndUserVistiedStore(EndUserVistiedStoreRequest endUserVistiedStoreRequest) {
        log.info("记录用户浏览的门店入参：{}", JSONObject.toJSONString(endUserVistiedStoreRequest));
        if (StringUtils.isEmpty(endUserVistiedStoreRequest.getOpenIdCode()) || StringUtils.isEmpty(endUserVistiedStoreRequest.getStoreId())) {
            log.error("记录用户浏览的门店入参错误：{}", JSONObject.toJSONString(endUserVistiedStoreRequest));
            throw new StoreSaasMarketingException("记录用户浏览的门店入参错误");
        }
        String clientType = endUserVistiedStoreRequest.getClientType();
        if (StringUtils.isEmpty(clientType)) {
            clientType = "end_user_client";
        }
        OauthClientDetailsDAO oauthClientDetails = iOauthClientDetailsService.getClientDetailByClientId(clientType);
        if (null == oauthClientDetails) {
            log.error("客户端配置信息不存在，clientType={}", clientType);
            throw new StoreSaasMarketingException("客户端配置信息不存在");
        }
        String openId = iWechatService.getOpenId(oauthClientDetails.getWxAppid()
                , oauthClientDetails.getWxSecret()
                , endUserVistiedStoreRequest.getOpenIdCode(),
                oauthClientDetails.getWxOpenidUrl());
        String storeId = endUserVistiedStoreRequest.getStoreId();
        //首选查询现有记录
        EndUserVisitedStoreEntity endUserVisitedStoreEntity = this.findFirstByOpenIdAndStoreId(openId, storeId);
        //如果没有记录
        if (null == endUserVisitedStoreEntity || StringUtils.isEmpty(endUserVisitedStoreEntity.getId())) {
            endUserVisitedStoreEntity = new EndUserVisitedStoreEntity();
            endUserVisitedStoreEntity.setId(UUID.randomUUID().toString());
            endUserVisitedStoreEntity.setOpenId(openId);
            endUserVisitedStoreEntity.setStoreId(endUserVistiedStoreRequest.getStoreId());
            endUserVisitedStoreEntity.setCount(1L);
            Date date = new Date();
            endUserVisitedStoreEntity.setCreateTime(date);
            endUserVisitedStoreEntity.setUpdateTime(date);
            try {
                this.addNewEndUserVisitedStore(endUserVisitedStoreEntity);
            } catch (Exception e) {
                log.warn("相同的openId和storeId记录已存在", e);
                //再次查询
                endUserVisitedStoreEntity = this.findFirstByOpenIdAndStoreId(openId, storeId);
            }
        } else {
            this.updateVisitedTimeById(endUserVisitedStoreEntity.getId(), new Date());
        }
        return endUserVisitedStoreEntity;
    }


    @Override
    public EndUserVisitedStoreEntity recordEndUserVistiedStore(EndUserVisitedStoreEntity endUserVisitedStoreEntity) {
        log.info("记录用户浏览的门店入参：{}", JSONObject.toJSONString(endUserVisitedStoreEntity));
        if (StringUtils.isEmpty(endUserVisitedStoreEntity.getOpenId()) || StringUtils.isEmpty(endUserVisitedStoreEntity.getStoreId())) {
            log.error("记录用户浏览的门店入参错误：{}", JSONObject.toJSONString(endUserVisitedStoreEntity));
            throw new StoreSaasMarketingException("记录用户浏览的门店入参错误");
        }
        String openId = endUserVisitedStoreEntity.getOpenId();
        String storeId = endUserVisitedStoreEntity.getStoreId();
        //首选查询现有记录
        EndUserVisitedStoreEntity oldEndUserVisitedStoreEntity = this.findFirstByOpenIdAndStoreId(openId, storeId);
        //如果没有记录
        if (null == oldEndUserVisitedStoreEntity || StringUtils.isEmpty(oldEndUserVisitedStoreEntity.getId())) {
            endUserVisitedStoreEntity = new EndUserVisitedStoreEntity();
            endUserVisitedStoreEntity.setId(UUID.randomUUID().toString());
            endUserVisitedStoreEntity.setOpenId(openId);
            endUserVisitedStoreEntity.setStoreId(storeId);
            endUserVisitedStoreEntity.setCount(1L);
            Date date = new Date();
            endUserVisitedStoreEntity.setCreateTime(date);
            endUserVisitedStoreEntity.setUpdateTime(date);
            try {
                this.addNewEndUserVisitedStore(endUserVisitedStoreEntity);
            } catch (Exception e) {
                log.warn("相同的openId和storeId记录已存在", e);
                //再次查询
                oldEndUserVisitedStoreEntity = this.findFirstByOpenIdAndStoreId(openId, storeId);
            }
        } else {
            this.updateVisitedTimeById(oldEndUserVisitedStoreEntity.getId(), new Date());
        }
        return oldEndUserVisitedStoreEntity;
    }


    @Override
    public List<EndUserVisitedStoreResp> findAllVisitedStoresByOpenIdCode(EndUserVistiedStoreRequest endUserVistiedStoreRequest) {
        log.info("查询用户浏览的门店入参：{}", JSONObject.toJSONString(endUserVistiedStoreRequest));
        if (StringUtils.isEmpty(endUserVistiedStoreRequest.getOpenIdCode())) {
            log.error("记录用户浏览的门店入参错误：{}", JSONObject.toJSONString(endUserVistiedStoreRequest));
            throw new StoreSaasMarketingException("记录用户浏览的门店入参错误");
        }
        String clientType = endUserVistiedStoreRequest.getClientType();
        if (StringUtils.isEmpty(clientType)) {
            clientType = "end_user_client";
        }
        OauthClientDetailsDAO oauthClientDetails = iOauthClientDetailsService.getClientDetailByClientId(clientType);
        if (null == oauthClientDetails) {
            log.error("客户端配置信息不存在，clientType={}", clientType);
            throw new StoreSaasMarketingException("客户端配置信息不存在");
        }
        String openId = iWechatService.getOpenId(oauthClientDetails.getWxAppid()
                , oauthClientDetails.getWxSecret()
                , endUserVistiedStoreRequest.getOpenIdCode(),
                oauthClientDetails.getWxOpenidUrl());
        List<EndUserVisitedStoreEntity> endUserVisitedStoreEntities = this.findAllByOpenId(openId);
        List<EndUserVisitedStoreReq> endUserVisitedStoreReqList = new ArrayList<>();
        if (!CollectionUtils.isEmpty(endUserVisitedStoreEntities)) {
            for (EndUserVisitedStoreEntity endUserVisitedStoreEntity : endUserVisitedStoreEntities) {
                String storeId = endUserVisitedStoreEntity.getStoreId();
                if (org.apache.commons.lang3.StringUtils.isNotBlank(storeId) && org.apache.commons.lang3.StringUtils.isNumeric(storeId)) {
                    EndUserVisitedStoreReq endUserVisitedStoreReq = new EndUserVisitedStoreReq();
                    endUserVisitedStoreReq.setStoreId(storeId);
                    endUserVisitedStoreReq.setCount(endUserVisitedStoreEntity.getCount());
                    endUserVisitedStoreReq.setLastTime(endUserVisitedStoreEntity.getUpdateTime());
                    endUserVisitedStoreReqList.add(endUserVisitedStoreReq);
                }
            }
        }
        List<EndUserVisitedStoreResp> endUserVisitedStoreResps = new ArrayList<>();
        if (!CollectionUtils.isEmpty(endUserVisitedStoreReqList)) {
            List<StoreSimpleInfoResp> storeSimpleInfoResps;
            try {
                log.info("根据门店Id列表查询门店信息，入参：" + JSONObject.toJSONString(endUserVisitedStoreReqList));
                BizBaseResponse<List<StoreSimpleInfoResp>> resultObject = storeUserClient.getHistoryStoreList(endUserVisitedStoreReqList);
                log.info("根据门店Id列表查询门店信息，返回：" + JSONObject.toJSONString(resultObject));
                storeSimpleInfoResps = resultObject != null ? resultObject.getData() : null;
                if (!CollectionUtils.isEmpty(storeSimpleInfoResps)) {
                    for (StoreSimpleInfoResp storeSimpleInfoResp : storeSimpleInfoResps) {
                        EndUserVisitedStoreResp endUserVisitedStoreResp = new EndUserVisitedStoreResp();
                        BeanUtils.copyProperties(storeSimpleInfoResp, endUserVisitedStoreResp);
                        endUserVisitedStoreResps.add(endUserVisitedStoreResp);
                    }
                }
            } catch (Exception e) {
                log.error("根据门店Id列表查询门店信息异常，入参：" + JSONObject.toJSONString(endUserVisitedStoreReqList), e);
            }
        }
        return endUserVisitedStoreResps;
    }

}
