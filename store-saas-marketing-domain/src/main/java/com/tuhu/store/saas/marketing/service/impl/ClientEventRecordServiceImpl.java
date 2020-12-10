package com.tuhu.store.saas.marketing.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.tuhu.store.saas.marketing.dataobject.ClientEventRecordDAO;
import com.tuhu.store.saas.marketing.dataobject.OauthClientDetailsDAO;
import com.tuhu.store.saas.marketing.entity.EndUserVisitedCouponEntity;
import com.tuhu.store.saas.marketing.entity.EndUserVisitedStoreEntity;
import com.tuhu.store.saas.marketing.enums.EventContentTypeEnum;
import com.tuhu.store.saas.marketing.exception.MarketingException;
import com.tuhu.store.saas.marketing.mysql.marketing.write.dao.ClientEventRecordMapper;
import com.tuhu.store.saas.marketing.request.ClientEventRecordReq;
import com.tuhu.store.saas.marketing.request.ClientEventRecordRequest;
import com.tuhu.store.saas.marketing.request.EndUserVistiedCouponRequest;
import com.tuhu.store.saas.marketing.response.ClientEventRecordResp;
import com.tuhu.store.saas.marketing.service.*;
import com.tuhu.store.saas.user.dto.ClientEventRecordDTO;
import com.tuhu.store.saas.user.vo.ClientEventRecordVO;
import com.tuhu.store.saas.user.vo.EventTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.*;

/**
 * C端用户事件记录服务实现类
 */
@Service
@Slf4j
public class ClientEventRecordServiceImpl implements IClientEventRecordService {

    @Autowired
    private IEndUserVisitedStoreService iEndUserVisitedStoreService;

    @Autowired
    private IEndUserVisitedCouponService iEndUserVisitedCouponService;

    @Autowired
    private IOauthClientDetailsService iOauthClientDetailsService;

    @Autowired
    private IWechatService iWechatService;

    @Autowired
    private ClientEventRecordMapper clientEventRecordMapper;

    @Override
    public Integer addNewClientEventRecord(ClientEventRecordDAO clientEventRecordEntity) {
        return clientEventRecordMapper.insert(clientEventRecordEntity);
    }

    @Override
    @Transactional
    public void recordEndUserEvent(ClientEventRecordRequest clientEventRecordRequest) {
        log.info("记录用户行为入参：{}", JSONObject.toJSONString(clientEventRecordRequest));
        String validateResult = this.validateClientEventRecordRequest(clientEventRecordRequest);
        if (null != validateResult) {
            throw new RuntimeException(validateResult);
        }
        String openId = clientEventRecordRequest.getOpenId();
        Integer sourceType = clientEventRecordRequest.getSourceType();
        //获取微信用户openId
        if (StringUtils.isEmpty(openId) && sourceType == 0) {
            String clientType = clientEventRecordRequest.getClientType();
            if (StringUtils.isEmpty(clientType)) {
                clientType = "end_user_client";
            }
            OauthClientDetailsDAO oauthClientDetails = iOauthClientDetailsService.getClientDetailByClientId(clientType);
            if (null == oauthClientDetails) {
                log.error("客户端配置信息不存在，clientType={}", clientType);
                throw new RuntimeException("客户端配置信息不存在");
            }

            openId = iWechatService.getOpenId(oauthClientDetails.getWxAppid()
                    , oauthClientDetails.getWxSecret()
                    , clientEventRecordRequest.getOpenIdCode(),
                    oauthClientDetails.getWxOpenidUrl());
            clientEventRecordRequest.setOpenId(openId);
        }
        String storeId = clientEventRecordRequest.getStoreId();
        //首选查询现有记录
        String contentValue = null;
        if (EventContentTypeEnum.STORE.getCode().equals(clientEventRecordRequest.getContentType())) {
            contentValue = clientEventRecordRequest.getStoreId();
        } else if (EventContentTypeEnum.COUPON.getCode().equals(clientEventRecordRequest.getContentType())) {
            contentValue = clientEventRecordRequest.getEncryptedCode();
        }
        if (StringUtils.isEmpty(contentValue)) {
            contentValue = clientEventRecordRequest.getContentValue();
        }
        ClientEventRecordDAO oldClientEventRecordEntity = this.getEventRecordByParams(clientEventRecordRequest.getEventType(), clientEventRecordRequest.getContentType(), contentValue, openId, sourceType);
        //如果没有记录
        if (null == oldClientEventRecordEntity || StringUtils.isEmpty(oldClientEventRecordEntity.getId())) {
            ClientEventRecordDAO clientEventRecordEntity = new ClientEventRecordDAO();
            clientEventRecordEntity.setId(UUID.randomUUID().toString());
            clientEventRecordEntity.setEventType(clientEventRecordRequest.getEventType());
            clientEventRecordEntity.setContentType(clientEventRecordRequest.getContentType());
            clientEventRecordEntity.setContentValue(contentValue);
            clientEventRecordEntity.setOpenId(openId);
            clientEventRecordEntity.setStoreId(clientEventRecordRequest.getStoreId());
            Date date = new Date();
            clientEventRecordEntity.setCreateTime(date);
            clientEventRecordEntity.setUpdateTime(date);
            clientEventRecordEntity.setEventCount(1);
            clientEventRecordEntity.setSourceType(sourceType);
            clientEventRecordEntity.setPhoneNumber(clientEventRecordRequest.getPhoneNumber());
            try {
                this.addNewClientEventRecord(clientEventRecordEntity);
            } catch (Exception e) {
                log.error("C端客户行为新增记录失败,ClientEventRecordEntity={},error={}", JSONObject.toJSONString(clientEventRecordEntity), ExceptionUtils.getStackTrace(e));
                //再次查询
                oldClientEventRecordEntity = this.getEventRecordByParams(clientEventRecordRequest.getEventType(), clientEventRecordRequest.getContentType(), contentValue, openId, sourceType);
                this.updateClientEventRecordCountById(oldClientEventRecordEntity.getId());
            }
        } else {
            this.updateClientEventRecordCountById(oldClientEventRecordEntity.getId());
        }
        //如果是访问门店或优惠券
//        if (EventTypeEnum.VISIT.getCode().equals(clientEventRecordRequest.getEventType())) {
//            if (EventContentTypeEnum.STORE.getCode().equals(clientEventRecordRequest.getContentType())) {
//                EndUserVisitedStoreEntity endUserVisitedStoreEntity = new EndUserVisitedStoreEntity();
//                endUserVisitedStoreEntity.setOpenId(openId);
//                endUserVisitedStoreEntity.setStoreId(storeId);
//                iEndUserVisitedStoreService.recordEndUserVistiedStore(endUserVisitedStoreEntity);
//            } else if (EventContentTypeEnum.COUPON.getCode().equals(clientEventRecordRequest.getContentType()) && sourceType == 0
//                    && StringUtils.isNotBlank(clientEventRecordRequest.getCustomerId()) && StringUtils.isNotBlank(storeId)) {
//                EndUserVisitedCouponEntity endUserVisitedCouponEntity = new EndUserVisitedCouponEntity();
//                endUserVisitedCouponEntity.setOpenId(openId);
//                endUserVisitedCouponEntity.setStoreId(storeId);
//                endUserVisitedCouponEntity.setCouponCode(contentValue);
//                iEndUserVisitedCouponService.recordEndUserVistiedCoupon(endUserVisitedCouponEntity);
//            }
//        } else if (EventTypeEnum.LOGIN.getCode().equals(clientEventRecordRequest.getEventType()) || EventTypeEnum.REGISTERED.getCode().equals(clientEventRecordRequest.getEventType())) {
//            if (EventContentTypeEnum.COUPON.getCode().equals(clientEventRecordRequest.getContentType())
//                    && StringUtils.isNotBlank(clientEventRecordRequest.getCustomerId()) && StringUtils.isNotBlank(storeId)) {
//                EndUserVistiedCouponRequest endUserVistiedCouponRequest = new EndUserVistiedCouponRequest();
//                endUserVistiedCouponRequest.setEncryptedCode(contentValue);
//                endUserVistiedCouponRequest.setOpenId(openId);
//                endUserVistiedCouponRequest.setStoreId(storeId);
//                iEndUserVisitedCouponService.recordNewCustomerByVistiedCoupon(endUserVistiedCouponRequest, clientEventRecordRequest.getCustomerId());
//            }
//        }
    }

    /**
     * 校验输入
     *
     * @param clientEventRecordRequest
     * @return
     */
    private String validateClientEventRecordRequest(ClientEventRecordRequest clientEventRecordRequest) {
        if (StringUtils.isEmpty(clientEventRecordRequest.getStoreId())) {
            return "门店ID不能为空";
        }
        if (StringUtils.isEmpty(clientEventRecordRequest.getOpenId()) && StringUtils.isEmpty(clientEventRecordRequest.getOpenIdCode()) && clientEventRecordRequest.getSourceType() == 0) {
            return "微信小程序code和openId不能同时为空";
        }
//        if (StringUtils.isEmpty(clientEventRecordRequest.getClientType())) {
//            return "客户端类型不能为空";
//        }
        if (StringUtils.isEmpty(clientEventRecordRequest.getEventType())) {
            return "事件类型不能为空";
        }
        if (StringUtils.isEmpty(clientEventRecordRequest.getContentType())) {
            return "主题类型不能为空";
        }
        if (EventContentTypeEnum.COUPON.getCode().equals(clientEventRecordRequest.getContentType())) {
            if (StringUtils.isEmpty(clientEventRecordRequest.getEncryptedCode()) && StringUtils.isEmpty(clientEventRecordRequest.getContentValue())) {
                return "优惠券编码不能为空";
            }
        } else if (!EventContentTypeEnum.STORE.getCode().equals(clientEventRecordRequest.getContentType())) {
            if (StringUtils.isEmpty(clientEventRecordRequest.getContentValue())) {
                return "主题值不能为空";
            }
        } else if (EventContentTypeEnum.ACTIVITY.getCode().equals(clientEventRecordRequest.getContentType())) {
            if (StringUtils.isEmpty(clientEventRecordRequest.getEncryptedCode()) && StringUtils.isEmpty(clientEventRecordRequest.getContentValue())) {
                return "活动编码不能为空";
            }
        } else if (EventContentTypeEnum.SEC_KILL.getCode().equals(clientEventRecordRequest.getContentType())) {
            if (StringUtils.isEmpty(clientEventRecordRequest.getEncryptedCode()) && StringUtils.isEmpty(clientEventRecordRequest.getContentValue())) {
                return "秒杀活动编码不能为空";
            }
        }
        return null;
    }

    @Override
    public void updateClientEventRecordCountById(String id) {
//        clientEventRecordEntityDao.updateClientEventRecordCountById(id, date);
        EntityWrapper<ClientEventRecordDAO> wrapper = new EntityWrapper<>();
        wrapper.eq("id", id);
        ClientEventRecordDAO recordDAO = clientEventRecordMapper.selectById(id);
        if (recordDAO.getEventCount() == null) {
            return;
        }
        recordDAO.setEventCount(recordDAO.getEventCount() + 1);
        recordDAO.setUpdateTime(new Date());
        clientEventRecordMapper.update(recordDAO, wrapper);
    }

    @Override
    public ClientEventRecordDAO getEventRecordByParams(String eventType, String contentType, String contentValue, String openId, Integer sourceType) {
        EntityWrapper<ClientEventRecordDAO> wrapper = new EntityWrapper<>();
        wrapper.eq("event_type", eventType)
                .eq("content_type", contentType)
                .eq("content_value", contentValue)
                .eq("source_type", sourceType);
        if (StringUtils.isNotEmpty(openId)) {
            wrapper.eq("open_id", openId);
        }
        List<ClientEventRecordDAO> recordDAOList = clientEventRecordMapper.selectList(wrapper);
        if (CollectionUtils.isEmpty(recordDAOList)) {
            return null;
        }
        return recordDAOList.get(0);
    }

    @Override
    public Map<String, ClientEventRecordDTO> getClientEventRecordStatisticsByEvent(ClientEventRecordVO clientEventRecordVO) {
        if (null == clientEventRecordVO) {
            return null;
        }
        ClientEventRecordReq clientEventRecordReq = new ClientEventRecordReq();
        BeanUtils.copyProperties(clientEventRecordVO, clientEventRecordReq);
        try {
            Map<String, ClientEventRecordResp> clientEventRecordRespMap = this.getClientEventRecordStatisticsByEvent(clientEventRecordReq);
            if (MapUtils.isNotEmpty(clientEventRecordRespMap)) {
                Map<String, ClientEventRecordDTO> clientEventRecordDTOMap = new HashMap<>();
                for (Map.Entry<String, ClientEventRecordResp> clientEventRecordRespEntry : clientEventRecordRespMap.entrySet()) {
                    String eventType = clientEventRecordRespEntry.getKey();
                    ClientEventRecordResp clientEventRecordResp = clientEventRecordRespEntry.getValue();
                    ClientEventRecordDTO clientEventRecordDTO = new ClientEventRecordDTO();
                    BeanUtils.copyProperties(clientEventRecordResp, clientEventRecordDTO);
                    clientEventRecordDTOMap.put(eventType, clientEventRecordDTO);
                }
                return clientEventRecordDTOMap;
            }
        } catch (MarketingException ue) {
            log.error("根据指定的事件类型及主题类型获取统计数据异常，request={}，errorMsg={}", JSONObject.toJSONString(clientEventRecordVO), ue.getMessage());
            throw new MarketingException(ue.getMessage());
        } catch (Exception e) {
            log.error("根据指定的事件类型及主题类型获取统计数据异常，request={}，errorMsg={}", JSONObject.toJSONString(clientEventRecordVO), ExceptionUtils.getStackTrace(e));
            throw new MarketingException(e);
        }
        return null;
    }

    public Map<String, ClientEventRecordResp> getClientEventRecordStatisticsByEvent(ClientEventRecordReq clientEventRecordReq) {
        if (null == clientEventRecordReq) {
            throw new MarketingException("入参不能为空");
        }
        if (org.apache.commons.collections.CollectionUtils.isEmpty(clientEventRecordReq.getEventTypes())) {
            throw new MarketingException("事件类型不能为空");
        }
        if (org.springframework.util.StringUtils.isEmpty(clientEventRecordReq.getContentType())) {
            throw new MarketingException("主题类型不能为空");
        }
        if (org.springframework.util.StringUtils.isEmpty(clientEventRecordReq.getContentValue())) {
            throw new MarketingException("主题值不能为空");
        }
        if (org.springframework.util.StringUtils.isEmpty(clientEventRecordReq.getStoreId())) {
            throw new MarketingException("门店ID不能为空");
        }
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("eventTypes", clientEventRecordReq.getEventTypes());
        paramMap.put("contentType", clientEventRecordReq.getContentType());
        paramMap.put("contentValue", clientEventRecordReq.getContentValue());
        paramMap.put("storeId", clientEventRecordReq.getStoreId());
        List<Map<String, Object>> statisticsByEvents = clientEventRecordMapper.getClientEventRecordStatisticsByEvent(paramMap);
        if (org.apache.commons.collections.CollectionUtils.isNotEmpty(statisticsByEvents)) {
            Map<String, ClientEventRecordResp> clientEventRecordRespMap = new HashMap<>();
            for (Map<String, Object> statisticsByEvent : statisticsByEvents) {
                ClientEventRecordResp clientEventRecordResp = new ClientEventRecordResp();
                clientEventRecordResp.setStoreId(clientEventRecordReq.getStoreId());
                clientEventRecordResp.setContentType(clientEventRecordReq.getContentType());
                clientEventRecordResp.setContentValue(clientEventRecordReq.getContentValue());
                String eventType = String.valueOf(statisticsByEvent.get("eventType"));
                clientEventRecordResp.setEventType(eventType);
                Object eventCountObj = statisticsByEvent.get("eventCount");
                Long eventCount = null;
                if (null == eventCountObj) {
                    eventCount = Long.valueOf(0);
                } else {
                    eventCount = Long.valueOf(String.valueOf(eventCountObj));
                }
                Object userCountObj = statisticsByEvent.get("userCount");
                Long userCount = null;
                if (null == userCountObj) {
                    userCount = Long.valueOf(0);
                } else {
                    userCount = Long.valueOf(String.valueOf(userCountObj));
                }
                clientEventRecordResp.setEventCount(eventCount);
                clientEventRecordResp.setUserCount(userCount);
                clientEventRecordRespMap.put(eventType, clientEventRecordResp);
            }
            return clientEventRecordRespMap;
        }
        return null;
    }
}
