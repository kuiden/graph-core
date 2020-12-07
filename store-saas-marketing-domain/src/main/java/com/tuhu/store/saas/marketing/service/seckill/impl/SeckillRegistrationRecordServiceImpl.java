package com.tuhu.store.saas.marketing.service.seckill.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.tuhu.boot.common.facade.BizBaseResponse;
import com.tuhu.springcloud.common.bean.BeanUtil;
import com.tuhu.store.saas.marketing.constant.SeckillConstant;
import com.tuhu.store.saas.marketing.context.UserContextHolder;
import com.tuhu.store.saas.marketing.dataobject.SeckillActivity;
import com.tuhu.store.saas.marketing.dataobject.SeckillRegistrationRecord;
import com.tuhu.store.saas.marketing.exception.StoreSaasMarketingException;
import com.tuhu.store.saas.marketing.mysql.marketing.write.dao.SeckillRegistrationRecordMapper;
import com.tuhu.store.saas.marketing.remote.order.ServiceOrderClient;
import com.tuhu.store.saas.marketing.request.seckill.SeckillActivityReq;
import com.tuhu.store.saas.marketing.response.seckill.SeckillActivityStatisticsResp;
import com.tuhu.store.saas.marketing.response.seckill.SeckillRegistrationRecordResp;
import com.tuhu.store.saas.marketing.service.seckill.SeckillActivityService;
import com.tuhu.store.saas.marketing.service.seckill.SeckillRegistrationRecordService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
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

    private final static Integer PAY_STATUS = 3; //支付状态 0:未支付 1:成功 2:失败 3:作废

    @Autowired
    private SeckillActivityService seckillActivityService;

    @Autowired
    private ServiceOrderClient serviceOrderClient;

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
        wrapper.eq(SeckillRegistrationRecord.PAY_STATUS, SeckillConstant.PAY_STATUS);
        List<SeckillRegistrationRecord> list = this.selectList(wrapper);
        if (CollectionUtils.isNotEmpty(list)) {
            Map<String, Integer> activityIdNumMap = new HashMap<>();
            Map<String, List<SeckillRegistrationRecord>> activityIdListMap = list.stream().collect(Collectors.groupingBy(SeckillRegistrationRecord::getSeckillActivityId));
            for (Map.Entry<String, List<SeckillRegistrationRecord>> entry : activityIdListMap.entrySet()) {
                activityIdNumMap.put(entry.getKey(), entry.getValue().size());
            }
            return activityIdNumMap;
        }
        return Collections.EMPTY_MAP;
    }

    @Override
    public PageInfo<SeckillRegistrationRecordResp> pageBuyList(SeckillActivityReq req) {
        log.info("pageBuyList{}", JSON.toJSONString(req));
        seckillActivityService.check(req.getSeckillActivityId());
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
        seckillActivityService.check(req.getSeckillActivityId());
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

    private void dataConversion(SeckillRegistrationRecord o, SeckillRegistrationRecordResp response, Map<String, Integer> phoneNewMap) {
        //是否新用户
        if (null != phoneNewMap.get(o.getBuyerPhoneNumber())) {
            response.setIsNewCustomer(SeckillConstant.TYPE_1);
        }
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
        EntityWrapper<SeckillRegistrationRecord> wrapper = new EntityWrapper<>();
        wrapper.eq(SeckillRegistrationRecord.IS_NEW_CUSTOMER, SeckillConstant.TYPE_1);
        wrapper.eq(SeckillRegistrationRecord.SECKILL_ACTIVITY_ID, seckillActivityId);
        wrapper.in(SeckillRegistrationRecord.BUYER_PHONE_NUMBER, phones);
        List<SeckillRegistrationRecord> list = this.selectList(wrapper);
        if (CollectionUtils.isNotEmpty(list)) {
            Map<String, Integer> phoneNewMap = new HashMap<>();
            Map<String, List<SeckillRegistrationRecord>> activityIdListMap = list.stream().collect(Collectors.groupingBy(SeckillRegistrationRecord::getBuyerPhoneNumber));
            for (Map.Entry<String, List<SeckillRegistrationRecord>> entry : activityIdListMap.entrySet()) {
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
        wrapper.eq(SeckillRegistrationRecord.PAY_STATUS, SeckillConstant.PAY_STATUS);
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
        SeckillActivity activity = seckillActivityService.check(seckillActivityId);
        SeckillActivityStatisticsResp resp = new SeckillActivityStatisticsResp();
        resp.setActivityTitle(activity.getActivityTitle());
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
        Integer newUserCount = this.baseMapper.getAllUserCountByTypeAndSeckillActivityId(seckillActivityId, SeckillConstant.TYPE);
        if (null != newUserCount) {
            resp.setNewCustomers(newUserCount);
            Integer newBuyCount = this.baseMapper.getBuyCountByTypeAndSeckillActivityId(seckillActivityId, SeckillConstant.TYPE);
            //新客转化率
            newBuyCount = null == newBuyCount ? 0 : newBuyCount;
            BigDecimal rate = new BigDecimal(newBuyCount).divide(new BigDecimal(newUserCount), SeckillConstant.SCALE, BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal(100));
            resp.setNewCustomersConversionRate(rate.setScale(SeckillConstant.NEW_SCALE, BigDecimal.ROUND_HALF_UP).stripTrailingZeros().toPlainString());
        }
        //唤醒老客
        Integer oldUserCount = this.baseMapper.getAllUserCountByTypeAndSeckillActivityId(seckillActivityId, SeckillConstant.TYPE_1);
        if (null != oldUserCount) {
            resp.setOldCustomer(oldUserCount);
            Integer oldBuyCount = this.baseMapper.getBuyCountByTypeAndSeckillActivityId(seckillActivityId, SeckillConstant.TYPE_1);
            //老客转化率
            oldBuyCount = null == oldBuyCount ? 0 : oldBuyCount;
            BigDecimal rate = new BigDecimal(oldBuyCount).divide(new BigDecimal(oldUserCount), SeckillConstant.SCALE, BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal(100));
            resp.setOldCustomerConversionRate(rate.setScale(SeckillConstant.NEW_SCALE, BigDecimal.ROUND_HALF_UP).stripTrailingZeros().toPlainString());
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
                record.setPayStatus(PAY_STATUS);
                record.setUpdateTime(now);
                record.setUpdateUser("24AutoCancel");
            }
            //批量取消
            if (CollectionUtils.isNotEmpty(orderNos)) {
                boolean success = cancelReceivingAndTradeByOrderNos(orderNos);
                if (success) {
                    this.insertOrUpdateBatch(list);
                }
            }
        }
    }

    /**
     * 更新 待收单的收款状态已取消;交易单 已作废
     *
     * @param orderNos
     * @return
     */
    private boolean cancelReceivingAndTradeByOrderNos(List<String> orderNos) {
        log.info("cancelReceivingAndTradeByOrderNos{}", orderNos);
        boolean success = false;
        try {
            BizBaseResponse baseResponse = serviceOrderClient.updateReceivingAndTradeByOrderNos(orderNos);
            log.info("cancelReceivingAndTradeByOrderNosResult{}", JSON.toJSONString(baseResponse));
            if (baseResponse.isSuccess()) {
                success = true;
            }
        } catch (Exception e) {
            log.error("cancelReceivingAndTradeByOrderNosError", e);
        }
        return success;
    }

    public static void main(String[] args) {
        BigDecimal DD = new BigDecimal(0).divide(new BigDecimal(1),4, BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal(100));
        String decimal = DD.setScale(1, BigDecimal.ROUND_HALF_UP).stripTrailingZeros().toPlainString();
        System.out.println(decimal);
    }
}
