package com.tuhu.store.saas.marketing.service.impl;

import com.tuhu.boot.common.facade.BizBaseResponse;
import com.tuhu.store.saas.marketing.remote.reponse.StoreInfoDTO;
import com.tuhu.store.saas.marketing.remote.request.StoreInfoVO;
import com.tuhu.store.saas.marketing.remote.storeuser.StoreUserClient;
import com.tuhu.store.saas.marketing.request.ReservePeriodReq;
import com.tuhu.store.saas.marketing.response.ReservationPeriodResp;
import com.tuhu.store.saas.marketing.service.INewReservationService;
import com.tuhu.store.saas.marketing.service.IReservationOrderService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
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

    @Autowired
    StoreUserClient storeUserClient;

    @Autowired
    IReservationOrderService reservationOrderService;

    @Value("${store.open.time.begin}")
    private String openBeginTime = "10:00:00";

    @Value("${store.open.time.end}")
    private String openEndTime = "18:00:00";

    @Override
    public List<ReservationPeriodResp> getReservationPeroidList(ReservePeriodReq req) {
        List<ReservationPeriodResp> result = new ArrayList<>();
        //查门店营业时间
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        Date beginTime = new Date();
        Date endTime = new Date();
        try {
            beginTime = sdf.parse(openBeginTime);
            endTime = sdf.parse(openEndTime);
            StoreInfoVO vo = new StoreInfoVO();
            vo.setStoreId(req.getStoreId());
            BizBaseResponse<StoreInfoDTO> resultObject = storeUserClient.getStoreInfo(vo);
            StoreInfoDTO storeInfoDTO = resultObject!=null ? (StoreInfoDTO) resultObject.getData() : null;
            if(storeInfoDTO != null){
                if(storeInfoDTO.getOpeningEffectiveDate() != null){
                    beginTime = storeInfoDTO.getOpeningEffectiveDate();
                }
                if(storeInfoDTO.getOpeningExpiryDate() != null){
                    endTime = storeInfoDTO.getOpeningExpiryDate();
                }
            }
        } catch (Exception ex) {
            log.error("INewReservationServiceImpl.getReservationPeroidList->获取门店信息出错" + ex.getMessage());
        }
        //算出时间段
        List<String> allTimePoints = getTimePoints(beginTime,endTime,30);
        for(String s : allTimePoints){
            ReservationPeriodResp resp = new ReservationPeriodResp();
            resp.setPeriodName(getPeriodName(s));
            resp.setReserveStartTimeString(s);

            result.add(resp);
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

    /**
     * 获取当前时间往后推半个小时的时间段
     * @param time
     * @return
     */
    private String getPeriodName(String time){
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        try {
            Calendar cal = Calendar.getInstance();
            cal.setTime(sdf.parse(time));
            cal.add(Calendar.MINUTE, 30);
            Date endTime = cal.getTime();
            return time + "-" + sdf.format(endTime);
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
        SimpleDateFormat sdf1 = new SimpleDateFormat("HH:mm:ss");
        SimpleDateFormat sdf2 = new SimpleDateFormat("HH:mm");
        List<String> newList = new ArrayList<>();
        try{
            for(String s : list){
                long now = sdf1.parse(s).getTime();
                if(now >= startTime.getTime() && now <= endTime.getTime()){
                    newList.add(sdf2.format(sdf2.parse(s)));
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        return newList;
    }
}
