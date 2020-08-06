package com.tuhu.store.saas.marketing.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.tuhu.boot.common.facade.BizBaseResponse;
import com.tuhu.java.common.utils.DateUtil;
import com.tuhu.store.saas.marketing.context.UserContextHolder;
import com.tuhu.store.saas.marketing.enums.CustomTypeEnumVo;
import com.tuhu.store.saas.marketing.enums.SrvReservationStatusEnum;
import com.tuhu.store.saas.marketing.exception.StoreSaasMarketingException;
import com.tuhu.store.saas.marketing.po.SrvReservationOrder;
import com.tuhu.store.saas.marketing.remote.reponse.CustomerDTO;
import com.tuhu.store.saas.marketing.remote.reponse.StoreInfoDTO;
import com.tuhu.store.saas.marketing.remote.request.AddVehicleReq;
import com.tuhu.store.saas.marketing.remote.request.BaseIdReqVO;
import com.tuhu.store.saas.marketing.remote.request.CustomerReq;
import com.tuhu.store.saas.marketing.remote.request.StoreInfoVO;
import com.tuhu.store.saas.marketing.remote.storeuser.StoreUserClient;
import com.tuhu.store.saas.marketing.request.NewReservationReq;
import com.tuhu.store.saas.marketing.request.ReservePeriodReq;
import com.tuhu.store.saas.marketing.response.ReservationPeriodResp;
import com.tuhu.store.saas.marketing.service.INewReservationService;
import com.tuhu.store.saas.marketing.service.IReservationOrderService;
import com.tuhu.store.saas.marketing.util.DateUtils;
import com.tuhu.store.saas.marketing.util.IdKeyGen;
import com.tuhu.store.saas.marketing.util.KeyResult;
import com.tuhu.store.saas.marketing.util.StoreRedisUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @Author: yanglanqing
 * @Date: 2020/8/3 16:36
 */
@Service
@Slf4j
public class INewReservationServiceImpl implements INewReservationService {

    private static final String orderCodePrefix = "YYD";
    private static final String orderSeqPrefix = "STORE_SAAS_YYD_SEQ_";

    @Autowired
    StoreUserClient storeUserClient;

    @Autowired
    IReservationOrderService reservationOrderService;

    @Autowired
    private IdKeyGen idKeyGen;

    @Autowired
    private StoreRedisUtils storeRedisUtils;

    @Value("${store.open.time.begin}")
    private String openBeginTime = "10:00:00";

    @Value("${store.open.time.end}")
    private String openEndTime = "18:00:00";

    private SimpleDateFormat hmDateFormat = new SimpleDateFormat("HH:mm");
    SimpleDateFormat hmsDateFormat = new SimpleDateFormat("HH:mm:ss");
    SimpleDateFormat ymdDateFormat = new SimpleDateFormat("yyyy-MM-dd");
    SimpleDateFormat ymdhmDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");

    @Override
    public List<ReservationPeriodResp> getReservationPeroidList(ReservePeriodReq req) {
        List<ReservationPeriodResp> result = new ArrayList<>();
        //查门店营业时间
        Map<String,Date> storeMap = getStoreInfo(req.getStoreId());

        //算出时间段
        List<String> allTimePoints = getTimePoints(storeMap.get("startTime"),storeMap.get("endTime"),30);
        try{
            String ymd = ymdDateFormat.format(req.getDate());
            for(String s : allTimePoints){
                ReservationPeriodResp resp = new ReservationPeriodResp();
                resp.setReserveStartTimeString(s);
                resp.setPeriodName(s + "-" + getAfterTime(s));
                resp.setReserveStartTime(ymdhmDateFormat.parse(ymd+" "+s).getTime());
                resp.setReserveEndTime(ymdhmDateFormat.parse(ymd+" "+getAfterTime(s)).getTime());

                result.add(resp);
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        //过滤出客户已预约过的
        if(StringUtils.isNotBlank(req.getCustomerId())){
            HashSet set = reservationOrderService.getReservedPeriodListForCustomer(req.getDate(), req.getCustomerId(), req.getStoreId());
            result.forEach(reservationPeriodResp -> {
                if (set.contains(reservationPeriodResp.getReserveStartTimeString())) {
                    reservationPeriodResp.setReserved(true);
                }
            });
        }
        return result;
    }

    @Override
    public String addReservation(NewReservationReq req, Integer type) {
        //校验预约的时间
        if (req.getEstimatedArriveTime().compareTo(new Date()) < 0) {
            throw new StoreSaasMarketingException("到店时间需大于当前时间");
        }
        Map<String,Date> storeMap = getStoreInfo(req.getStoreId());
        if (DateUtils.getHourOfDay(req.getEstimatedArriveTime()) < DateUtils.getHourOfDay(storeMap.get("startTime"))
                || DateUtils.getHourOfDay(req.getEstimatedArriveTime()) > DateUtils.getHourOfDay(storeMap.get("endTime"))) {
            throw new StoreSaasMarketingException("当前预约时间段不能预约,门店预约时间范围为：" + hmDateFormat.format(storeMap.get("startTime")) + "-" + hmDateFormat.format(storeMap.get("endTime")));
        }
        //如果手机号不在门店客户中，添加客户(只有小程序和H5会出现这种情况)
        if(type != 0){
            AddVehicleReq addVehicleReq = new AddVehicleReq();
            addVehicleReq.setStoreId(req.getStoreId());
            addVehicleReq.setTenantId(req.getTenantId());
            CustomerReq customerReq = new CustomerReq();
            customerReq.setPhoneNumber(req.getCustomerPhoneNumber());
            customerReq.setGender("1");
            customerReq.setCustomerType(CustomTypeEnumVo.PERSON.getCode());
            customerReq.setCustomerSource("ZRJD");
            customerReq.setIsVip(false);
            addVehicleReq.setCustomerReq(customerReq);
            BizBaseResponse<AddVehicleReq> addObject = storeUserClient.addCustomerForReservation(addVehicleReq);
            if(addObject != null && addObject.getCode() != 10000){
                throw new StoreSaasMarketingException("新建客户出错："+addObject.getMessage());
            }
            if(addObject != null && addObject.getData() != null && addObject.getData().getCustomerReq() != null){
                req.setCustomerId(addObject.getData().getCustomerReq().getId());
            }
        }else {//门店的手机号会脱敏处理，所以这里根据客户id查
            BaseIdReqVO baseIdReqVO = new BaseIdReqVO();
            baseIdReqVO.setId(req.getCustomerId());
            baseIdReqVO.setTenantId(req.getTenantId());
            baseIdReqVO.setStoreId(req.getStoreId());
            log.info("查询客户信息请求参数为:{}", JSONObject.toJSONString(baseIdReqVO));
            BizBaseResponse<CustomerDTO> response = storeUserClient.getCustomerById(baseIdReqVO);
            log.info("查询客户信息返回参数为:{}", JSONObject.toJSONString(response));
            if (response != null && response.isSuccess() && response.getData() != null) {
                req.setCustomerPhoneNumber(response.getData().getPhoneNumber());
            }
        }
        //校验客户是否已预约过当前时间段
        HashSet set = reservationOrderService.getReservedPeriodListForCustomer(req.getEstimatedArriveTime(), req.getCustomerId(), req.getStoreId());
        if(CollectionUtils.isNotEmpty(set) && set.contains(hmDateFormat.format(req.getEstimatedArriveTime()))){
            throw new StoreSaasMarketingException("您已预约该时段，请勿重复预约");
        }

        //写表
        SrvReservationOrder order = new SrvReservationOrder();
        BeanUtils.copyProperties(req, order);
        String id = idKeyGen.generateId(req.getTenantId());
        order.setId(id);
        order.setReservationOrdeNo(getOrderCode(req.getStoreId(),UserContextHolder.getUser().getStoreNo()));
        order.setStatus(type == 0 ? SrvReservationStatusEnum.CONFIRMED.getEnumCode() : SrvReservationStatusEnum.UNCONFIRMED.getEnumCode());
        order.setCreateTime(new Date());
        order.setUpdateTime(new Date());
        order.setCreateUser(req.getUserId());
        order.setUpdateUser(req.getUserId());
        order.setDelete(false);
        reservationOrderService.insert(order);
        return id;
    }

    //获取门店营业时间
    private Map<String,Date> getStoreInfo(Long storeId){
        Map<String,Date> result = new HashMap<>();
        try {
            result.put("startTime",hmsDateFormat.parse(openBeginTime));
            result.put("endTime",hmsDateFormat.parse(openEndTime));
            StoreInfoVO vo = new StoreInfoVO();
            vo.setStoreId(storeId);
            BizBaseResponse<StoreInfoDTO> resultObject = storeUserClient.getStoreInfo(vo);
            if(resultObject != null && resultObject.getData() != null){
                if(resultObject.getData().getOpeningEffectiveDate() != null){
                    result.put("startTime",resultObject.getData().getOpeningEffectiveDate());
                }
                if(resultObject.getData().getOpeningExpiryDate() != null){
                    result.put("endTime",resultObject.getData().getOpeningExpiryDate());
                }
            }
        }catch (Exception ex) {
            log.error("INewReservationServiceImpl.getStoreInfo->获取门店信息出错" + ex.getMessage());
        }
        return result;
    }

    /**
     * 拼装预约单编号
     *
     * @return
     */
    private String getOrderCode(Long storeId, String storeNo) {
        if(StringUtils.isEmpty(storeNo)){
            storeNo = "";
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyMMddHHmm");
        String format = sdf.format(new Date());
        String key = orderSeqPrefix + storeId;
        KeyResult kr = storeRedisUtils.incrementAndGet(key, DateUtil.getTomorrow());
        String orderCode = orderCodePrefix + storeNo+ format + kr.getKey(3);
        log.info("生成的预约单号:{}", orderCode);
        return orderCode;
    }

    /**
     * 获取当前时间往后推半个小时的时间
     * @param time
     * @return
     */
    private String getAfterTime(String time){
        try {
            Calendar cal = Calendar.getInstance();
            cal.setTime(hmDateFormat.parse(time));
            cal.add(Calendar.MINUTE, 30);
            Date endTime = cal.getTime();
            return hmDateFormat.format(endTime);
        }catch (Exception e){
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 获取时间段内 间隔interval分钟的所有时间点
     */
    private List<String> getTimePoints(Date startTime, Date endTime, int interval) {
        ArrayList<String> list = new ArrayList<String>();//创建集合存储所有时间点
        for (int h = 0, m = 0; h < 24; m += interval) {//创建循环，指定间隔interval分钟
            if (m >= 60) {//判断分钟累计到60时清零，小时+1
                h++;
                m = 0;
            }
            if (h >= 24) {//判断小时累计到24时跳出循环，不添加到集合
                break;
            }

            /*转换为字符串*/
            String hour = String.valueOf(h);
            String minute = String.valueOf(m);

            /*判断如果为个位数则在前面拼接‘0’*/
            if (hour.length() < 2) {
                hour = "0" + hour;
            }
            if (minute.length() < 2) {
                minute = "0" + minute;
            }
            list.add(hour + ":" + minute + ":00");//拼接为HH:mm格式，添加到集合
        }
        List<String> newList = new ArrayList<>();
        try{
            for(String s : list){
                long now = hmsDateFormat.parse(s).getTime();
                if(now >= startTime.getTime() && now <= endTime.getTime()){
                    newList.add(hmDateFormat.format(hmDateFormat.parse(s)));
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        return newList;
    }
}
