package com.tuhu.store.saas.marketing.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.github.pagehelper.PageInfo;
import com.tuhu.boot.common.facade.BizBaseResponse;
import com.tuhu.java.common.utils.DateUtil;
import com.tuhu.store.saas.crm.vo.CustomerSourceEnumVo;
import com.tuhu.store.saas.marketing.bo.SMSResult;
import com.tuhu.store.saas.marketing.context.EndUserContextHolder;
import com.tuhu.store.saas.marketing.context.UserContextHolder;
import com.tuhu.store.saas.marketing.enums.CustomTypeEnumVo;
import com.tuhu.store.saas.marketing.enums.SMSTypeEnum;
import com.tuhu.store.saas.marketing.enums.SrvReservationStatusEnum;
import com.tuhu.store.saas.marketing.exception.StoreSaasMarketingException;
import com.tuhu.store.saas.marketing.mysql.marketing.write.dao.SrvReservationOrderMapper;
import com.tuhu.store.saas.marketing.parameter.SMSParameter;
import com.tuhu.store.saas.marketing.po.ReservationDateDTO;
import com.tuhu.store.saas.marketing.po.SrvReservationOrder;
import com.tuhu.store.saas.marketing.remote.reponse.CustomerDTO;
import com.tuhu.store.saas.marketing.remote.reponse.StoreInfoDTO;
import com.tuhu.store.saas.marketing.remote.request.AddVehicleReq;
import com.tuhu.store.saas.marketing.remote.request.BaseIdReqVO;
import com.tuhu.store.saas.marketing.remote.request.CustomerReq;
import com.tuhu.store.saas.marketing.remote.request.StoreInfoVO;
import com.tuhu.store.saas.marketing.remote.storeuser.StoreUserClient;
import com.tuhu.store.saas.marketing.request.*;
import com.tuhu.store.saas.marketing.response.BReservationListResp;
import com.tuhu.store.saas.marketing.response.ReservationDateResp;
import com.tuhu.store.saas.marketing.response.ReservationPeriodResp;
import com.tuhu.store.saas.marketing.response.dto.ReservationDTO;
import com.tuhu.store.saas.marketing.service.IMessageTemplateLocalService;
import com.tuhu.store.saas.marketing.service.INewReservationService;
import com.tuhu.store.saas.marketing.service.IReservationOrderService;
import com.tuhu.store.saas.marketing.service.ISMSService;
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
import java.util.stream.Collectors;

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

    @Autowired
    SrvReservationOrderMapper reservationOrderMapper;

    @Autowired
    IMessageTemplateLocalService iMessageTemplateLocalService;

    @Autowired
    ISMSService ismsService;

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
        Map<String,Date> storeMap = getStoreWorkingTime(req.getStoreId());
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

    //teminalType 门店：0,小程序：1,H5:2
    @Override
    public String addReservation(NewReservationReq req, Integer type) {
        log.info("C端新增预约单addReservation入参：", JSONObject.toJSONString(req));
        //校验
        validReservationParam(req,type,1);
        //写表
        SrvReservationOrder order = new SrvReservationOrder();
        BeanUtils.copyProperties(req, order);
        String id = idKeyGen.generateId(req.getTenantId());
        order.setId(id);
        order.setReservationOrdeNo(getOrderCode(req.getStoreId(),UserContextHolder.getUser()==null?null:UserContextHolder.getUser().getStoreNo()));
        order.setStatus(type == 0 ? SrvReservationStatusEnum.CONFIRMED.getEnumCode() : SrvReservationStatusEnum.UNCONFIRMED.getEnumCode());
        order.setCreateTime(new Date());
        order.setUpdateTime(new Date());
        order.setCreateUser(req.getUserId());
        order.setUpdateUser(req.getUserId());
        order.setDelete(false);
        reservationOrderService.insert(order);

        //发送短信
        StoreInfoDTO storeInfo = getStoreInfo(req.getStoreId());
        if(storeInfo != null){
            if(type == 0 || type == 2){//发给客户：【门店名称】（【门店联系手机】），【预约月日时分】，【门店地址，只展示详细地址，不展示省市区】
                List<String> list = new ArrayList<>();
                list.add(storeInfo.getStoreName());
                list.add(storeInfo.getClientAppointPhone() == null?"":storeInfo.getClientAppointPhone());
                list.add(dealMdDate(order.getEstimatedArriveTime()));
                list.add(storeInfo.getAddress() == null?"":storeInfo.getAddress());
                sendSms(req.getCustomerPhoneNumber(),SMSTypeEnum.SAAS_STORE_ORDER_SUCCESS.templateCode(),list);
            }
            //门店预约电话不为空时才发送短信
            if(StringUtils.isNotBlank(storeInfo.getClientAppointPhone())){
                if(type == 1){//发给门店老板：客户【客户手机】通过“车主小程序”预约【预约月日时分】到店，汽配龙APP→我的→门店管理，查看详情
                    List<String> list = new ArrayList<>();
                    list.add(order.getCustomerPhoneNumber());
                    list.add(dealMdDate(order.getEstimatedArriveTime()));
                    sendSms(storeInfo.getClientAppointPhone(),SMSTypeEnum.SAAS_MINI_ORDER_CREATE.templateCode(),list);
                }
                if(type == 2){//发给门店老板:客户【门店联系手机】通过“【活动名称】”预约【预约月日时分】到店，汽配龙APP→我的→门店管理，查看详情
                    List<String> list = new ArrayList<>();
                    list.add(order.getCustomerPhoneNumber());
                    list.add(req.getMarketingName());
                    list.add(dealMdDate(order.getEstimatedArriveTime()));
                    sendSms(storeInfo.getClientAppointPhone(),SMSTypeEnum.SAAS_MINI_ORDER_SUCCESS.templateCode(),list);
                }
            }
        }

        return id;
    }

    @Override
    public Boolean updateReservation(NewReservationReq req) {
        log.info("车主小程序端修改预约单updateReservation入参：", JSONObject.toJSONString(req));
        //校验
        validReservationParam(req,1,2);
        SrvReservationOrder newOrder = new SrvReservationOrder();
        BeanUtils.copyProperties(req,newOrder);
        newOrder.setUpdateUser(req.getCustomerId());
        return reservationOrderService.update(newOrder) > 0;
    }

    @Override
    public PageInfo<ReservationDTO> getCReservationList(CReservationListReq req) {
        PageInfo<ReservationDTO> result = new PageInfo<>();
        List<ReservationDTO> list = new ArrayList<>();
        List<SrvReservationOrder> daoList = reservationOrderMapper.getCReservationList(req.getStoreId(),req.getCustomerId(),req.getPageNum(),req.getPageSize());
        if(CollectionUtils.isNotEmpty(daoList)){
            for(SrvReservationOrder dao : daoList){
                ReservationDTO dto = new ReservationDTO();
                dto.setId(dao.getId());
                dto.setReservationTime(dao.getEstimatedArriveTime().getTime());
                dto.setStatus(transCStatus(dao));
                list.add(dto);
            }
        }
        result.setList(list);
        result.setTotal(reservationOrderMapper.getCReservationCount(req.getStoreId(),req.getCustomerId()));
        return result;
    }

    @Override
    public List<ReservationDateResp> getReserveDateList(Long storeId) {
        List<ReservationDateResp> result = new ArrayList<>();
        Date today = DateUtils.getDateStartTime(new Date());
        for(int i = 0 ; i < 7 ; i++){
            ReservationDateResp resp = new ReservationDateResp();
            resp.setReservationDate(DateUtils.addDate(today, i).getTime());
            result.add(resp);
        }
        //查出往后7天的预约数
        List<ReservationDateDTO> daoList = reservationOrderMapper.getReserveDateList(storeId);
        if(CollectionUtils.isNotEmpty(daoList)){
            for(ReservationDateDTO dao : daoList){
                for(ReservationDateResp resp : result){
                    if(dao.getReservationDate().getTime() == resp.getReservationDate()){
                        resp.setCount(dao.getCount());
                    }
                }
            }
        }
        return result;
    }

    @Override
    public List<BReservationListResp> getBReservationList(BReservationListReq req) {
        List<BReservationListResp> result = new ArrayList<>();
        List<BReservationListResp> allTimeResult = new ArrayList<>();
        //查出规定日期内所有预约单
        List<SrvReservationOrder> daoList = reservationOrderMapper.getBReservationList(req.getStoreId(),ymdDateFormat.format(req.getReservationDate()));
        if(CollectionUtils.isNotEmpty(daoList)){
            try {
                //算出一天内所有时间段
                List<String> allTimePoints = getTimePoints(null,null,30);
                String ymd = ymdDateFormat.format(req.getReservationDate());
                //将数据库数据按照时间段归拢
                for(String s : allTimePoints){
                    BReservationListResp resp = new BReservationListResp();
                    List<ReservationDTO> reservationDTOs = new ArrayList<>();
                    for(SrvReservationOrder order : daoList){
                        if(s.equals(hmDateFormat.format(order.getEstimatedArriveTime()))){
                            ReservationDTO dto = new ReservationDTO();
                            BeanUtils.copyProperties(order,dto);
                            reservationDTOs.add(dto);
                        }
                    }
                    resp.setReservationStartTime(ymdhmDateFormat.parse(ymd+" "+s).getTime());
                    resp.setReservationEndTime(ymdhmDateFormat.parse(ymd+" "+getAfterTime(s)).getTime());
                    resp.setPeriodName(s + "-" + getAfterTime(s));
                    resp.setReservationDTOs(reservationDTOs);
                    allTimeResult.add(resp);
                }
                //将没有数据的时间段剔除
                result = allTimeResult.stream().filter(x->CollectionUtils.isNotEmpty(x.getReservationDTOs())).collect(Collectors.toList());
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        return result;
    }

    @Override
    public ReservationDTO getCReservationDetail(CReservationListReq req) {
        ReservationDTO result = new ReservationDTO();
        SrvReservationOrder order = getReservationById(req.getId(),req.getStoreId());
        if(order != null){
            BeanUtils.copyProperties(order,result);
            result.setReservationTime(order.getEstimatedArriveTime().getTime());
        }
        return result;
    }

    @Override
    public void confirmReservation(CReservationListReq req) {
        SrvReservationOrder order = getReservationById(req.getId(),req.getStoreId());
        if(!SrvReservationStatusEnum.UNCONFIRMED.getEnumCode().equals(order.getStatus())){
            throw new StoreSaasMarketingException("只有待确认的预约单才能确认");
        }
        EntityWrapper<SrvReservationOrder> wrapper = new EntityWrapper<>();
        wrapper.eq("id",req.getId());
        wrapper.eq("store_id",req.getStoreId());
        order.setStatus(SrvReservationStatusEnum.CONFIRMED.getEnumCode());
        order.setUpdateTime(new Date());
        order.setUpdateUser(UserContextHolder.getStoreUserId());
        reservationOrderMapper.update(order,wrapper);
    }

    @Override
    public void cancelReservation(CancelReservationReq req) {
        SrvReservationOrder order = getReservationById(req.getId(),req.getStoreId());
        switch (req.getTeminal()){
            case 1://门店拒绝
                if(!SrvReservationStatusEnum.UNCONFIRMED.getEnumCode().equals(order.getStatus())){
                    throw new StoreSaasMarketingException("只有待确认的预约单才能拒绝");
                }
                break;
            case 2://车主自己取消
                if(!SrvReservationStatusEnum.UNCONFIRMED.getEnumCode().equals(order.getStatus()) &&
                        !SrvReservationStatusEnum.CONFIRMED.getEnumCode().equals(order.getStatus())){
                    throw new StoreSaasMarketingException("只有已预约的预约单才能取消");
                }
                break;
            default:
                break;
        }
        EntityWrapper<SrvReservationOrder> wrapper = new EntityWrapper<>();
        wrapper.eq("id",req.getId());
        wrapper.eq("store_id",req.getStoreId());
        order.setStatus(SrvReservationStatusEnum.CANCEL.getEnumCode());
        order.setUpdateTime(new Date());
        order.setUpdateUser(req.getTeminal()==1?UserContextHolder.getStoreUserId(): EndUserContextHolder.getCustomerId());
        int updateResult = reservationOrderMapper.update(order,wrapper);
        if(updateResult > 0 && req.getTeminal() == 1){
            //门店拒绝预约给客户发短信:【门店名称】（【门店联系手机】）已取消您【预约月日时分】的到店预约，如有疑问请联系门店
            StoreInfoDTO storeInfo = getStoreInfo(req.getStoreId());
                List<String> list = new ArrayList<>();
                if(storeInfo != null){
                    list.add(storeInfo.getStoreName());
                    list.add(storeInfo.getClientAppointPhone() == null?"":storeInfo.getClientAppointPhone());
                    list.add(dealMdDate(order.getEstimatedArriveTime()));
                    sendSms(order.getCustomerPhoneNumber(),SMSTypeEnum.SAAS_STORE_CANCEL_ORDER.templateCode(),list);
                }
        }
    }

    @Override
    public int updateStatusToOver(Long expireTime) {
        return reservationOrderMapper.updateStatusToOver(expireTime);
    }

    /**
     * 根据id查预约单
     * @param id
     * @param storeId
     * @return
     */
    private SrvReservationOrder getReservationById(String id, Long storeId){
        SrvReservationOrder order;
        EntityWrapper<SrvReservationOrder> wrapper = new EntityWrapper<>();
        wrapper.eq("id",id);
        wrapper.eq("store_id",storeId);
        List<SrvReservationOrder> orderList = reservationOrderMapper.selectList(wrapper);
        if(CollectionUtils.isEmpty(orderList)){
            throw new StoreSaasMarketingException("预约单id:" +id+ "无效");
        }
        order = orderList.get(0);
        return order;
    }

    //01-01 10:30处理成1月1日10:30
    private String dealMdDate(Date date){
        String result = "";
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        Integer month = cal.get(Calendar.MONTH) + 1;
        Integer day = cal.get(Calendar.DAY_OF_MONTH);
        result += month+"月"+ day +"日"+ cal.get(Calendar.HOUR_OF_DAY) +":"+ (cal.get(Calendar.MINUTE)==0?"00":cal.get(Calendar.MINUTE));
        return result;
    }

    /**
     * 发送短信
     */
    private String sendSms(String phoneNum, String templateId, List<String> msgContent){
        String result = "" ;
        SMSParameter smsParameter = new SMSParameter();
        smsParameter.setPhone(phoneNum);
        smsParameter.setTemplateId(iMessageTemplateLocalService.getSMSTemplateIdByCodeAndStoreId(templateId,null));
        smsParameter.setDatas(msgContent);
        SMSResult sendResult = ismsService.sendCommonSms(smsParameter);
        if(sendResult != null && sendResult.isSendResult()){
            result = "发送成功";
        }
        return result;
    }

    /**
     * 将两个时间按照HH:mm格式比较大小
     * date1 < date2 --true
     * @return
     */
    private boolean compareHMDate(Date date1, Date date2){
        try{
            Calendar startTime = Calendar.getInstance();
            startTime.setTime(hmDateFormat.parse(hmDateFormat.format(date1)));
            Calendar endTime = Calendar.getInstance();
            endTime.setTime(hmDateFormat.parse(hmDateFormat.format(date2)));
            return startTime.before(endTime);
        }catch (Exception e){
            e.printStackTrace();
        }
        return true;
    }

    //C端预约列表的已预约对应B端的待确认和已确认
    private String transCStatus(SrvReservationOrder order){
        String result = order.getStatus();
        if(SrvReservationStatusEnum.UNCONFIRMED.getEnumCode().equals(order.getStatus()) || SrvReservationStatusEnum.CONFIRMED.getEnumCode().equals(order.getStatus())){
             result = "ORDERED";
        }
        return result;
    }

    /**
     * 新增和修改预约单共同的校验
     * @param req
     * @param teminalType 门店：0,小程序：1,H5:2
     * @param operateType 1:新增 ，2：修改
     */
    private void validReservationParam(NewReservationReq req, Integer teminalType, Integer operateType) {
        //校验预约的时间
        if (req.getEstimatedArriveTime().compareTo(new Date()) < 0) {
            throw new StoreSaasMarketingException("到店时间需大于当前时间");
        }
        Map<String,Date> storeMap = getStoreWorkingTime(req.getStoreId());
        Calendar arriveTime = Calendar.getInstance();
        Calendar startTime = Calendar.getInstance();
        Calendar endTime = Calendar.getInstance();
        try{
            arriveTime.setTime(hmDateFormat.parse(hmDateFormat.format(req.getEstimatedArriveTime())));
            startTime.setTime(storeMap.get("startTime"));
            endTime.setTime(storeMap.get("endTime"));
        }catch (Exception e){
            e.printStackTrace();
        }
        if (compareHMDate(req.getEstimatedArriveTime(),storeMap.get("startTime"))
                || compareHMDate(storeMap.get("endTime"),req.getEstimatedArriveTime())) {
            throw new StoreSaasMarketingException("当前预约时间段不能预约,门店预约时间范围为：" + hmDateFormat.format(storeMap.get("startTime")) + "-" + hmDateFormat.format(storeMap.get("endTime")));
        }
        //如果手机号不在门店客户中，添加客户(只有小程序和H5会出现这种情况)
        if(teminalType != 0){
            AddVehicleReq addVehicleReq = new AddVehicleReq();
            addVehicleReq.setStoreId(req.getStoreId());
            addVehicleReq.setTenantId(req.getTenantId());
            //H5短链进来拿不到操作人id,故H5创建的用户操作人为空，小程序的操作人也只拿得到客户id
            addVehicleReq.setUserId(req.getCustomerId());
            CustomerReq customerReq = new CustomerReq();
            customerReq.setPhoneNumber(req.getCustomerPhoneNumber());
            customerReq.setGender("3");
            customerReq.setCustomerType(CustomTypeEnumVo.PERSON.getCode());
            customerReq.setCustomerSource(CustomerSourceEnumVo.ZRJD.getCode());
            customerReq.setIsVip(false);
            customerReq.setName("空");
            addVehicleReq.setCustomerReq(customerReq);
            log.info("新增客户请求参数为:{}", JSONObject.toJSONString(addVehicleReq));
            BizBaseResponse<AddVehicleReq> addObject = storeUserClient.addCustomerForReservation(addVehicleReq);
            log.info("新增客户返回为:{}", JSONObject.toJSONString(addObject));
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
        String oldOrderReservationTime = "";
        if(operateType == 2){
            //查原有预约单是否存在
            SrvReservationOrder oldOrder = getReservationById(req.getId(),req.getStoreId());
            oldOrderReservationTime = hmDateFormat.format(oldOrder.getEstimatedArriveTime());
        }

        //校验客户是否已预约过当前时间段
        HashSet set = reservationOrderService.getReservedPeriodListForCustomer(req.getEstimatedArriveTime(), req.getCustomerId(), req.getStoreId());
        //修改预约单时，若传入的预约时间和之前一致，不算重复预约
        if(StringUtils.isNotBlank(oldOrderReservationTime)){
            Iterator it = set.iterator();
            while(it.hasNext()){
                if(it.next().equals(oldOrderReservationTime)){
                    it.remove();
                }
            }
        }
        if(CollectionUtils.isNotEmpty(set) && set.contains(hmDateFormat.format(req.getEstimatedArriveTime()))){
            throw new StoreSaasMarketingException("您已预约该时段，请勿重复预约");
        }
    }

    //获取门店营业时间
    private Map<String,Date> getStoreWorkingTime(Long storeId){
        Map<String,Date> result = new HashMap<>();
        try {
            result.put("startTime",hmsDateFormat.parse(openBeginTime));
            result.put("endTime",hmsDateFormat.parse(openEndTime));
            StoreInfoDTO dto = getStoreInfo(storeId);
            if(dto != null){
                if(dto.getOpeningEffectiveDate() != null){
                    result.put("startTime",dto.getOpeningEffectiveDate());
                }
                if(dto.getOpeningExpiryDate() != null){
                    result.put("endTime",dto.getOpeningExpiryDate());
                }
            }
        }catch (Exception ex) {
            log.error("INewReservationServiceImpl.getStoreWorkingTime->获取门店信息出错" + ex.getMessage());
        }
        return result;
    }

    //获取门店信息
    private StoreInfoDTO getStoreInfo(Long storeId){
        StoreInfoDTO result = new StoreInfoDTO();
        try {
            StoreInfoVO vo = new StoreInfoVO();
            vo.setStoreId(storeId);
            BizBaseResponse<StoreInfoDTO> resultObject = storeUserClient.getStoreInfo(vo);
            log.info("==storeAdminClient.getUserByToken=={}", JSONObject.toJSONString(resultObject));
            if(resultObject != null && resultObject.getData() != null){
                result = resultObject.getData();
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
            list.add(hour + ":" + minute);//拼接为HH:mm格式，添加到集合
        }
        if(startTime == null && endTime == null){
            return list;
        }
        List<String> newList = new ArrayList<>();
        try{
            Calendar rightNow = Calendar.getInstance();
            rightNow.setTime(endTime);
            rightNow.add(Calendar.HOUR, -1);
            for(String s : list){
                long now = hmDateFormat.parse(s).getTime();
                if(now >= startTime.getTime() && now <= rightNow.getTime().getTime()){
                    newList.add(hmDateFormat.format(hmDateFormat.parse(s)));
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        return newList;
    }
}
