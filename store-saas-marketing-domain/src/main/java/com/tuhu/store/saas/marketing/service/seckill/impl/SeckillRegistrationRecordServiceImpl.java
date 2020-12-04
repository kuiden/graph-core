package com.tuhu.store.saas.marketing.service.seckill.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.alibaba.fastjson.JSON;
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
import com.tuhu.store.saas.marketing.remote.order.StoreReceivingClient;
import com.tuhu.store.saas.marketing.remote.order.TradeOrderClient;
import com.tuhu.store.saas.marketing.request.seckill.SeckillActivityReq;
import com.tuhu.store.saas.marketing.response.seckill.SeckillActivityStatisticsResp;
import com.tuhu.store.saas.marketing.response.seckill.SeckillRegistrationRecordResp;
import com.tuhu.store.saas.marketing.service.seckill.SeckillActivityService;
import com.tuhu.store.saas.marketing.service.seckill.SeckillRegistrationRecordService;
import com.tuhu.store.saas.order.vo.finance.receiving.AddReceivingVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Objects;

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
    @Autowired
    private SeckillActivityService seckillActivityService;

    @Autowired
    private StoreReceivingClient storeReceivingClient;

    @Autowired
    private TradeOrderClient tradeOrderClient;

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
        //查询报名记录购买记录
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

    private void dataConversion(SeckillRegistrationRecord o, SeckillRegistrationRecordResp response, Map<String, Integer> customerIdNewMap) {
        //是否新用户
        if (null != customerIdNewMap.get(o.getBuyerPhoneNumber())) {
            response.setIsNewCustomer(SeckillConstant.TYPE_1);
        }
    }

    private Map<String, Integer> customerIdNewMap(List<String> customerIds,String seckillActivityId ) {
        log.info("customerIdNewMap{}", JSON.toJSONString(customerIds));
        EntityWrapper<SeckillRegistrationRecord> wrapper = new EntityWrapper<>();
        List<SeckillRegistrationRecord> list = this.selectList(wrapper);
        if (CollectionUtils.isNotEmpty(list)) {
            Map<String, Integer> customerIdNewMap = new HashMap<>();
            //todo 获取用户对应的注册数据，算新用户
            return customerIdNewMap;
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

/*    public static void main(String[] args) {
        BigDecimal DD = new BigDecimal(0).divide(new BigDecimal(1),4, BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal(100));
        String decimal = DD.setScale(1, BigDecimal.ROUND_HALF_UP).stripTrailingZeros().toPlainString();
        System.out.println(decimal);
    }*/


    /**
     * 判断登录手机号在活动下是否有未收款状态的待收单
     *
     * @param request
     */
    private void checkHasUnpaidReceiving(SeckillRegistrationRecord request) {
        List<SeckillRegistrationRecord> seckillRegistrationRecordList = this.selectList(this.buildSearchParams(request));
        if (CollectionUtils.isNotEmpty(seckillRegistrationRecordList)) {
            List<String> orderNos = seckillRegistrationRecordList.stream().map(p -> p.getOrderNo()).collect(Collectors.toList());
            if (CollectionUtils.isEmpty(orderNos)) {
                return;
            }
            try {
                BizBaseResponse<Boolean> resultObjectdata = storeReceivingClient.updateInitReceivingListByOrderNos(orderNos);
                log.info("storeReceivingClient.updateInitReceivingListByOrderNos  return:{}", JSON.toJSONString(resultObjectdata));
                if (null != resultObjectdata && resultObjectdata.isSuccess() && null != resultObjectdata.getData()) {
                    Boolean result = resultObjectdata.getData();
                    if (null == result || !result) {
                        log.warn("storeReceivingClient.updateInitReceivingListByOrderNos error:更新待收单失败");
                        throw new StoreSaasMarketingException("更新待收单失败");
                    }
                }
            } catch (Exception e) {
                log.error("storeReceivingClient.updateInitReceivingListByOrderNos error", e);
                throw e;
            }
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
        if (Objects.nonNull(req.getIsDelete())) {
            search.eq(SeckillRegistrationRecord.IS_DELETE, req.getIsDelete());
        }
        return search;
    }


    /**
     * 添加待收单
     *
     * @param seckillRegistrationRecord
     */
    private void addReceiving(SeckillRegistrationRecord seckillRegistrationRecord) {
        //新增待收记录
        AddReceivingVO addReceivingVO = new AddReceivingVO();
        addReceivingVO.setOrderId(seckillRegistrationRecord.getId());
        addReceivingVO.setOrderNo(seckillRegistrationRecord.getOrderNo());
        addReceivingVO.setOrderDate(seckillRegistrationRecord.getCreateTime());
        addReceivingVO.setBusinessCategoryCode("SECKILL_ACTIVITY_ORDER");
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

        Boolean result = storeReceivingClient.addReceiving(addReceivingVO).getData();
        if (null == result || !result) {
            throw new StoreSaasMarketingException("创建待收记录失败");
        }
    }


    /**
     * 根据秒杀活动创建交易单
     *
     * @param addReceivingVO
     */
    private void addTradeOrderBySeckillActivity(AddReceivingVO addReceivingVO) {
        //根据秒杀活动创建交易单
        String result = tradeOrderClient.addTradeOrderBySeckillActivity(addReceivingVO).getData();
        if (StringUtils.isBlank(result)) {
            throw new StoreSaasMarketingException("根据秒杀活动创建交易单失败");
        }
    }


}
