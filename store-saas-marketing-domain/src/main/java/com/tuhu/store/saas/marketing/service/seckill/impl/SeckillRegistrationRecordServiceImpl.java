package com.tuhu.store.saas.marketing.service.seckill.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.mengfan.common.response.fianace.PaymentResponse;
import com.tuhu.boot.common.enums.BizErrorCodeEnum;
import com.tuhu.boot.common.exceptions.BizException;
import com.tuhu.boot.common.facade.BizBaseResponse;
import com.tuhu.springcloud.common.bean.BeanUtil;
import com.tuhu.springcloud.common.util.RedisUtils;
import com.tuhu.store.saas.crm.dto.CustomerDTO;
import com.tuhu.store.saas.crm.vo.*;
import com.tuhu.store.saas.marketing.constant.SeckillConstant;
import com.tuhu.store.saas.marketing.context.UserContextHolder;
import com.tuhu.store.saas.marketing.dataobject.ClientEventRecordDAO;
import com.tuhu.store.saas.marketing.dataobject.SeckillActivity;
import com.tuhu.store.saas.marketing.dataobject.SeckillRegistrationRecord;
import com.tuhu.store.saas.marketing.enums.SeckillActivitySellTypeEnum;
import com.tuhu.store.saas.marketing.enums.SeckillActivityStatusEnum;
import com.tuhu.store.saas.marketing.enums.SeckillRegistrationRecordPayStatusEnum;
import com.tuhu.store.saas.marketing.enums.ShoppingPlatformEnum;
import com.tuhu.store.saas.marketing.exception.StoreSaasMarketingException;
import com.tuhu.store.saas.marketing.mysql.marketing.write.dao.ClientEventRecordMapper;
import com.tuhu.store.saas.marketing.mysql.marketing.write.dao.SeckillRegistrationRecordMapper;
import com.tuhu.store.saas.marketing.remote.crm.CustomerClient;
import com.tuhu.store.saas.marketing.remote.crm.StoreInfoClient;
import com.tuhu.store.saas.marketing.remote.order.StoreReceivingClient;
import com.tuhu.store.saas.marketing.remote.order.TradeOrderClient;
import com.tuhu.store.saas.marketing.remote.request.AddVehicleReq;
import com.tuhu.store.saas.marketing.remote.request.CustomerReq;
import com.tuhu.store.saas.marketing.request.card.AddCardOrderReq;
import com.tuhu.store.saas.marketing.request.seckill.*;
import com.tuhu.store.saas.marketing.response.seckill.SeckillActivityStatisticsResp;
import com.tuhu.store.saas.marketing.response.seckill.SeckillRegistrationRecordResp;
import com.tuhu.store.saas.marketing.service.ICardOrderService;
import com.tuhu.store.saas.marketing.service.seckill.PayService;
import com.tuhu.store.saas.marketing.service.seckill.SeckillActivityService;
import com.tuhu.store.saas.marketing.service.seckill.SeckillRegistrationRecordService;
import com.tuhu.store.saas.marketing.util.CodeFactory;
import com.tuhu.store.saas.marketing.util.DateUtils;
import com.tuhu.store.saas.marketing.util.IdKeyGen;
import com.tuhu.store.saas.marketing.util.StoreRedisUtils;
import com.tuhu.store.saas.order.dto.finance.receiving.TradeOrderDTO;
import com.tuhu.store.saas.order.enums.*;
import com.tuhu.store.saas.order.vo.finance.receiving.AddReceivingVO;
import com.tuhu.store.saas.user.dto.StoreDTO;
import com.tuhu.store.saas.user.vo.StoreInfoVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.curator.shaded.com.google.common.collect.Maps;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

/**
 * <p>
 * 秒杀报名记录表  服务实现类
 * </p>
 *
 * @author yangshengyong
 * @since 2020-12-02
 */
@Service
@Slf4j
public class SeckillRegistrationRecordServiceImpl extends ServiceImpl<SeckillRegistrationRecordMapper, SeckillRegistrationRecord> implements SeckillRegistrationRecordService {
    @Value("${seckill.activity.cancel.num:200}")
    private Integer NUM;

    @Autowired
    private SeckillActivityService seckillActivityService;

    @Autowired
    private StoreReceivingClient storeReceivingClient;

    @Autowired
    private TradeOrderClient tradeOrderClient;

    @Autowired
    private StoreRedisUtils storeRedisUtils;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private CodeFactory codeFactory;

    @Autowired
    IdKeyGen idKeyGen;

    @Autowired
    private CustomerClient customerClient;

    @Autowired
    private StoreInfoClient storeInfoClient;

    @Autowired
    private ICardOrderService cardOrderService;

    @Autowired
    private ClientEventRecordMapper clientEventRecordMapper;

    @Autowired
    private PayService payService;

    private Lock lock = new ReentrantLock();

    /**
     * 活动对应的支付成功的订单
     *
     * @param activityIds
     * @return
     */
    @Override
    public Map<String, Integer> activityIdNumMap(List<String> activityIds) {
        log.info("activityIdNumMap{}", JSON.toJSONString(activityIds));
        EntityWrapper<SeckillRegistrationRecord> wrapper = new EntityWrapper<>();
        wrapper.in(SeckillRegistrationRecord.SECKILL_ACTIVITY_ID, activityIds);
        wrapper.eq(SeckillRegistrationRecord.PAY_STATUS, SeckillConstant.PAY_SUCCESS_STATUS);
        wrapper.eq(SeckillRegistrationRecord.IS_DELETE, SeckillConstant.TYPE);
        List<SeckillRegistrationRecord> list = this.selectList(wrapper);
        if (CollectionUtils.isNotEmpty(list)) {
            Map<String, Integer> activityIdNumMap = new HashMap<>();
            Map<String, List<SeckillRegistrationRecord>> activityIdListMap = list.stream().collect(Collectors.groupingBy(SeckillRegistrationRecord::getSeckillActivityId));
            for (Map.Entry<String, List<SeckillRegistrationRecord>> entry : activityIdListMap.entrySet()) {
                Long num = entry.getValue().stream().filter(x -> x.getQuantity() != null).mapToLong(SeckillRegistrationRecord::getQuantity).sum();
                activityIdNumMap.put(entry.getKey(), null == num ? 0 : num.intValue());
            }
            return activityIdNumMap;
        }
        return Collections.EMPTY_MAP;
    }

    @Override
    public PageInfo<SeckillRegistrationRecordResp> pageBuyList(SeckillActivityReq req) {
        log.info("pageBuyList{}", JSON.toJSONString(req));
        PageInfo<SeckillRegistrationRecordResp> responsePageInfo = new PageInfo<>();
        PageHelper.startPage(req.getPageNum(), req.getPageSize());
        //查询报名记录购买记录
        PageInfo<SeckillRegistrationRecord> pageInfo = new PageInfo<>(this.baseMapper.pageBuyList(req.getTenantId(), req.getStoreId(), req.getSeckillActivityId(), req.getPhone()));
        List<SeckillRegistrationRecord> list = pageInfo.getList();
        List<SeckillRegistrationRecordResp> responseList = Lists.newArrayList();
        if (null != pageInfo && CollectionUtils.isNotEmpty(list)) {
            List<String> customerIds = new ArrayList<>();
            for (SeckillRegistrationRecord record : list) {
                customerIds.add(record.getCustomerId());
            }
            Map<String, Integer> customerIdNewMap = this.customerIdNewMap(customerIds, req.getSeckillActivityId());
            responseList = list.stream().map(o -> {
                SeckillRegistrationRecordResp response = new SeckillRegistrationRecordResp();
                BeanUtils.copyProperties(o, response);
                dataConversion(o, response, customerIdNewMap);
                return response;
            }).collect(Collectors.toList());
        }
        BeanUtil.copyProperties(pageInfo, responsePageInfo);
        responsePageInfo.setList(responseList);
        return responsePageInfo;
    }

    @Override
    public PageInfo<SeckillRegistrationRecordResp> pageNoBuyBrowseList(SeckillActivityReq req) {
        log.info("pageNoBuyBrowseList{}", JSON.toJSONString(req));
        PageInfo<SeckillRegistrationRecordResp> responsePageInfo = new PageInfo<>();
        PageHelper.startPage(req.getPageNum(), req.getPageSize());
        //查询报名未购买浏览记录
        PageInfo<SeckillRegistrationRecord> pageInfo = new PageInfo<>(this.baseMapper.pageNoBuyBrowseList(req.getTenantId(), req.getStoreId(), req.getSeckillActivityId(), req.getPhone()));
        List<SeckillRegistrationRecord> list = pageInfo.getList();
        List<SeckillRegistrationRecordResp> responseList = Lists.newArrayList();
        if (null != pageInfo && CollectionUtils.isNotEmpty(list)) {
            List<String> customerIds = new ArrayList<>();
            for (SeckillRegistrationRecord record : list) {
                customerIds.add(record.getCustomerId());
            }
            Map<String, Integer> customerIdNewMap = this.customerIdNewMap(customerIds, req.getSeckillActivityId());
            responseList = list.stream().map(o -> {
                SeckillRegistrationRecordResp response = new SeckillRegistrationRecordResp();
                BeanUtils.copyProperties(o, response);
                dataConversion(o, response, customerIdNewMap);
                return response;
            }).collect(Collectors.toList());
        }
        BeanUtil.copyProperties(pageInfo, responsePageInfo);
        responsePageInfo.setList(responseList);
        return responsePageInfo;
    }

    @Override
    public PageInfo<SeckillRegistrationRecordResp> pageBuyRecodeList(SeckillActivityReq req) {
        log.info("pageBuyRecodeList{}", JSON.toJSONString(req));
        seckillActivityService.check(req.getSeckillActivityId(), Boolean.TRUE);
        PageInfo<SeckillRegistrationRecordResp> responsePageInfo = new PageInfo<>();
        PageHelper.startPage(req.getPageNum(), req.getPageSize());
        EntityWrapper<SeckillRegistrationRecord> wrapper = new EntityWrapper<>();
        wrapper.eq(SeckillRegistrationRecord.SECKILL_ACTIVITY_ID, req.getSeckillActivityId());
        wrapper.eq(SeckillRegistrationRecord.PAY_STATUS, SeckillConstant.PAY_SUCCESS_STATUS);
        wrapper.eq(SeckillRegistrationRecord.STORE_ID, req.getStoreId());
        wrapper.eq(SeckillRegistrationRecord.TENANT_ID, req.getTenantId());
        wrapper.orderBy(SeckillRegistrationRecord.PAYMENT_TIME, Boolean.FALSE);
        PageInfo<SeckillRegistrationRecord> pageInfo = new PageInfo<>(this.selectList(wrapper));
        List<SeckillRegistrationRecord> list = pageInfo.getList();
        List<SeckillRegistrationRecordResp> responseList = Lists.newArrayList();
        if (null != pageInfo && CollectionUtils.isNotEmpty(list)) {
            responseList = list.stream().map(o -> {
                SeckillRegistrationRecordResp response = new SeckillRegistrationRecordResp();
                BeanUtils.copyProperties(o, response);
                //车牌号脱敏
                if (StringUtils.isNotBlank(response.getVehicleNumber())) {
                    int length = response.getVehicleNumber().length();
                    String plateFlag = response.getVehicleNumber().substring(0, 1);
                    String plateNumber = response.getVehicleNumber().substring(length - 1, length);
                    response.setVehicleNumber(plateFlag + "******" + plateNumber);
                }
                return response;
            }).collect(Collectors.toList());
        }
        BeanUtil.copyProperties(pageInfo, responsePageInfo);
        responsePageInfo.setList(responseList);
        return responsePageInfo;
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> customerActivityOrderAdd(SeckillRecordAddReq req, ShoppingPlatformEnum shoppingPlatformEnum) {
        Map<String, Object> returnMap = com.google.common.collect.Maps.newHashMap();
        log.info("customerActivityOrderAdd：{}", JSON.toJSONString(req));
        //校验
        this.checkSeckillRecordAddReqParam(req, shoppingPlatformEnum);
        Long storeId = req.getStoreId();
        //生成秒杀活动编码
        String codeNumber = codeFactory.getCodeNumber(CodeFactory.SECKILL_ACTIVITY_PREFIX_CODE, storeId);
        String seckillOrderCode = codeFactory.generateSeckillActivityCode(storeId, codeNumber);
        Object obj = storeRedisUtils.getAtomLock(seckillOrderCode, 3);
        if (Objects.nonNull(obj)) {
            try {
                //写入抢购报名（订单）表数据
                SeckillRegistrationRecord seckillRegistrationRecord = new SeckillRegistrationRecord();
                BeanUtils.copyProperties(req, seckillRegistrationRecord);


                //将未收款的待收单+交易单作废
                this.updateReceivingAndTradeOrder(seckillRegistrationRecord, SeckillConstant.CANCEL_STATUS);
                //根据秒杀订单新建客户
                Map<String, CustomerReq> maps = this.addCustomerForOrder(seckillRegistrationRecord);
                if (MapUtils.isEmpty(maps) || StringUtils.isBlank(maps.get(seckillRegistrationRecord.getUserPhoneNumber()).getId()) || StringUtils.isBlank(maps.get(seckillRegistrationRecord.getBuyerPhoneNumber()).getId())) {
                    throw new StoreSaasMarketingException("根据秒杀订单新建客户失败");
                }
                if (!maps.get(seckillRegistrationRecord.getBuyerPhoneNumber()).getId().equals(req.getCustomerId()) && shoppingPlatformEnum.equals(ShoppingPlatformEnum.WECHAT_APPLET)) {
                    log.warn("秒杀订单,购买人客户id不一致: {}，{}", maps.get(seckillRegistrationRecord.getBuyerPhoneNumber()), req.getCustomerId());
                }

                //记录秒杀订单
                this.addSeckillRegistrationRecord(seckillOrderCode, seckillRegistrationRecord, maps, shoppingPlatformEnum);

                //根据秒杀订单创建 待收单、交易单
                String tradeOrderId = this.addReceivingAndTradeOrderBySeckillActivity(seckillRegistrationRecord);
                if (StringUtils.isBlank(tradeOrderId)) {
                    throw new StoreSaasMarketingException("根据秒杀订单新建交易单失败");
                }
                //支付
                returnMap = payService.getPayAuthToken(seckillRegistrationRecord, tradeOrderId);
                returnMap.put("seckillRegistrationRecordId", seckillRegistrationRecord.getId());
                returnMap.put("tradeOrderId", tradeOrderId);
            } catch (Exception e) {
                log.error("Repeat customerActivityOrderAdd error key: {}", seckillOrderCode, e);
                throw new StoreSaasMarketingException(e.getMessage());
            } finally {
                //如果发生异常后 放上释放锁
                storeRedisUtils.releaseLock(seckillOrderCode, obj.toString());
            }
        } else {
            throw new StoreSaasMarketingException(BizErrorCodeEnum.TOO_MANY_REQUEST.getDesc());
        }
        return returnMap;
    }

    @Override
    public List<SeckillRegistrationRecord> customerBuyRecordList(SeckillActivityDetailReq req) {
        EntityWrapper<SeckillRegistrationRecord> search = new EntityWrapper<>();
        if (Objects.nonNull(req.getCustomerId())) {
            search.eq(SeckillRegistrationRecord.CUSTOMER_ID, req.getCustomerId());
        } else if (Objects.nonNull(req.getCustomerPhoneNumber())) {
            search.eq(SeckillRegistrationRecord.BUYER_PHONE_NUMBER, req.getCustomerPhoneNumber());
        }
        search.eq(SeckillRegistrationRecord.SECKILL_ACTIVITY_ID, req.getSeckillActivityId())
                .eq(SeckillRegistrationRecord.STORE_ID, req.getStoreId())
                .eq(SeckillRegistrationRecord.TENANT_ID, req.getTenantId())
                .eq(SeckillRegistrationRecord.PAY_STATUS, SeckillConstant.PAY_SUCCESS_STATUS)
                .eq(SeckillRegistrationRecord.IS_DELETE, 0)
                .orderBy(SeckillRegistrationRecord.PAYMENT_TIME, false);
        List<SeckillRegistrationRecord> seckillRegistrationRecords = this.selectList(search);
        return seckillRegistrationRecords;
    }


    @Override
    @Transactional
    public void callBack(PaymentResponse paymentResponse) {
        log.info("callback，paymentResponse={}", JSONObject.toJSONString(paymentResponse));
        String outBizNo = paymentResponse.getOutBizNo();
        if (StringUtils.isBlank(outBizNo)) {
            return;
        }
        TradeOrderDTO tradeOrderDTO = this.remoteGetTradeOrder(outBizNo.trim());
        if (Objects.isNull(tradeOrderDTO) || StringUtils.isBlank(tradeOrderDTO.getOrderId())) {
            log.warn("not found TradeOrderDTO  key: {}", outBizNo);
        }
        SeckillRegistrationRecord seckillRegistrationRecord = this.selectById(tradeOrderDTO.getOrderId());
        log.info("callback，paymentResponse,seckillRegistrationRecord:{}", JSONObject.toJSONString(seckillRegistrationRecord));
        if (Objects.isNull(seckillRegistrationRecord)) {
            log.warn("callBack,没有找到秒杀活动抢购单：" + outBizNo.trim());
        }
        if (seckillRegistrationRecord.getPayStatus().equals(SeckillRegistrationRecordPayStatusEnum.CG.getStatus())) {
            log.warn("秒杀活动抢购单已支付");
        } else {
            this.processUpdateSeckillRegistrationRecord(paymentResponse.getPaymentStatus(), seckillRegistrationRecord);
        }
    }


    @Override
    @Transactional
    public void update(SeckillRecordUpdateReq seckillRecordUpdateReq) {
        log.info("update，seckillRecordUpdateReq={}", JSONObject.toJSONString(seckillRecordUpdateReq));
        SeckillRegistrationRecord seckillRegistrationRecord = this.selectById(seckillRecordUpdateReq.getOrderId());
        log.info("update，seckillRecordUpdateReq,seckillRegistrationRecord:{}", JSONObject.toJSONString(seckillRegistrationRecord));
        if (Objects.isNull(seckillRegistrationRecord)) {
            log.warn("update,没有找到秒杀活动抢购单：" + seckillRecordUpdateReq.getOrderId());
            return;
        }
        if (seckillRegistrationRecord.getPayStatus().equals(SeckillRegistrationRecordPayStatusEnum.CG.getStatus())) {
            log.warn("秒杀活动抢购单已支付");
            return;
        }

        if (StringUtils.isNotBlank(seckillRecordUpdateReq.getStatus()) && seckillRecordUpdateReq.getStatus().equals(FinancePaymentStatusEnum.SUCCESS.getStatus())) {
            //支付成功
            this.updatePaySuccess(seckillRegistrationRecord);
        } else {
            //支付失败
            seckillRegistrationRecord.setPayStatus(SeckillRegistrationRecordPayStatusEnum.SB.getStatus());
            this.updateById(seckillRegistrationRecord);
        }
    }

    private void updatePaySuccess(SeckillRegistrationRecord seckillRegistrationRecord) {
        try {
            lock.lock();
            seckillRegistrationRecord.setPayStatus(SeckillRegistrationRecordPayStatusEnum.CG.getStatus());
            seckillRegistrationRecord.setUpdateTime(DateUtils.now());
            seckillRegistrationRecord.setPaymentTime(DateUtils.now());//支付时间
            this.updateById(seckillRegistrationRecord);
            //this.updateReceivingAndTradeOrder(seckillRegistrationRecord, SeckillConstant.PAY_SUCCESS_STATUS);
            //生成开卡单
            cardOrderService.addCardOrderBySeckillActivity(this.generateAddCardOrderReq(seckillRegistrationRecord));
            //通过秒杀活动成功后,车辆绑定或变更
            this.remoteAddVehicleBySeckillActivity(seckillRegistrationRecord);
            log.info("paySuccess  key={}, counter = {}", seckillRegistrationRecord.getSeckillActivityId(), seckillRegistrationRecord.getQuantity());
        } finally {
            lock.unlock();
        }
    }


    @Override
    public Map<String, Object> OrderPayDetail(SeckillActivityDetailReq seckillActivityDetailReq) {
        Map<String, Object> returnMap = com.google.common.collect.Maps.newHashMap();
        log.info("OrderPayDetail, request={}", JSONObject.toJSONString(seckillActivityDetailReq));
        if (Objects.isNull(seckillActivityDetailReq)) {
            return returnMap;
        }

        SeckillRegistrationRecord seckillRegistrationRecord = this.selectById(seckillActivityDetailReq.getSeckillRegistrationRecordId());
        if (Objects.isNull(seckillRegistrationRecord)) {
            log.warn("not found seckillActivityDetailId key: {}", seckillActivityDetailReq.getSeckillRegistrationRecordId());
            return returnMap;
        }
        returnMap.put("id", seckillRegistrationRecord.getId());
        if (StringUtils.isBlank(seckillActivityDetailReq.getTradeOrderId())) {
            returnMap.put("payStatus", seckillRegistrationRecord.getPayStatus());
            return returnMap;
        } else {
            //判断交易状态是否有变动，可能已经回调
            TradeOrderDTO tradeOrderDTO = this.remoteGetTradeOrder(seckillActivityDetailReq.getTradeOrderId());
            if (Objects.isNull(tradeOrderDTO)) {
                log.warn("not found TradeOrderDTO key: {}", seckillActivityDetailReq.getTradeOrderId());
                return returnMap;
            }

            List<Integer> integerList = Lists.newArrayList(TradeOrderStatusEnum.SUCCESS.getCode(), TradeOrderStatusEnum.FAIL.getCode());
            if (Objects.nonNull(tradeOrderDTO) && integerList.contains(tradeOrderDTO.getStatus()) && StringUtils.isNotBlank(tradeOrderDTO.getCallbackData())) {
                returnMap.put("payStatus", tradeOrderDTO.getStatus());
                return returnMap;
            }
            Object result = payService.queryPaymentResult(seckillActivityDetailReq.getTradeOrderId());
            JSONObject jsonObject = JSONObject.parseObject(JSONObject.toJSONString(result));
            JSONObject returnObject = jsonObject.getJSONObject("data");
            String paymentStatus = null;
            if (Objects.nonNull(returnObject)) {
                paymentStatus = (String) returnObject.get("paymentStatus");
            }
            if (StringUtils.isBlank(paymentStatus)) {
                returnMap.put("payStatus", TradeOrderStatusEnum.UNACTIVATE.getCode());
                return returnMap;
            }
            this.processUpdateSeckillRegistrationRecord(paymentStatus, seckillRegistrationRecord);
            if (TradeOrderStatusEnum.SUCCESS.name().equals(paymentStatus)) {
                returnMap.put("payStatus", TradeOrderStatusEnum.SUCCESS.getCode());
            } else if (TradeOrderStatusEnum.FAIL.name().equals(paymentStatus)) {
                returnMap.put("payStatus", TradeOrderStatusEnum.FAIL.getCode());
            } else {
                returnMap.put("payStatus", TradeOrderStatusEnum.UNACTIVATE.getCode());
            }
        }
        return returnMap;
    }

    private void processUpdateSeckillRegistrationRecord(String paymentStatus, SeckillRegistrationRecord seckillRegistrationRecord) {
        String key = seckillRegistrationRecord.getId() + seckillRegistrationRecord.getStoreId() + seckillRegistrationRecord.getTenantId();
        Object obj = storeRedisUtils.getAtomLock(key, 2);
        log.info("processUpdateSeckillRegistrationRecord tryLock key = {}", key);
        if (obj != null) {
            log.info("processUpdateSeckillRegistrationRecord tryLock success, key ={}", key);
            try {
                seckillRegistrationRecord.setUpdateTime(DateUtils.now());
                seckillRegistrationRecord.setPaymentTime(DateUtils.now());//支付时间
                if (StringUtils.isNotBlank(paymentStatus) && paymentStatus.equals(FinancePaymentStatusEnum.SUCCESS.getStatus())) {
                    //支付成功
                    this.paySuccess(seckillRegistrationRecord);
                } else {
                    //支付失败
                    seckillRegistrationRecord.setPayStatus(SeckillRegistrationRecordPayStatusEnum.SB.getStatus());
                    this.updateById(seckillRegistrationRecord);
                    this.updateReceivingAndTradeOrder(seckillRegistrationRecord, SeckillConstant.CANCEL_STATUS);
                }
            } catch (Exception e) {
                log.error("processUpdateSeckillRegistrationRecord error key: {}", key, e);
                seckillRegistrationRecord.setPayStatus(SeckillRegistrationRecordPayStatusEnum.SB.getStatus());
                this.updateById(seckillRegistrationRecord);
            } finally {
                storeRedisUtils.releaseLock(key, obj.toString());
            }
        }
    }


    private void paySuccess(SeckillRegistrationRecord seckillRegistrationRecord) {
        try {
            lock.lock();
            seckillRegistrationRecord.setPayStatus(SeckillRegistrationRecordPayStatusEnum.CG.getStatus());
            this.updateById(seckillRegistrationRecord);
            this.updateReceivingAndTradeOrder(seckillRegistrationRecord, SeckillConstant.PAY_SUCCESS_STATUS);
            //生成开卡单
            cardOrderService.addCardOrderBySeckillActivity(this.generateAddCardOrderReq(seckillRegistrationRecord));
            //通过秒杀活动成功后,车辆绑定或变更
            this.remoteAddVehicleBySeckillActivity(seckillRegistrationRecord);
            log.info("paySuccess  key={}, counter = {}", seckillRegistrationRecord.getSeckillActivityId(), seckillRegistrationRecord.getQuantity());
        } finally {
            lock.unlock();
        }
    }

    private AddCardOrderReq generateAddCardOrderReq(SeckillRegistrationRecord seckillRegistrationRecord) {
        AddCardOrderReq addCardOrderReq = new AddCardOrderReq();
        BeanUtils.copyProperties(seckillRegistrationRecord, addCardOrderReq);
        SeckillActivity seckillActivity = seckillActivityService.selectById(seckillRegistrationRecord.getSeckillActivityId());
        if (Objects.isNull(seckillActivity)) {
            log.error("not found SeckillActivity  key: {}", seckillRegistrationRecord.getSeckillActivityId());
            throw new StoreSaasMarketingException("没有找到秒杀活动！");
        }
        addCardOrderReq.setCardTemplateId(Long.parseLong(seckillActivity.getCadCardTemplateId()));
        //开卡秒杀单id
        addCardOrderReq.setSeckillRegisterRecodeId(seckillRegistrationRecord.getId());
        //开卡 填充使用人客户id
        addCardOrderReq.setCustomerId(seckillRegistrationRecord.getUserCustomerId());
        CustomerDTO customerDTO = null;
        try {
            BaseIdReqVO baseIdReqVO = new BaseIdReqVO();
            baseIdReqVO.setId(seckillRegistrationRecord.getUserCustomerId());
            customerDTO = customerClient.getCustomerById(baseIdReqVO).getData();
        } catch (Exception e) {
            log.error("customerClient.getCustomerById error,storeId=" + seckillRegistrationRecord.getStoreId(), e);
        }
        if (Objects.isNull(customerDTO)) {
            throw new StoreSaasMarketingException("客户不存在");
        }
        addCardOrderReq.setCustomerName(customerDTO.getName());
        addCardOrderReq.setCustomerPhoneNumber(customerDTO.getPhoneNumber());

        if (Objects.nonNull(seckillActivity) && seckillActivity.getCadCardExpiryDateType().equals(SeckillConstant.CARD_EXPIRY_DATE_TYPE_FOREVER)) {
            addCardOrderReq.setForever(Boolean.TRUE);
        } else if (Objects.nonNull(seckillActivity) && seckillActivity.getCadCardExpiryDateType().equals(SeckillConstant.CARD_EXPIRY_DATE_TYPE_DEADLINE)) {
            addCardOrderReq.setForever(Boolean.FALSE);
            addCardOrderReq.setExpiryDate(DateUtils.getDateEndTime2(seckillActivity.getCadCardExpiryDateTime()));
        } else if (Objects.nonNull(seckillActivity) && seckillActivity.getCadCardExpiryDateType().equals(SeckillConstant.CARD_EXPIRY_DATE_TYPE_EFFECTIVE)) {
            addCardOrderReq.setForever(Boolean.FALSE);
            addCardOrderReq.setExpiryDate(DateUtils.getDateEndTime2(DateUtils.addDate(DateUtils.now(), seckillActivity.getCadCardExpiryDateDay() - 1)));
        }
        StoreDTO storeInfoDTO = this.remoteGetStoreInfo(seckillRegistrationRecord.getStoreId());
        if (Objects.nonNull(storeInfoDTO)) {
            addCardOrderReq.setStoreNo(storeInfoDTO.getStoreNo());
        }
        return addCardOrderReq;
    }

    private void dataConversion(SeckillRegistrationRecord o, SeckillRegistrationRecordResp
            response, Map<String, Integer> customerIdNewMap) {
        //是否新用户
        if (null != customerIdNewMap.get(o.getCustomerId())) {
            response.setIsNewCustomer(SeckillConstant.TYPE_1);
        }
        //浏览时间
        response.setBrowseTime(o.getCreateTime());
    }

    /**
     * 根据手机号和活动id 获取是否新用户
     *
     * @param customerIds
     * @param seckillActivityId
     * @return
     */
    private Map<String, Integer> customerIdNewMap(List<String> customerIds, String seckillActivityId) {
        log.info("customerIdNewMap{}", JSON.toJSONString(customerIds));
        EntityWrapper<ClientEventRecordDAO> wrapper = new EntityWrapper<>();
        wrapper.eq(ClientEventRecordDAO.CONTENT_VALUE, seckillActivityId);
        wrapper.eq(ClientEventRecordDAO.EVENT_TYPE, SeckillConstant.REGISTERED);
        wrapper.in(SeckillRegistrationRecord.CUSTOMER_ID, customerIds);
        List<ClientEventRecordDAO> list = clientEventRecordMapper.selectList(wrapper);
        if (CollectionUtils.isNotEmpty(list)) {
            Map<String, Integer> customerIdNewMap = new HashMap<>();
            Map<String, List<ClientEventRecordDAO>> activityIdListMap = list.stream().collect(Collectors.groupingBy(ClientEventRecordDAO::getCustomerId));
            for (Map.Entry<String, List<ClientEventRecordDAO>> entry : activityIdListMap.entrySet()) {
                customerIdNewMap.put(entry.getKey(), SeckillConstant.TYPE_1);
            }
            return customerIdNewMap;
        }
        return Collections.EMPTY_MAP;
    }

    @Override
    public List<SeckillRegistrationRecordResp> participateDetail(String customersId, String seckillActivityId) {
        if (null == customersId) {
            throw new StoreSaasMarketingException("客户ID不能为空");
        }
        seckillActivityService.check(seckillActivityId, Boolean.TRUE);
        EntityWrapper<SeckillRegistrationRecord> wrapper = new EntityWrapper<>();
        wrapper.eq(SeckillRegistrationRecord.SECKILL_ACTIVITY_ID, seckillActivityId);
        wrapper.eq(SeckillRegistrationRecord.CUSTOMER_ID, customersId);
        wrapper.eq(SeckillRegistrationRecord.PAY_STATUS, SeckillConstant.PAY_SUCCESS_STATUS);
        wrapper.eq(SeckillRegistrationRecord.STORE_ID, UserContextHolder.getStoreId());
        wrapper.eq(SeckillRegistrationRecord.TENANT_ID, UserContextHolder.getTenantId());
        wrapper.eq(SeckillRegistrationRecord.IS_DELETE, SeckillConstant.TYPE);
        wrapper.orderBy(SeckillRegistrationRecord.PAYMENT_TIME, Boolean.FALSE);
        List<SeckillRegistrationRecord> list = this.selectList(wrapper);
        List<SeckillRegistrationRecordResp> recordResps = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(list)) {
            for (SeckillRegistrationRecord record : list) {
                SeckillRegistrationRecordResp response = new SeckillRegistrationRecordResp();
                BeanUtils.copyProperties(record, response);
                recordResps.add(response);
            }
        }
        return recordResps;
    }

    @Override
    public SeckillActivityStatisticsResp dataStatistics(String seckillActivityId) {
        SeckillActivity activity = seckillActivityService.check(seckillActivityId, Boolean.TRUE);
        SeckillActivityStatisticsResp resp = new SeckillActivityStatisticsResp();
        resp.setActivityTitle(activity.getActivityTitle());
        resp.setSeckillActivityId(seckillActivityId);
        Long storeId = UserContextHolder.getStoreId();
        Long tenantId = UserContextHolder.getTenantId();
        //今日成交，取当日成功支付购买的份数，注意不是支付笔数
        SeckillRegistrationRecord todayDealRecord = this.baseMapper.dataStatistics(tenantId, storeId, seckillActivityId, SeckillConstant.TYPE);
        if (null != todayDealRecord) {
            resp.setTodayDeal(todayDealRecord.getQuantity());
        }
        //总成交，取当前活动成功支付购买的份数，注意不是支付笔数
        SeckillRegistrationRecord totalDealRecord = this.baseMapper.dataStatistics(tenantId, storeId, seckillActivityId, null);
        if (null != totalDealRecord) {
            resp.setTotalDeal(totalDealRecord.getQuantity());
            resp.setTotalAmount(totalDealRecord.getExpectAmount());  //总收入（元）
        }
        //获取新客
        List<String> newUsers = this.baseMapper.getAllUserCountByTypeAndSeckillActivityId(seckillActivityId, SeckillConstant.TYPE);
        log.info("newUsers{}", newUsers);
        List<String> newBuys = null;
        if (CollectionUtils.isNotEmpty(newUsers)) {
            resp.setNewCustomers(newUsers.size());
            newBuys = this.baseMapper.getBuyCountByTypeAndSeckillActivityId(seckillActivityId, SeckillConstant.TYPE);
            //新客转化率
            int newBuyCount = CollectionUtils.isEmpty(newBuys) ? 0 : newBuys.size();
            BigDecimal rate = new BigDecimal(newBuyCount).divide(new BigDecimal(newUsers.size()), SeckillConstant.SCALE, BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal(100));
            resp.setNewCustomersConversionRate(rate.setScale(SeckillConstant.NEW_SCALE, BigDecimal.ROUND_HALF_UP).stripTrailingZeros().toPlainString());
        }
        log.info("newBuys{}", newBuys);
        //唤醒老客
        List<String> oldUsers = this.baseMapper.getAllUserCountByTypeAndSeckillActivityId(seckillActivityId, SeckillConstant.TYPE_1);
        log.info("oldUsers{}", oldUsers);
        if (CollectionUtils.isNotEmpty(oldUsers)) {
            Collection<String> oldCustomerSubtract = CollectionUtils.subtract(oldUsers, newUsers);//差集 = 老客户包含新用户 - 新用户
            log.info("oldCustomerSubtract{}", oldCustomerSubtract);
            if (CollectionUtils.isNotEmpty(oldCustomerSubtract)) {
                resp.setOldCustomer(oldCustomerSubtract.size());
                List<String> oldBuys = this.baseMapper.getBuyCountByTypeAndSeckillActivityId(seckillActivityId, SeckillConstant.TYPE_1);
                log.info("oldBuys{}", oldBuys);
                newBuys = CollectionUtils.isEmpty(newBuys) ? new ArrayList<>() : newBuys;
                Collection<String> oldBuySubtract = CollectionUtils.subtract(oldBuys, newBuys);//差集 = 老客户包含新用户 - 新用户
                log.info("oldBuySubtract{}", oldBuySubtract);
                //老客转化率
                int oldBuyCount = CollectionUtils.isEmpty(oldBuySubtract) ? 0 : oldBuySubtract.size();
                BigDecimal rate = new BigDecimal(oldBuyCount).divide(new BigDecimal(oldCustomerSubtract.size()), SeckillConstant.SCALE, BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal(100));
                resp.setOldCustomerConversionRate(rate.setScale(SeckillConstant.NEW_SCALE, BigDecimal.ROUND_HALF_UP).stripTrailingZeros().toPlainString());
            }
        }
        return resp;
    }

    @Override
    @Transactional
    public void seckillActivity24AutoCancel() {
        List<SeckillRegistrationRecord> list = this.baseMapper.seckillActivity24AutoCancel(NUM);
        if (CollectionUtils.isNotEmpty(list)) {
            List<String> orderNos = new ArrayList<>();
            Date now = new Date();
            for (SeckillRegistrationRecord record : list) {
                String orderNo = record.getOrderNo();
                orderNos.add(orderNo);
                record.setPayStatus(SeckillRegistrationRecordPayStatusEnum.ZF.getStatus());
                record.setUpdateTime(now);
                record.setUpdateUser("24AutoCancel");
            }
            //批量取消
            if (CollectionUtils.isNotEmpty(orderNos)) {
                boolean success = this.updateReceivingAndTradeOrderByOrderNos(orderNos, SeckillConstant.CANCEL_STATUS);
                if (success) {
                    this.insertOrUpdateBatch(list);
                }
            }
        }
    }

    /**
     * 更新 待收单、交易单的状态
     *
     * @param orderNos
     * @param state    0 表示：已取消、已作废；    1 表示 ：已结清、回调(成功)   2 表示 ：已取消、回调(失败)
     * @return
     */
    private boolean updateReceivingAndTradeOrderByOrderNos(List<String> orderNos, Integer state) {
        log.info("updateReceivingAndTradeOrderByOrderNos orderNos:{};state:{}", orderNos, state);
        boolean success = false;
        try {
            BizBaseResponse baseResponse = storeReceivingClient.updateReceivingAndTradeOrderByOrderNos(orderNos, state);
            log.info("updateReceivingAndTradeOrderByOrderNos Result{}", JSON.toJSONString(baseResponse));
            if (baseResponse.isSuccess()) {
                success = true;
            }
        } catch (Exception e) {
            log.error("updateReceivingAndTradeOrderByOrderNos Error", e);
            throw new StoreSaasMarketingException("更新 待收单、交易单的状态 失败");
        }
        return success;
    }


    /**
     * 将未收款的待收单+交易单作废
     *
     * @param seckillRegistrationRecord
     * @param state                     0 表示：已取消、已作废；    1 表示 ：已结清、回调(成功)   2 表示 ：已取消、回调(失败)
     */
    private void updateReceivingAndTradeOrder(SeckillRegistrationRecord seckillRegistrationRecord, Integer state) {
        //只有成功的时候才更新总库存、和已下单库存
        this.updatePreNum(seckillRegistrationRecord, state);

        List<SeckillRegistrationRecord> seckillRegistrationRecordList = this.selectList(this.buildSearchParams(seckillRegistrationRecord));
        if (CollectionUtils.isNotEmpty(seckillRegistrationRecordList)) {
            List<String> orderNos = seckillRegistrationRecordList.stream().map(p -> p.getOrderNo()).collect(Collectors.toList());
            if (CollectionUtils.isEmpty(orderNos)) {
                return;
            }
            this.updateReceivingAndTradeOrderByOrderNos(orderNos, state);
        }
        //临时作废秒杀抢购单表数据
        if (CollectionUtils.isNotEmpty(seckillRegistrationRecordList)) {
            for (SeckillRegistrationRecord seckillRegistrationRecordTemp : seckillRegistrationRecordList) {
                seckillRegistrationRecordTemp.setPayStatus(SeckillRegistrationRecordPayStatusEnum.LSZF.getStatus());
            }
            this.updateBatchById(seckillRegistrationRecordList);
        }
    }

    /**
     * 组装查询条件
     *
     * @param req
     * @return
     */
    private EntityWrapper<SeckillRegistrationRecord> buildSearchParams(SeckillRegistrationRecord req) {
        EntityWrapper<SeckillRegistrationRecord> search = new EntityWrapper<>();
        search.orderBy(SeckillRegistrationRecord.UPDATE_TIME, Boolean.FALSE);
        if (Objects.nonNull(req.getStoreId())) {
            search.eq(SeckillRegistrationRecord.STORE_ID, req.getStoreId());
        }
        if (Objects.nonNull(req.getTenantId())) {
            search.eq(SeckillRegistrationRecord.TENANT_ID, req.getTenantId());
        }
        if (Objects.nonNull(req.getPayStatus())) {
            search.eq(SeckillRegistrationRecord.PAY_STATUS, req.getPayStatus());
        }
        if (Objects.nonNull(req.getSeckillActivityId())) {
            search.eq(SeckillRegistrationRecord.SECKILL_ACTIVITY_ID, req.getSeckillActivityId());
        }
        if (Objects.nonNull(req.getCustomerId())) {
            search.eq(SeckillRegistrationRecord.CUSTOMER_ID, req.getCustomerId());
        }
        if (Objects.nonNull(req.getBuyerPhoneNumber())) {
            search.eq(SeckillRegistrationRecord.BUYER_PHONE_NUMBER, req.getBuyerPhoneNumber());
        }
        if (Objects.nonNull(req.getIsDelete())) {
            search.eq(SeckillRegistrationRecord.IS_DELETE, req.getIsDelete());
        }
        if (StringUtils.isNotBlank(req.getId())) {
            search.eq(SeckillRegistrationRecord.ID, req.getId());
        }
        return search;
    }


    /**
     * 根据秒杀订单创建 待收单  交易单
     *
     * @param seckillRegistrationRecord
     */
    private String addReceivingAndTradeOrderBySeckillActivity(SeckillRegistrationRecord seckillRegistrationRecord) {
        AddReceivingVO addReceivingVO = new AddReceivingVO();
        addReceivingVO.setOrderId(seckillRegistrationRecord.getId());
        addReceivingVO.setOrderNo(seckillRegistrationRecord.getOrderNo());
        addReceivingVO.setOrderDate(seckillRegistrationRecord.getCreateTime());
        addReceivingVO.setBusinessCategoryCode(BusinessCategoryEnum.SECKILL_ACTIVITY_ORDER.name());
        addReceivingVO.setBusinessCategoryName(BusinessCategoryEnum.SECKILL_ACTIVITY_ORDER.status);
        addReceivingVO.setPayerId(seckillRegistrationRecord.getCustomerId());
        addReceivingVO.setPayerName(seckillRegistrationRecord.getCustomerName());
        addReceivingVO.setPayerPhoneNumber(seckillRegistrationRecord.getBuyerPhoneNumber());
        addReceivingVO.setAmount(seckillRegistrationRecord.getExpectAmount().multiply(new BigDecimal(100)).longValue());
        addReceivingVO.setDiscountAmount(0L);
        addReceivingVO.setActualAmount(addReceivingVO.getAmount());
        addReceivingVO.setPayedAmount(0L);
        addReceivingVO.setStatus(PayStatusEnum.INIT.status);
        addReceivingVO.setPaymentStatus(PayStatusEnum.UNRECEIVABLE.status);
        addReceivingVO.setStoreId(seckillRegistrationRecord.getStoreId());
        addReceivingVO.setTenantId(seckillRegistrationRecord.getTenantId());
        addReceivingVO.setCreateTime(DateUtils.now());
        addReceivingVO.setPaySource(1);//1:客户车主端支付

        //this.remoteAddReceiving(addReceivingVO);
        //return this.remoteAddTradeOrder(addReceivingVO);
        //添加 代收单、交易单
        return this.addReceivingAndTradeOrder(addReceivingVO);
    }


    private void remoteAddReceiving(AddReceivingVO addReceivingVO) {
        try {
            //创建待收单
            log.info("storeReceivingClient.addReceiving request:{}", JSONObject.toJSONString(addReceivingVO));
            Boolean result = storeReceivingClient.addReceiving(addReceivingVO).getData();
            log.info("storeReceivingClient.addReceiving response:{}", JSONObject.toJSONString(result));
            if (Objects.isNull(result) || !result) {
                throw new StoreSaasMarketingException("创建待收记录失败");
            }
        } catch (BizException e) {
            log.error("storeReceivingClient.addReceiving error：调用参数{}，异常{}", JSONObject.toJSONString(addReceivingVO), e);
            throw new StoreSaasMarketingException("创建待收记录异常");
        }
    }


    private String addReceivingAndTradeOrder(AddReceivingVO addReceivingVO) {
        String result;
        try {
            //创建交易单和待收单
            log.info("tradeOrderClient.addReceivingAndTradeOrder request:{}", JSONObject.toJSONString(addReceivingVO));
            result = tradeOrderClient.addReceivingAndTradeOrder(addReceivingVO).getData();
            log.info("tradeOrderClient.addReceivingAndTradeOrder response:{}", result);
            if (StringUtils.isBlank(result)) {
                throw new StoreSaasMarketingException("根据秒杀活动创建交易单失败");
            }
        } catch (BizException e) {
            log.error("tradeOrderClient.addTradeOrderBySeckillActivity error：调用参数{}，异常{}", JSONObject.toJSONString(addReceivingVO), e);
            throw new StoreSaasMarketingException("根据秒杀活动创建交易单异常");
        }
        return result;
    }


    private String remoteAddTradeOrder(AddReceivingVO addReceivingVO) {
        String result;
        try {
            //创建交易单
            log.info("tradeOrderClient.addTradeOrderBySeckillActivity request:{}", JSONObject.toJSONString(addReceivingVO));
            result = tradeOrderClient.addTradeOrderBySeckillActivity(addReceivingVO).getData();
            log.info("tradeOrderClient.addTradeOrderBySeckillActivity response:{}", result);
            if (StringUtils.isBlank(result)) {
                throw new StoreSaasMarketingException("根据秒杀活动创建交易单失败");
            }
        } catch (BizException e) {
            log.error("tradeOrderClient.addTradeOrderBySeckillActivity error：调用参数{}，异常{}", JSONObject.toJSONString(addReceivingVO), e);
            throw new StoreSaasMarketingException("根据秒杀活动创建交易单异常");
        }
        return result;
    }


    private TradeOrderDTO remoteGetTradeOrder(String tradeOrderId) {
        TradeOrderDTO tradeOrderDTO;
        try {
            //创建交易单
            log.info("tradeOrderClient.getTradeOrderById request:{}", JSONObject.toJSONString(tradeOrderId));
            BizBaseResponse<TradeOrderDTO> result = tradeOrderClient.getTradeOrderById(tradeOrderId);
            log.info("tradeOrderClient.getTradeOrderById response:{}", JSONObject.toJSONString(result));
            if (Objects.nonNull(result) && result.isSuccess() && Objects.nonNull(result.getData())) {
                tradeOrderDTO = result.getData();
            } else {
                log.error("tradeOrderClient.getTradeOrderById error：调用参数{}", JSONObject.toJSONString(tradeOrderId));
                throw new StoreSaasMarketingException("获取交易单失败");
            }
        } catch (BizException e) {
            log.error("tradeOrderClient.getTradeOrderById error：调用参数{}，异常{}", JSONObject.toJSONString(tradeOrderId), e);
            throw new StoreSaasMarketingException("获取交易单失败异常");
        }
        return tradeOrderDTO;
    }


    private StoreDTO remoteGetStoreInfo(Long storeId) {
        StoreDTO storeDTO = null;
        try {
            StoreInfoVO storeInfoVO = new StoreInfoVO();
            storeInfoVO.setStoreId(storeId);
            log.info("storeInfoClient.getStoreInfo request:{}", JSONObject.toJSONString(storeInfoVO));
            BizBaseResponse<StoreDTO> result = storeInfoClient.getStoreInfo(storeInfoVO);
            log.info("storeInfoClient.getStoreInfo response:{}", JSONObject.toJSONString(result));
            if (Objects.nonNull(result) && result.isSuccess() && Objects.nonNull(result.getData())) {
                storeDTO = result.getData();
            } else {
                log.error("storeInfoClient.getStoreInfo error：调用参数{}", JSONObject.toJSONString(storeId));
            }
        } catch (Exception e) {
            log.error("storeInfoClient.getStoreInfo error：调用参数{}，异常{}", JSONObject.toJSONString(storeId), e);
            throw new StoreSaasMarketingException("获取门店信息失败");
        }
        return storeDTO;
    }


    private void checkSeckillRecordAddReqParam(SeckillRecordAddReq req, ShoppingPlatformEnum shoppingPlatformEnum) {
        if (Objects.isNull(req.getQuantity()) || req.getQuantity() < 1) {
            throw new StoreSaasMarketingException("抢购数量不能小于1！");
        }
        if (StringUtils.isBlank(req.getSeckillActivityId())) {
            throw new StoreSaasMarketingException("秒杀活动ID不能为空");
        }
        SeckillActivity seckillActivity = null;
        if (shoppingPlatformEnum == ShoppingPlatformEnum.WECHAT_APPLET) {
            //判断活动是否开启或者已结束
            seckillActivity = seckillActivityService.check(req.getSeckillActivityId(), Boolean.FALSE);
        } else {
            seckillActivity = seckillActivityService.selectById(req.getSeckillActivityId());
            if (Objects.isNull(seckillActivity)) {
                throw new StoreSaasMarketingException("秒杀活动不存在");
            }
        }

        if (seckillActivity.getStatus().equals(SeckillActivityStatusEnum.WSJ.getStatus())) {
            throw new StoreSaasMarketingException(seckillActivity.getActivityTitle() + SeckillActivityStatusEnum.WSJ.getStatusName());
        } else if (seckillActivity.getStatus().equals(SeckillActivityStatusEnum.XJ.getStatus())) {
            throw new StoreSaasMarketingException(seckillActivity.getActivityTitle() + SeckillActivityStatusEnum.XJ.getStatusName());
        }
        if (Objects.nonNull(seckillActivity.getStartTime())) {
            if (com.tuhu.springcloud.common.util.DateUtils.compareTime(seckillActivity.getStartTime(), DateUtils.now()) > 0) {
                throw new StoreSaasMarketingException(seckillActivity.getActivityTitle() + "秒杀活动未开始");
            }
        }
        if (Objects.nonNull(seckillActivity.getStartTime())) {
            if (com.tuhu.springcloud.common.util.DateUtils.compareTime(DateUtils.now(), seckillActivity.getEndTime()) > 0) {
                throw new StoreSaasMarketingException(seckillActivity.getActivityTitle() + "秒杀活动已过期");
            }
        }

        if (seckillActivity.getSellNumberType().equals(SeckillActivitySellTypeEnum.XZSL.getCode())) {
            if (req.getQuantity() > seckillActivity.getSellNumber()) {
                throw new StoreSaasMarketingException(seckillActivity.getActivityTitle() + ",销售数量不足或已抢购完");
            }
            //校验预占库存
            this.checkHasStock(req, seckillActivity);
        }
        if (seckillActivity.getSoloSellNumberType().equals(SeckillActivitySellTypeEnum.XZSL.getCode())) {
            //获取客户已购数量
            Long customerHasBuyNum = this.baseMapper.getCustomerBuyNumber(seckillActivity.getId(), req.getCustomerId());
            customerHasBuyNum = Objects.isNull(customerHasBuyNum) ? 0L : customerHasBuyNum;
            if (customerHasBuyNum + req.getQuantity() > seckillActivity.getSoloSellNumber()) {
                throw new StoreSaasMarketingException(seckillActivity.getActivityTitle() + ",单人销售数量不能大于" + seckillActivity.getSoloSellNumber());
            }
        }
    }


    /**
     * 校验预占库存
     *
     * @param seckillRecordAddReq
     * @param seckillActivity
     */
    private void checkHasStock(SeckillRecordAddReq seckillRecordAddReq, SeckillActivity seckillActivity) {
        String zku = RedisUtils.initInstance().getRedisPrefix() + SeckillConstant.SECKILL_ACTIVITY_ZKC + seckillRecordAddReq.getSeckillActivityId();
        String yxdku = RedisUtils.initInstance().getRedisPrefix() + SeckillConstant.SECKILL_ACTIVITY_YXDKC + seckillRecordAddReq.getSeckillActivityId();

        if (!stringRedisTemplate.hasKey(zku)) {
            stringRedisTemplate.boundValueOps(zku).increment(seckillActivity.getSellNumber());//秒杀活动总库存
        }
        if (!stringRedisTemplate.hasKey(yxdku)) {
            //查询支付成的单据
            SeckillRegistrationRecord seckillRegistrationRecord = new SeckillRegistrationRecord();
            BeanUtils.copyProperties(seckillRecordAddReq, seckillRegistrationRecord);
            seckillRegistrationRecord.setPayStatus(SeckillRegistrationRecordPayStatusEnum.CG.getStatus());
            List<SeckillRegistrationRecord> seckillRegistrationRecordList = this.selectList(this.buildSearchParams(seckillRegistrationRecord));
            Long hasBuyNum = 0l;
            if (CollectionUtils.isNotEmpty(seckillRegistrationRecordList)) {
                hasBuyNum = seckillRegistrationRecordList.stream().collect(Collectors.summingLong(SeckillRegistrationRecord::getQuantity));
            }
            stringRedisTemplate.boundValueOps(yxdku).increment(hasBuyNum.intValue());//秒杀活动真实库存
            stringRedisTemplate.boundValueOps(zku).increment((Integer.parseInt(stringRedisTemplate.opsForValue().get(yxdku)) * (-1)));//秒杀活动总库存-已下单库存=剩余总库存
        }

        long syzkc = Long.parseLong(stringRedisTemplate.opsForValue().get(zku));
        int yxdkuInt = Integer.parseInt(stringRedisTemplate.opsForValue().get(yxdku));

        if (seckillRecordAddReq.getQuantity() > syzkc) {
            throw new StoreSaasMarketingException(seckillActivity.getActivityTitle() + ",销售数量不足或已抢购完");
        }
        if (yxdkuInt + seckillRecordAddReq.getQuantity() > syzkc) {
            throw new StoreSaasMarketingException(seckillActivity.getActivityTitle() + ",销售数量不足或已抢购完");
        }
        SeckillActivityBuy seckillActivityBuy = new SeckillActivityBuy();
        seckillActivityBuy.setActivityId(seckillRecordAddReq.getSeckillActivityId());
        seckillActivityBuy.setCustomerId(seckillRecordAddReq.getCustomerId());
        seckillActivityBuy.setPhone(seckillRecordAddReq.getBuyerPhoneNumber());
        seckillActivityBuy.setNum(seckillRecordAddReq.getQuantity().intValue());
        seckillActivityBuy.setTotalNum(seckillActivity.getSellNumber());
        seckillActivityBuy.setBuyNum(Integer.parseInt(stringRedisTemplate.opsForValue().get(yxdku)));
        seckillActivityBuy.setSaleNum(seckillActivityBuy.getTotalNum() - seckillActivityBuy.getBuyNum());
        Integer preNum = seckillActivityService.getPreNum(seckillActivityBuy);
        if (preNum + yxdkuInt > syzkc) {
            throw new StoreSaasMarketingException(seckillActivity.getActivityTitle() + ",销售数量不足或已抢购完");
        }
    }

    /**
     * 更新预占缓存
     *
     * @param seckillRegistrationRecord
     */
    private void updatePreNum(SeckillRegistrationRecord seckillRegistrationRecord, Integer state) {
        String zku = RedisUtils.initInstance().getRedisPrefix() + SeckillConstant.SECKILL_ACTIVITY_ZKC + seckillRegistrationRecord.getSeckillActivityId();
        String yxdku = RedisUtils.initInstance().getRedisPrefix() + SeckillConstant.SECKILL_ACTIVITY_YXDKC + seckillRegistrationRecord.getSeckillActivityId();

        String activityId = RedisUtils.initInstance().getRedisPrefix() + SeckillConstant.SECKILL_ACTIVITY + seckillRegistrationRecord.getSeckillActivityId();
        if (SeckillConstant.PAY_SUCCESS_STATUS.equals(state)) {
            List<String> delKeys = new ArrayList<>();
            Map<Object, Object> entriesMap = stringRedisTemplate.opsForHash().entries(activityId);
            for (Map.Entry<Object, Object> entry : entriesMap.entrySet()) {
                String key = entry.getKey().toString();
                String value = entry.getValue().toString();
                SeckillActivityBuy buy = JSON.parseObject(value, SeckillActivityBuy.class);
                if (buy.getPhone().equals(seckillRegistrationRecord.getBuyerPhoneNumber()) && buy.getActivityId().equals(seckillRegistrationRecord.getSeckillActivityId())) {
                    delKeys.add(key);
                }
            }
            //异步删除
            if (CollectionUtils.isNotEmpty(delKeys)) {
                new Thread(() -> stringRedisTemplate.opsForHash().delete(activityId, delKeys.toArray())).start();
            }
            stringRedisTemplate.boundValueOps(yxdku).increment(seckillRegistrationRecord.getQuantity());//秒杀活动已下单库存
            stringRedisTemplate.boundValueOps(zku).increment(seckillRegistrationRecord.getQuantity() * (-1));//秒杀活动总库存-已下单库存=剩余总库存
        }
    }


    /**
     * 创建秒杀订单
     *
     * @param seckillActivityCode
     * @param seckillRegistrationRecord
     * @param maps
     * @param shoppingPlatformEnum
     * @return
     */
    private void addSeckillRegistrationRecord(String seckillActivityCode, SeckillRegistrationRecord seckillRegistrationRecord, Map<String, CustomerReq> maps, ShoppingPlatformEnum shoppingPlatformEnum) {
        seckillRegistrationRecord.setId(idKeyGen.generateId(seckillRegistrationRecord.getTenantId()));
        seckillRegistrationRecord.setOrderNo(seckillActivityCode);
        seckillRegistrationRecord.setCustomerId(maps.get(seckillRegistrationRecord.getBuyerPhoneNumber()).getId());
        seckillRegistrationRecord.setCustomerName(maps.get(seckillRegistrationRecord.getBuyerPhoneNumber()).getName());
        seckillRegistrationRecord.setUserCustomerId(maps.get(seckillRegistrationRecord.getUserPhoneNumber()).getId());
        seckillRegistrationRecord.setUserCustomerName(maps.get(seckillRegistrationRecord.getUserPhoneNumber()).getName());
        seckillRegistrationRecord.setRegisterTime(DateUtils.now());
        seckillRegistrationRecord.setCreateTime(DateUtils.now());
        seckillRegistrationRecord.setPaymentModeCode(PaymentModeConverEnum.WX_BARCODE.getModel());
        seckillRegistrationRecord.setPlatForm(shoppingPlatformEnum.getCode());

        SeckillActivity seckillActivity = seckillActivityService.selectById(seckillRegistrationRecord.getSeckillActivityId());
        if (StringUtils.isBlank(seckillRegistrationRecord.getSeckillActivityName())) {
            seckillRegistrationRecord.setSeckillActivityName(seckillActivity.getActivityTitle());
        }
        if (Objects.nonNull(seckillActivity) && seckillActivity.getCadCardExpiryDateType().equals(SeckillConstant.CARD_EXPIRY_DATE_TYPE_DEADLINE)) {
            seckillRegistrationRecord.setEffectiveTime(DateUtils.getDateEndTime2(seckillActivity.getCadCardExpiryDateTime()));
        } else if (Objects.nonNull(seckillActivity) && seckillActivity.getCadCardExpiryDateType().equals(SeckillConstant.CARD_EXPIRY_DATE_TYPE_EFFECTIVE)) {
            seckillRegistrationRecord.setEffectiveTime(DateUtils.getDateEndTime2(DateUtils.addDate(DateUtils.now(), seckillActivity.getCadCardExpiryDateDay() - 1)));
        }
        boolean flag = this.insert(seckillRegistrationRecord);
        if (!flag) {
            throw new StoreSaasMarketingException("创建秒杀订单失败");
        }
    }


    /**
     * 根据秒杀订单创建 客户
     *
     * @param seckillRegistrationRecord
     */
    private Map<String, CustomerReq> addCustomerForOrder(SeckillRegistrationRecord seckillRegistrationRecord) {
        Map<String, CustomerReq> maps = Maps.newHashMap();
        if (seckillRegistrationRecord.getBuyerPhoneNumber().equals(seckillRegistrationRecord.getUserPhoneNumber())) {
            String customerName = StringUtils.isBlank(seckillRegistrationRecord.getCustomerName()) ? SeckillConstant.DEFAULT_CUSTOMER_NAME : seckillRegistrationRecord.getCustomerName();
            CustomerReq buyerCustomerReq = this.remoteAddCustomer(seckillRegistrationRecord.getBuyerPhoneNumber(), customerName, seckillRegistrationRecord.getStoreId(), seckillRegistrationRecord.getTenantId());
            maps.put(seckillRegistrationRecord.getBuyerPhoneNumber(), buyerCustomerReq);
        } else {
            String customerName = StringUtils.isBlank(seckillRegistrationRecord.getCustomerName()) ? SeckillConstant.DEFAULT_CUSTOMER_NAME : seckillRegistrationRecord.getCustomerName();
            CustomerReq buyerCustomerReq = this.remoteAddCustomer(seckillRegistrationRecord.getBuyerPhoneNumber(), customerName, seckillRegistrationRecord.getStoreId(), seckillRegistrationRecord.getTenantId());
            maps.put(seckillRegistrationRecord.getBuyerPhoneNumber(), buyerCustomerReq);
            CustomerReq userCustomerReq = this.remoteAddCustomer(seckillRegistrationRecord.getUserPhoneNumber(), SeckillConstant.DEFAULT_CUSTOMER_NAME, seckillRegistrationRecord.getStoreId(), seckillRegistrationRecord.getTenantId());
            maps.put(seckillRegistrationRecord.getUserPhoneNumber(), userCustomerReq);
        }
        return maps;
    }


    private CustomerReq remoteAddCustomer(String phoneNumber, String name, Long storeId, Long tenantId) {
        //添加客户
        CustomerReq customerReq = new CustomerReq();
        customerReq.setPhoneNumber(phoneNumber);
        customerReq.setName(name);
        customerReq.setCustomerType("person");
        customerReq.setGender("3");
        customerReq.setCustomerSource(CustomerSourceEnumVo.ZRJD.getCode());
        AddVehicleReq addVehicleReq = new AddVehicleReq();
        addVehicleReq.setStoreId(storeId);
        addVehicleReq.setTenantId(tenantId);
        addVehicleReq.setCustomerReq(customerReq);
        log.info("customerClient.addCustomerForOrder request:{}", JSONObject.toJSONString(addVehicleReq));
        BizBaseResponse<AddVehicleVO> resultObject = customerClient.addCustomerForOrder(addVehicleReq);
        log.info("customerClient.addCustomerForOrder response:{}", JSONObject.toJSONString(resultObject));
        if (Objects.nonNull(resultObject) && Objects.nonNull(resultObject.getData())) {
            AddVehicleVO addVehicleVO = resultObject.getData();
            customerReq.setId(addVehicleVO.getCustomerReq().getId());
            customerReq.setName(addVehicleVO.getCustomerReq().getName());
        }
        return customerReq;
    }


    private void remoteAddVehicleBySeckillActivity(SeckillRegistrationRecord seckillRegistrationRecord) {
        try {
            AddVehicleVO addVehicleVO = new AddVehicleVO();
            addVehicleVO.setStoreId(seckillRegistrationRecord.getStoreId());
            addVehicleVO.setTenantId(seckillRegistrationRecord.getTenantId());

            CustomerReqVO customerReqVO = new CustomerReqVO();
            customerReqVO.setPhoneNumber(seckillRegistrationRecord.getUserPhoneNumber());
            customerReqVO.setId(seckillRegistrationRecord.getUserCustomerId());
            addVehicleVO.setCustomerReq(customerReqVO);

            VehicleReqVO vehicleReqVO = new VehicleReqVO();
            vehicleReqVO.setLicensePlateFlag(seckillRegistrationRecord.getVehicleNumber().substring(0, 1));
            vehicleReqVO.setLicensePlateNumber(seckillRegistrationRecord.getVehicleNumber().substring(1, seckillRegistrationRecord.getVehicleNumber().length()));
            addVehicleVO.setVehicleReq(vehicleReqVO);
            log.info("customerClient.addVehicleBySeckillActivity request:{}", JSONObject.toJSONString(addVehicleVO));
            BizBaseResponse resultObject = customerClient.addVehicleBySeckillActivity(addVehicleVO);
            log.info("customerClient.addVehicleBySeckillActivity response:{}", JSONObject.toJSONString(resultObject));
        } catch (BizException e) {
            log.error("customerClient.addVehicleBySeckillActivity error：调用参数{}，异常{}", JSONObject.toJSONString(seckillRegistrationRecord), e);
            throw new StoreSaasMarketingException("车主绑定、变更异常");
        }

    }

}
