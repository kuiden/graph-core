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
import com.tuhu.springcloud.common.annotation.DistributedLock;
import com.tuhu.springcloud.common.bean.BeanUtil;
import com.tuhu.store.saas.crm.dto.CustomerDTO;
import com.tuhu.store.saas.crm.vo.AddVehicleVO;
import com.tuhu.store.saas.crm.vo.BaseIdReqVO;
import com.tuhu.store.saas.crm.vo.CustomerSourceEnumVo;
import com.tuhu.store.saas.marketing.constant.SeckillConstant;
import com.tuhu.store.saas.marketing.context.UserContextHolder;
import com.tuhu.store.saas.marketing.dataobject.ClientEventRecordDAO;
import com.tuhu.store.saas.marketing.dataobject.SeckillActivity;
import com.tuhu.store.saas.marketing.dataobject.SeckillRegistrationRecord;
import com.tuhu.store.saas.marketing.enums.SeckillActivitySellTypeEnum;
import com.tuhu.store.saas.marketing.enums.SeckillActivityStatusEnum;
import com.tuhu.store.saas.marketing.enums.SeckillRegistrationRecordPayStatusEnum;
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
import com.tuhu.store.saas.marketing.request.seckill.SeckillActivityReq;
import com.tuhu.store.saas.marketing.request.seckill.SeckillRecordAddReq;
import com.tuhu.store.saas.marketing.response.seckill.SeckillActivityStatisticsResp;
import com.tuhu.store.saas.marketing.response.seckill.SeckillRegistrationRecordResp;
import com.tuhu.store.saas.marketing.service.ICardOrderService;
import com.tuhu.store.saas.marketing.service.seckill.SeckillActivityService;
import com.tuhu.store.saas.marketing.service.seckill.SeckillRegistrationRecordService;
import com.tuhu.store.saas.marketing.util.CodeFactory;
import com.tuhu.store.saas.marketing.util.IdKeyGen;
import com.tuhu.store.saas.marketing.util.StoreRedisUtils;
import com.tuhu.store.saas.order.enums.BusinessCategoryEnum;
import com.tuhu.store.saas.order.enums.FinancePaymentStatusEnum;
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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
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
            responseList = list.stream().map(o -> {
                SeckillRegistrationRecordResp response = new SeckillRegistrationRecordResp();
                BeanUtils.copyProperties(o, response);
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
            List<String> phones = new ArrayList<>();
            for (SeckillRegistrationRecord record : list) {
                phones.add(record.getBuyerPhoneNumber());
            }
            Map<String, Integer> phoneNewMap = this.phoneNewMap(phones, req.getSeckillActivityId());
            responseList = list.stream().map(o -> {
                SeckillRegistrationRecordResp response = new SeckillRegistrationRecordResp();
                BeanUtils.copyProperties(o, response);
                dataConversion(o, response, phoneNewMap);
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
    @DistributedLock(key = "#req.seckillActivityId + #req.buyerPhoneNumber")
    public void customerActivityOrderAdd(SeckillRecordAddReq req) {
        log.info("customerActivityOrderAdd：{}", JSON.toJSONString(req));
        //判断活动是否开启或者已结束
        SeckillActivity seckillActivity = seckillActivityService.check(req.getSeckillActivityId(), Boolean.FALSE);
        if (seckillActivity.getStatus().equals(SeckillActivityStatusEnum.WSJ.getStatus())) {
            throw new StoreSaasMarketingException(seckillActivity.getActivityTitle() + SeckillActivityStatusEnum.WSJ.getStatusName());
        } else if (seckillActivity.getStatus().equals(SeckillActivityStatusEnum.XJ.getStatus())) {
            throw new StoreSaasMarketingException(seckillActivity.getActivityTitle() + SeckillActivityStatusEnum.XJ.getStatusName());
        }
        if (seckillActivity.getSellNumberType().equals(SeckillActivitySellTypeEnum.XZSL.getCode())) {
            if (req.getQuantity() > seckillActivity.getSellNumber()) {
                throw new StoreSaasMarketingException(seckillActivity.getActivityTitle() + ",销售数量不足！");
            }
        }
        if (seckillActivity.getSoloSellNumberType().equals(SeckillActivitySellTypeEnum.XZSL.getCode())) {
            if (req.getQuantity() > seckillActivity.getSoloSellNumber()) {
                throw new StoreSaasMarketingException(seckillActivity.getActivityTitle() + ",单人销售数量不能大于" + seckillActivity.getSoloSellNumber());
            }
        }

        Long storeId = req.getStoreId();
        //生成秒杀活动编码
        String codeNumber = codeFactory.getCodeNumber(CodeFactory.SECKILL_ACTIVITY_PREFIX_CODE, storeId);
        String seckillActivityCode = codeFactory.generateSeckillActivityCode(storeId, codeNumber);
        Object obj = storeRedisUtils.getAtomLock(seckillActivityCode, 2);
        if (Objects.nonNull(obj)) {
            try {
                //写入抢购报名（订单）表数据
                SeckillRegistrationRecord seckillRegistrationRecord = new SeckillRegistrationRecord();
                BeanUtils.copyProperties(req, seckillRegistrationRecord);
                seckillRegistrationRecord.setId(idKeyGen.generateId(req.getTenantId()));
                seckillRegistrationRecord.setOrderNo(seckillActivityCode);
                //将未收款的待收单+交易单作废
                this.updateReceivingAndTradeOrder(seckillRegistrationRecord, SeckillConstant.CANCEL_STATUS);
                //根据秒杀订单新建客户
                Map<String, String> maps = this.addCustomerForOrder(seckillRegistrationRecord);
                if (MapUtils.isEmpty(maps) && StringUtils.isBlank(maps.get(seckillRegistrationRecord.getUserPhoneNumber()))) {
                    throw new StoreSaasMarketingException("根据秒杀订单新建客户失败");
                }
                seckillRegistrationRecord.setUserCustomerId(maps.get(seckillRegistrationRecord.getUserPhoneNumber()));
                this.insert(seckillRegistrationRecord);
                //根据秒杀订单创建 待收单、交易单
                this.addReceivingAndTradeOrderBySeckillActivity(seckillRegistrationRecord);
            } catch (Exception e) {
                //如果发生异常后 放上释放锁
                storeRedisUtils.releaseLock(seckillActivityCode, obj.toString());
                log.error("Repeat customerActivityOrderAdd error key: {}", seckillActivityCode, e);
                throw new StoreSaasMarketingException(e.getMessage());
            } finally {
                //如果发生异常后 放上释放锁
                storeRedisUtils.releaseLock(seckillActivityCode, obj.toString());
            }
        } else {
            throw new StoreSaasMarketingException(BizErrorCodeEnum.TOO_MANY_REQUEST.getDesc());
        }
    }

    public static void main(String[] args) {
        Date st = new Date();
        List<String> a = new ArrayList<>();
        for(int i=0 ;i<2000;i++){
            a.add("test123293344545344343"+i);
        }
        List<String> b = new ArrayList<>();
        for(int i=500 ;i<10000;i++){
            b.add("test123293344545344343"+i);
        }
        Collection<String> oldBuySubtract = CollectionUtils.subtract(b, a);//差集 = 老客户包含新用户 - 新用户
        System.out.println(new Date().getTime() - st.getTime());
        System.out.println(oldBuySubtract.size());
    }

    @Override
    @Transactional
    public void callBack(PaymentResponse paymentResponse) {
        log.info("callback，paymentResponse={}", JSONObject.toJSONString(paymentResponse));
        String outBizNo = paymentResponse.getOutBizNo();
        if (StringUtils.isBlank(outBizNo)) {
            return;
        }
        SeckillRegistrationRecord seckillRegistrationRecord = this.selectById(outBizNo.trim());
        log.info("callback，paymentResponse,seckillRegistrationRecord:{}", JSONObject.toJSONString(seckillRegistrationRecord));
        if (Objects.isNull(seckillRegistrationRecord)) {
            log.warn("callBack,没有找到秒杀活动抢购单：" + outBizNo.trim());
        }
        if (seckillRegistrationRecord.getPayStatus().equals(SeckillRegistrationRecordPayStatusEnum.CG.getStatus())) {
            storeRedisUtils.redisDelete(seckillRegistrationRecord.getId());
            log.warn("秒杀活动抢购单已支付");
        } else {
            this.processUpdateSeckillRegistrationRecord(paymentResponse, seckillRegistrationRecord);
        }
    }

    private void processUpdateSeckillRegistrationRecord(PaymentResponse paymentResponse, SeckillRegistrationRecord seckillRegistrationRecord) {
        String key = seckillRegistrationRecord.getId() + seckillRegistrationRecord.getStoreId() + seckillRegistrationRecord.getTenantId();
        Object obj = storeRedisUtils.getAtomLock(key, 3);
        log.info("processUpdateSeckillRegistrationRecord tryLock key = {}", key);
        if (obj != null) {
            log.info("processUpdateSeckillRegistrationRecord tryLock success, key ={}", key);
            try {
                seckillRegistrationRecord.setUpdateTime(new Date());
                if (StringUtils.isNotBlank(paymentResponse.getPaymentStatus()) && paymentResponse.getPaymentStatus().equals(FinancePaymentStatusEnum.SUCCESS.getStatus())) {
                    //支付成功
                    seckillRegistrationRecord.setPayStatus(SeckillRegistrationRecordPayStatusEnum.CG.getStatus());
                    this.updateById(seckillRegistrationRecord);
                    this.updateReceivingAndTradeOrder(seckillRegistrationRecord, SeckillConstant.PAY_SUCCESS_STATUS);
                    //生成开卡单
                    cardOrderService.addCardOrder(this.generateAddCardOrderReq(seckillRegistrationRecord));
                } else {
                    //支付失败
                    seckillRegistrationRecord.setPayStatus(SeckillRegistrationRecordPayStatusEnum.SB.getStatus());
                    this.updateById(seckillRegistrationRecord);
                    this.updateReceivingAndTradeOrder(seckillRegistrationRecord, SeckillConstant.PAY_FAIL_STATUS);
                }
            } catch (Exception e) {
                log.error("processUpdateSeckillRegistrationRecord error key: {}", key, e);
                seckillRegistrationRecord.setPayStatus(SeckillRegistrationRecordPayStatusEnum.SB.getStatus());
                this.updateById(seckillRegistrationRecord);
                //如果发生异常后 放上释放锁
                storeRedisUtils.releaseLock(key, obj.toString());
                throw e;
            }
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
        //开卡 填充使用人客户id
        addCardOrderReq.setCustomerId(seckillRegistrationRecord.getUserCustomerId());
        CustomerDTO customerDTO = null;
        try {
            BaseIdReqVO baseIdReqVO = new BaseIdReqVO();
            baseIdReqVO.setId(seckillRegistrationRecord.getUserCustomerId());
            customerDTO = customerClient.getCustomerById(baseIdReqVO).getData();
        } catch (Exception e) {
            log.error("查询门店信息RPC接口异常,storeId=" + seckillRegistrationRecord.getStoreId(), e);
        }
        if (Objects.isNull(customerDTO)) {
            throw new StoreSaasMarketingException("客户不存在");
        }
        addCardOrderReq.setCustomerName(customerDTO.getName());
        addCardOrderReq.setCustomerPhoneNumber(customerDTO.getPhoneNumber());

        if (Objects.nonNull(seckillActivity) && seckillActivity.getCadCardExpiryDateType().equals(SeckillConstant.CARD_EXPIRY_DATE_TYPE_1)) {
            addCardOrderReq.setForever(Boolean.TRUE);
        } else {
            addCardOrderReq.setForever(Boolean.FALSE);
        }

        try {
            StoreInfoVO storeInfoVO = new StoreInfoVO();
            storeInfoVO.setStoreId(seckillRegistrationRecord.getStoreId());
            StoreDTO storeInfoDTO = storeInfoClient.getStoreInfo(storeInfoVO).getData();
            if (Objects.nonNull(storeInfoDTO)) {
                addCardOrderReq.setStoreNo(storeInfoDTO.getStoreNo());
            }
        } catch (Exception e) {
            log.error("查询门店信息RPC接口异常,storeId=" + seckillRegistrationRecord.getStoreId(), e);
        }
        return addCardOrderReq;
    }

    private void dataConversion(SeckillRegistrationRecord o, SeckillRegistrationRecordResp response, Map<String, Integer> phoneNewMap) {
        //是否新用户
        if (null != phoneNewMap.get(o.getBuyerPhoneNumber())) {
            response.setIsNewCustomer(SeckillConstant.TYPE_1);
        }
        //浏览时间
        response.setBrowseTime(o.getCreateTime());
    }

    /**
     * 根据手机号和活动id 获取是否新用户
     *
     * @param phones
     * @param seckillActivityId
     * @return
     */
    private Map<String, Integer> phoneNewMap(List<String> phones, String seckillActivityId) {
        log.info("phoneNewMap{}", JSON.toJSONString(phones));
        EntityWrapper<ClientEventRecordDAO> wrapper = new EntityWrapper<>();
        wrapper.eq(ClientEventRecordDAO.CONTENT_VALUE, seckillActivityId);
        wrapper.eq(ClientEventRecordDAO.CONTENT_TYPE, SeckillConstant.REGISTERED);
        wrapper.in(SeckillRegistrationRecord.BUYER_PHONE_NUMBER, phones);
        List<ClientEventRecordDAO> list = clientEventRecordMapper.selectList(wrapper);
        if (CollectionUtils.isNotEmpty(list)) {
            Map<String, Integer> phoneNewMap = new HashMap<>();
            Map<String, List<ClientEventRecordDAO>> activityIdListMap = list.stream().collect(Collectors.groupingBy(ClientEventRecordDAO::getPhoneNumber));
            for (Map.Entry<String, List<ClientEventRecordDAO>> entry : activityIdListMap.entrySet()) {
                phoneNewMap.put(entry.getKey(), SeckillConstant.TYPE_1);
            }
            return phoneNewMap;
        }
        return Collections.EMPTY_MAP;
    }

    @Override
    public List<SeckillRegistrationRecordResp> participateDetail(String customersId) {
        if (null == customersId) {
            throw new StoreSaasMarketingException("客户ID不能为空");
        }
        EntityWrapper<SeckillRegistrationRecord> wrapper = new EntityWrapper<>();
        wrapper.eq(SeckillRegistrationRecord.CUSTOMER_ID, customersId);
        wrapper.eq(SeckillRegistrationRecord.PAY_STATUS, SeckillConstant.PAY_SUCCESS_STATUS);
        wrapper.eq(SeckillRegistrationRecord.STORE_ID, UserContextHolder.getStoreId());
        wrapper.eq(SeckillRegistrationRecord.TENANT_ID, UserContextHolder.getTenantId());
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
     * @param num      0 表示：已取消、已作废；    1 表示 ：已结清、回调(成功)   2 表示 ：已取消、回调(失败)
     * @return
     */
    private boolean updateReceivingAndTradeOrderByOrderNos(List<String> orderNos, Integer num) {
        log.info("updateReceivingAndTradeOrderByOrderNos orderNos:{};num:{}", orderNos, num);
        boolean success = false;
        try {
            BizBaseResponse baseResponse = storeReceivingClient.updateReceivingAndTradeOrderByOrderNos(orderNos, num);
            log.info("updateReceivingAndTradeOrderByOrderNos Result{}", JSON.toJSONString(baseResponse));
            if (baseResponse.isSuccess()) {
                success = true;
            }
        } catch (Exception e) {
            log.error("updateReceivingAndTradeOrderByOrderNos Error", e);
        }
        return success;
    }


    /**
     * 将未收款的待收单+交易单作废
     *
     * @param seckillRegistrationRecord
     * @param num
     */
    private void updateReceivingAndTradeOrder(SeckillRegistrationRecord seckillRegistrationRecord, Integer num) {
        List<SeckillRegistrationRecord> seckillRegistrationRecordList = this.selectList(this.buildSearchParams(seckillRegistrationRecord));
        if (CollectionUtils.isNotEmpty(seckillRegistrationRecordList)) {
            List<String> orderNos = seckillRegistrationRecordList.stream().map(p -> p.getOrderNo()).collect(Collectors.toList());
            if (CollectionUtils.isEmpty(orderNos)) {
                return;
            }
            this.updateReceivingAndTradeOrderByOrderNos(orderNos, num);
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
        if (Objects.nonNull(req.getBuyerPhoneNumber())) {
            search.eq(SeckillRegistrationRecord.BUYER_PHONE_NUMBER, req.getBuyerPhoneNumber());
        }
        if (Objects.nonNull(req.getIsDelete())) {
            search.eq(SeckillRegistrationRecord.IS_DELETE, req.getIsDelete());
        }
        return search;
    }


    /**
     * 根据秒杀订单创建 待收单  交易单
     *
     * @param seckillRegistrationRecord
     */
    private void addReceivingAndTradeOrderBySeckillActivity(SeckillRegistrationRecord seckillRegistrationRecord) {
        AddReceivingVO addReceivingVO = new AddReceivingVO();
        addReceivingVO.setOrderId(seckillRegistrationRecord.getId());
        addReceivingVO.setOrderNo(seckillRegistrationRecord.getOrderNo());
        addReceivingVO.setOrderDate(seckillRegistrationRecord.getCreateTime());
        addReceivingVO.setBusinessCategoryCode(BusinessCategoryEnum.SECKILL_ACTIVITY_ORDER.name());
        addReceivingVO.setBusinessCategoryName("秒杀活动单");
        addReceivingVO.setPayerId(seckillRegistrationRecord.getCustomerId());
        addReceivingVO.setPayerName(seckillRegistrationRecord.getCustomerName());
        addReceivingVO.setPayerPhoneNumber(seckillRegistrationRecord.getBuyerPhoneNumber());
        addReceivingVO.setAmount(seckillRegistrationRecord.getExpectAmount().multiply(new BigDecimal(100)).longValue());
        addReceivingVO.setDiscountAmount(0L);
        addReceivingVO.setActualAmount(addReceivingVO.getAmount());
        addReceivingVO.setPayedAmount(0L);
        addReceivingVO.setStatus("INIT");
        addReceivingVO.setPaymentStatus("UNRECEIVABLE");
        addReceivingVO.setStoreId(seckillRegistrationRecord.getStoreId());
        addReceivingVO.setTenantId(seckillRegistrationRecord.getTenantId());
        addReceivingVO.setCreateTime(new Date());
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

        try {
            //创建交易单
            log.info("tradeOrderClient.addTradeOrderBySeckillActivity request:{}", JSONObject.toJSONString(addReceivingVO));
            String result = tradeOrderClient.addTradeOrderBySeckillActivity(addReceivingVO).getData();
            log.info("tradeOrderClient.addTradeOrderBySeckillActivity response:{}", result);
            if (StringUtils.isBlank(result)) {
                throw new StoreSaasMarketingException("根据秒杀活动创建交易单失败");
            }
        } catch (BizException e) {
            log.error("tradeOrderClient.addTradeOrderBySeckillActivity error：调用参数{}，异常{}", JSONObject.toJSONString(addReceivingVO), e);
            throw new StoreSaasMarketingException("根据秒杀活动创建交易单异常");
        }
    }


    /**
     * 根据秒杀订单创建 客户
     *
     * @param seckillRegistrationRecord
     */
    private Map<String, String> addCustomerForOrder(SeckillRegistrationRecord seckillRegistrationRecord) {
        Map<String, String> maps = Maps.newHashMap();
        if (seckillRegistrationRecord.getBuyerPhoneNumber().equals(seckillRegistrationRecord.getUserPhoneNumber())) {
            CustomerReq customerReq = this.addCustomer(seckillRegistrationRecord.getBuyerPhoneNumber(), seckillRegistrationRecord.getCustomerName(), seckillRegistrationRecord.getStoreId(), seckillRegistrationRecord.getTenantId());
            maps.put(seckillRegistrationRecord.getBuyerPhoneNumber(), customerReq.getId());
        } else {
            CustomerReq customerReq = null;
            customerReq = this.addCustomer(seckillRegistrationRecord.getBuyerPhoneNumber(), seckillRegistrationRecord.getCustomerName(), seckillRegistrationRecord.getStoreId(), seckillRegistrationRecord.getTenantId());
            maps.put(seckillRegistrationRecord.getBuyerPhoneNumber(), customerReq.getId());
            customerReq = this.addCustomer(seckillRegistrationRecord.getUserPhoneNumber(), "空", seckillRegistrationRecord.getStoreId(), seckillRegistrationRecord.getTenantId());
            maps.put(seckillRegistrationRecord.getUserPhoneNumber(), customerReq.getId());
        }
        return maps;
    }


    private CustomerReq addCustomer(String phoneNumber, String name, Long storeId, Long tenantId) {
        //添加客户
        CustomerReq customerReq = new CustomerReq();
        customerReq.setPhoneNumber(phoneNumber);
        customerReq.setName(name);
        customerReq.setCustomerType("person");
        customerReq.setGender("");
        customerReq.setCustomerSource(CustomerSourceEnumVo.ZRJD.getCode());
        AddVehicleReq addVehicleReq = new AddVehicleReq();
        addVehicleReq.setStoreId(storeId);
        addVehicleReq.setTenantId(tenantId);
        addVehicleReq.setCustomerReq(customerReq);
        log.info("customerClient.addCustomerForOrder request:{}", JSONObject.toJSONString(addVehicleReq));
        BizBaseResponse<AddVehicleVO> resultObject = customerClient.addCustomerForOrder(addVehicleReq);
        log.info("customerClient.addCustomerForOrder response:{}", JSONObject.toJSONString(resultObject));
        if (Objects.nonNull(resultObject) && Objects.nonNull(resultObject.getData())) {
            customerReq.setId(resultObject.getData().getCustomerReq().getId());
        }
        return customerReq;
    }

}
