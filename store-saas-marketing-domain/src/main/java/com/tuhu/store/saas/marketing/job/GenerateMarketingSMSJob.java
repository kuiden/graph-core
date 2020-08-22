package com.tuhu.store.saas.marketing.job;

import com.google.common.collect.Lists;
import com.tuhu.store.saas.marketing.dataobject.*;
import com.tuhu.store.saas.marketing.enums.SMSTypeEnum;
import com.tuhu.store.saas.marketing.mysql.marketing.write.dao.CustomerMarketingMapper;
import com.tuhu.store.saas.marketing.request.SendCouponReq;
import com.tuhu.store.saas.marketing.request.SendRemindReq;
import com.tuhu.store.saas.marketing.response.ActivityResp;
import com.tuhu.store.saas.marketing.response.CommonResp;
import com.tuhu.store.saas.marketing.service.*;
import com.tuhu.store.saas.marketing.util.DateUtils;
import com.tuhu.store.saas.marketing.util.GsonTool;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.JobHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 定时扫描营销任务，添加代发短信记录
 */
@Slf4j
@Component
@JobHandler("generateMarketingSMSJob")
public class GenerateMarketingSMSJob extends IJobHandler {

    /**
     * 允许发送5分钟之后合法的任务
     */
    @Value("${marketing.sendsmstask.minutesLater:5}")
    private int minutesLater;

    /**
     * 活动短信链接长链
     */
    @Value("${marketing.customer.message.activity.url:http://store-dev.yunquecloud.com/store-h5-marketing/activity/}")
    private String activityUrl;

    /**
     * 优惠券短信链接长链
     */
    @Value("${marketing.customer.message.coupon.url:http://store-dev.yunquecloud.com/store-h5-marketing/coupon/}")
    private String couponUrl;


    @Autowired
    private IRemindService remindService;

    @Autowired
    private CustomerMarketingMapper customerMarketingMapper;

    @Autowired
    private IMarketingSendRecordService sendRecordService;

    @Autowired
    private IMessageTemplateLocalService templateLocalService;

    @Autowired
    private ICouponService iCouponService;

    @Autowired
    private IActivityService iActivityService;

    @Autowired
    private IUtilityService iUtilityService;

    @Override
    public ReturnT<String> execute(String param) throws Exception {
        log.info("{} -> 时间: {}", "generateMarketingSMSJob定时任务", new Date());
        Date sendTime = DateUtils.getNextMinutes(DateUtils.now(),minutesLater);
        CustomerMarketingExample customerMarketingExample = new CustomerMarketingExample();
        CustomerMarketingExample.Criteria listCriterion = customerMarketingExample.createCriteria();
        //门店ID过滤
        listCriterion.andSendTimeLessThanOrEqualTo(sendTime)
                .andTaskTypeEqualTo(Byte.valueOf("0"));

        List<CustomerMarketing> customerMarketings = customerMarketingMapper.selectByExample(customerMarketingExample);
//        customerMarketings = Lists.newArrayList(customerMarketings.get(customerMarketings.size()-1));
        for(CustomerMarketing customerMarketing : customerMarketings){
            MessageTemplateLocal messageTemplateLocal = templateLocalService.getTemplateLocalById(customerMarketing.getMessageTemplateId());
            if(messageTemplateLocal==null){
                //发送失败，模板不存在
                log.warn("创建{}定向营销任务短信列表失败，短信模板id{}不存在",customerMarketing.getId(),customerMarketing.getMessageTemplateId());
                customerMarketing.setTaskType(Byte.valueOf("2"));
                customerMarketingMapper.updateByPrimaryKey(customerMarketing);
                break;
            }
            if(customerMarketing.getMarketingMethod().equals(Byte.valueOf("0"))){
                //发送优惠卷
                //TODO .根据优惠卷模板生成短链，发送短信，给用户发送卷入库，占用减配额（发送失败取消占用），更新发送记录和任务状态
                //TODO .发送短信发送到IRemindService中,IRemindService统一处理减少短信数量
                doSendSMS4Coupon(customerMarketing);
            }else if(customerMarketing.getMarketingMethod().equals(Byte.valueOf("1"))){
                //活动营销，只是根据模板发送短信，更新发送记录和任务状态，发送短信发送到IRemindService中,IRemindService统一处理减少短信数量
                doSendSMS4Activity(customerMarketing);
            }
        }
        return ReturnT.SUCCESS;
    }

    /**
     * 活动营销发送短信逻辑
     * @param customerMarketing
     */
    private void doSendSMS4Activity(CustomerMarketing customerMarketing){

        ActivityResp activity = iActivityService.getActivityDetailById(Long.valueOf(customerMarketing.getCouponId()), customerMarketing.getStoreId());
        List<Byte> sendTypes = Lists.newArrayList();
        sendTypes.add(Byte.valueOf("0"));
        List<MarketingSendRecord> marketingSendRecords = sendRecordService.listMarketingSendRecord(customerMarketing.getId()+"",sendTypes);
        //生成统一的短链
        String url = iUtilityService.getShortUrl(activityUrl + activity.getEncryptedCode());

        for(MarketingSendRecord marketingSendRecord : marketingSendRecords){
            //发送记录一条一条发送
            String sendState = "1";
            SendRemindReq sendRemindReq = new SendRemindReq();
            sendRemindReq.setMessageTemplateId(customerMarketing.getMessageTemplateId());

            //替换短链
            List<String> sendDatas = GsonTool.fromJsonList(customerMarketing.getMessageDatas(),String.class);
            sendDatas.add(url);

            sendRemindReq.setCustomerId(marketingSendRecord.getCustomerId());
            sendRemindReq.setCustomerName(marketingSendRecord.getCustomerName());
            sendRemindReq.setDatas(GsonTool.toJSONString(sendDatas));
            sendRemindReq.setStoreId(customerMarketing.getStoreId());
            sendRemindReq.setTenantId(customerMarketing.getTenantId());
            sendRemindReq.setSource(SMSTypeEnum.MARKETING_ACTIVITY.templateCode());
            sendRemindReq.setSourceId(String.valueOf(customerMarketing.getId()));
            try{
                boolean result = remindService.sendWithPhone(sendRemindReq,marketingSendRecord.getPhoneNumber(),true);
                if(!result){
                    log.warn("定向营销活动{}待发送记录生成失败,result{}",sendRemindReq,result);
                    sendState = "2";
                }
            }catch (Exception e){
                log.warn("定向营销活动{}待发送记录生成异常:{}",marketingSendRecord.getId(),e.getMessage());
                sendState = "2";
            }
            sendRecordService.updateMarketingSendRecord(marketingSendRecord.getCustomerId(),customerMarketing.getId()+"",sendState);
        }
        //已发送，发送失败更新记录
        customerMarketing.setTaskType(Byte.valueOf("1"));
        customerMarketingMapper.updateByPrimaryKey(customerMarketing);
    }

    /**
     * 优惠券营销发送短信逻辑
     * @param customerMarketing
     */
    private void doSendSMS4Coupon(CustomerMarketing customerMarketing){

        //执行批量送券
        SendCouponReq sendCouponReq = new SendCouponReq();
        sendCouponReq.setUserId("system");
        sendCouponReq.setStoreId(customerMarketing.getStoreId());
        sendCouponReq.setTenantId(customerMarketing.getTenantId());
        if (sendCouponReq.getReceiveType() == null) {
            sendCouponReq.setReceiveType(Integer.valueOf(2));//手动发券
        }
        sendCouponReq.setCodes(Lists.newArrayList(customerMarketing.getCouponCode()));

        List<Byte> sendTypes = Lists.newArrayList(Byte.valueOf("0"));
        List<MarketingSendRecord> marketingSendRecords = sendRecordService.listMarketingSendRecord(customerMarketing.getId()+"",sendTypes);
        if(CollectionUtils.isEmpty(marketingSendRecords)) {
            log.info("定向营销{}未包含待发送的优惠券送券任务！", customerMarketing.getId());
            customerMarketing.setTaskType(Byte.valueOf("1"));
            customerMarketingMapper.updateByPrimaryKey(customerMarketing);
        }

        List<String> customerIds = marketingSendRecords.stream().map(x->x.getCustomerId()).collect(Collectors.toList());
        sendCouponReq.setCustomerIds(customerIds);
        Map<String , String> customerIdCodeMap = new HashMap<>();
        try{
            List<CommonResp<CustomerCoupon>> customerCouponRespList = iCouponService.sendCoupon(sendCouponReq);

            Long successNum = customerCouponRespList.stream().filter(x->x.isSuccess()).count();

            if(successNum < customerIds.size()) {
                log.error("定向营销{}创建优惠券{}失败！", customerMarketing.getId(),customerMarketing.getCouponCode());
                return;
            }

            customerIdCodeMap = customerCouponRespList.stream().collect(Collectors.toMap(x -> x.getData().getCustomerId(), x -> x.getData().getCode()));

        }catch (Exception e) {
            log.error("定向营销优惠券送券失败！", e);
            return;
        }


        for(MarketingSendRecord marketingSendRecord : marketingSendRecords){
            //发送记录一条一条发送
            String sendState = "1";
            SendRemindReq sendRemindReq = new SendRemindReq();
            sendRemindReq.setMessageTemplateId(customerMarketing.getMessageTemplateId());

            //替换短链
            String url = iUtilityService.getShortUrl(couponUrl + customerIdCodeMap.get(marketingSendRecord.getCustomerId()));
            List<String> sendDatas = GsonTool.fromJsonList(customerMarketing.getMessageDatas(),String.class);
            sendDatas.add(url);

            sendRemindReq.setCustomerId(marketingSendRecord.getCustomerId());
            sendRemindReq.setCustomerName(marketingSendRecord.getCustomerName());
            sendRemindReq.setDatas(GsonTool.toJSONString(sendDatas));
            sendRemindReq.setStoreId(customerMarketing.getStoreId());
            sendRemindReq.setTenantId(customerMarketing.getTenantId());
            sendRemindReq.setSource(SMSTypeEnum.MARKETING_COUPON.templateCode());
            sendRemindReq.setSourceId(String.valueOf(customerMarketing.getId()));
            sendRemindReq.setUserId("job");
            try{
                boolean result = remindService.sendWithPhone(sendRemindReq,marketingSendRecord.getPhoneNumber(),true);
                if(!result){
                    log.warn("定向营销活动{}待发送记录生成失败,result{}",sendRemindReq,result);
                    sendState = "2";
                }
            }catch (Exception e){
                log.warn("定向营销活动{}待发送记录生成异常:{}",marketingSendRecord.getId(),e.getMessage());
                sendState = "2";
            }
            sendRecordService.updateMarketingSendRecord(marketingSendRecord.getCustomerId(),customerMarketing.getId()+"",sendState);
        }
        //已发送，发送失败更新记录
        customerMarketing.setTaskType(Byte.valueOf("1"));
        customerMarketingMapper.updateByPrimaryKey(customerMarketing);

    }

}
