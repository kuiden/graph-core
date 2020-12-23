package com.tuhu.store.saas.marketing.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.tuhu.base.service.impl.BaseServiceImpl;
import com.tuhu.store.saas.marketing.dataobject.OauthClientDetailsDAO;
import com.tuhu.store.saas.marketing.entity.EndUserVisitedCouponEntity;
import com.tuhu.store.saas.marketing.exception.StoreSaasMarketingException;
import com.tuhu.store.saas.marketing.mysql.marketing.write.dao.EndUserVisitedCouponWriteMapper;
import com.tuhu.store.saas.marketing.request.EndUserVistiedCouponRequest;
import com.tuhu.store.saas.marketing.service.IEndUserVisitedCouponService;
import com.tuhu.store.saas.marketing.service.IOauthClientDetailsService;
import com.tuhu.store.saas.marketing.service.IWechatService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.UUID;

/**
 * <p>
 * 车主端用户访问的优惠券记录 服务实现类
 * </p>
 *
 * @author someone
 * @since 2020-08-03
 */
@Service
@Slf4j
public class EndUserVisitedCouponServiceImpl extends BaseServiceImpl<EndUserVisitedCouponWriteMapper, EndUserVisitedCouponEntity> implements IEndUserVisitedCouponService {


//    @Autowired
//    private EndUserVisitedCouponWriteMapper endUserVisitedCouponWriteMapper;

    @Autowired
    private IOauthClientDetailsService iOauthClientDetailsService;

    @Autowired
    private IWechatService iWechatService;

    @Override
    public EndUserVisitedCouponEntity findFirstByOpenIdAndCouponCode(String openId, String couponCode) {
        return baseMapper.findFirstByOpenIdAndCouponCode(openId, couponCode);
    }

    @Override
    public Integer addNewEndUserVisitedCoupon(EndUserVisitedCouponEntity endUserVisitedCouponEntity) {
        return baseMapper.insert(endUserVisitedCouponEntity);
    }

    @Override
    @Transactional
    public void recordNewCustomerByVistiedCoupon(EndUserVistiedCouponRequest endUserVistiedCouponRequest, String customerId) {
        //查找当前用户的优惠券浏览记录
        EndUserVisitedCouponEntity endUserVisitedCouponEntity = this.findFirstByOpenIdAndCouponCode(endUserVistiedCouponRequest.getOpenId(), endUserVistiedCouponRequest.getEncryptedCode());
        Date date = new Date();
        if (null == endUserVisitedCouponEntity || StringUtils.isEmpty(endUserVisitedCouponEntity.getId())) {
            endUserVisitedCouponEntity = new EndUserVisitedCouponEntity();
            endUserVisitedCouponEntity.setId(UUID.randomUUID().toString());
            endUserVisitedCouponEntity.setOpenId(endUserVistiedCouponRequest.getOpenId());
            endUserVisitedCouponEntity.setStoreId(endUserVistiedCouponRequest.getStoreId());
            endUserVisitedCouponEntity.setCouponCode(endUserVistiedCouponRequest.getEncryptedCode());
            endUserVisitedCouponEntity.setCreateTime(date);
        } else if (!StringUtils.isEmpty(endUserVisitedCouponEntity.getCustomerId())) {
            return;
        }
        endUserVisitedCouponEntity.setCustomerId(customerId);
        endUserVisitedCouponEntity.setRegisteredTime(date);
        baseMapper.insert(endUserVisitedCouponEntity);
    }


    @Override
    public void recordEndUserVistiedCoupon(EndUserVistiedCouponRequest endUserVistiedCouponRequest) {
        log.info("记录用户浏览的优惠券入参：{}", JSONObject.toJSONString(endUserVistiedCouponRequest));
        if (StringUtils.isEmpty(endUserVistiedCouponRequest.getEncryptedCode()) || StringUtils.isEmpty(endUserVistiedCouponRequest.getOpenIdCode())) {
            log.error("记录用户浏览的门店入参错误：{}", JSONObject.toJSONString(endUserVistiedCouponRequest));
            throw new StoreSaasMarketingException("记录用户浏览的门店入参错误");
        }
        String clientType = endUserVistiedCouponRequest.getClientType();
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
                , endUserVistiedCouponRequest.getOpenIdCode(),
                oauthClientDetails.getWxOpenidUrl());
        String couponCode = endUserVistiedCouponRequest.getEncryptedCode();
        //首选查询现有记录
        EndUserVisitedCouponEntity endUserVisitedCouponEntity = this.findFirstByOpenIdAndCouponCode(openId, couponCode);
        //如果没有记录
        if (null == endUserVisitedCouponEntity || StringUtils.isEmpty(endUserVisitedCouponEntity.getId())) {
            endUserVisitedCouponEntity = new EndUserVisitedCouponEntity();
            endUserVisitedCouponEntity.setId(UUID.randomUUID().toString());
            endUserVisitedCouponEntity.setOpenId(openId);
            endUserVisitedCouponEntity.setStoreId(endUserVistiedCouponRequest.getStoreId());
            endUserVisitedCouponEntity.setCouponCode(couponCode);
            Date date = new Date();
            endUserVisitedCouponEntity.setCreateTime(date);
            try {
                this.addNewEndUserVisitedCoupon(endUserVisitedCouponEntity);
            } catch (Exception e) {
                log.warn("相同的openId和storeId记录已存在", e);
            }
        }
    }


    @Override
    public void recordEndUserVistiedCoupon(EndUserVisitedCouponEntity endUserVisitedCouponEntity) {
        log.info("记录用户浏览的优惠券入参：{}", JSONObject.toJSONString(endUserVisitedCouponEntity));
        if (StringUtils.isEmpty(endUserVisitedCouponEntity.getCouponCode()) || StringUtils.isEmpty(endUserVisitedCouponEntity.getOpenId())) {
            log.error("记录用户浏览的门店入参错误：{}", JSONObject.toJSONString(endUserVisitedCouponEntity));
            throw new StoreSaasMarketingException("记录用户浏览的门店入参错误");
        }
        String openId = endUserVisitedCouponEntity.getOpenId();
        String couponCode = endUserVisitedCouponEntity.getCouponCode();
        //首选查询现有记录
        EndUserVisitedCouponEntity oldEndUserVisitedCouponEntity = this.findFirstByOpenIdAndCouponCode(openId, couponCode);
        //如果没有记录
        if (null == oldEndUserVisitedCouponEntity || StringUtils.isEmpty(oldEndUserVisitedCouponEntity.getId())) {
            endUserVisitedCouponEntity.setId(UUID.randomUUID().toString());
            endUserVisitedCouponEntity.setCreateTime(new Date());
            try {
                this.addNewEndUserVisitedCoupon(endUserVisitedCouponEntity);
            } catch (Exception e) {
                log.warn("相同的openId和couponCode记录已存在", e);
            }
        }
    }
}
