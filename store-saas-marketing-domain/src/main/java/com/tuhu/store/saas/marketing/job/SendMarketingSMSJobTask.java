package com.tuhu.store.saas.marketing.job;

import com.google.common.collect.Lists;
import com.tuhu.store.saas.marketing.dataobject.CustomerMarketing;
import com.tuhu.store.saas.marketing.dataobject.CustomerMarketingExample;
import com.tuhu.store.saas.marketing.dataobject.MarketingSendRecord;
import com.tuhu.store.saas.marketing.dataobject.MessageTemplateLocal;
import com.tuhu.store.saas.marketing.enums.SMSTypeEnum;
import com.tuhu.store.saas.marketing.mysql.marketing.write.dao.CustomerMarketingMapper;
import com.tuhu.store.saas.marketing.request.SendRemindReq;
import com.tuhu.store.saas.marketing.service.IMarketingSendRecordService;
import com.tuhu.store.saas.marketing.service.IMessageTemplateLocalService;
import com.tuhu.store.saas.marketing.service.IRemindService;
import com.tuhu.store.saas.marketing.util.DateUtils;
import com.tuhu.store.saas.marketing.util.GsonTool;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.JobHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

/**
 * 定时扫描营销任务，添加发送短信
 */
@Slf4j
@Component
@JobHandler("summarySassOrderJobTask")
public class SendMarketingSMSJobTask extends IJobHandler {

    /**
     * 允许发送5分钟之后合法的任务
     */
    @Value("${marketing.sendsmstask.minutesLater:5}")
    private int minutesLater;

    @Autowired
    private IRemindService remindService;

    @Autowired
    private CustomerMarketingMapper customerMarketingMapper;

    @Autowired
    private IMarketingSendRecordService sendRecordService;

    @Autowired
    private IMessageTemplateLocalService templateLocalService;

    @Override
    public ReturnT<String> execute(String param) throws Exception {
        Date sendTime = DateUtils.getNextMinutes(DateUtils.now(),minutesLater);
        CustomerMarketingExample customerMarketingExample = new CustomerMarketingExample();
        CustomerMarketingExample.Criteria listCriterion = customerMarketingExample.createCriteria();
        //门店ID过滤
        listCriterion.andSendTimeLessThanOrEqualTo(sendTime)
                .andTaskTypeEqualTo(Byte.valueOf("0"));
        List<Long> storeIds = customerMarketingMapper.listMatchedStoreIds(customerMarketingExample);
        for(Long storeId : storeIds){
            //处理每个门店的短信发送事物
            //指定门店id
            listCriterion.andStoreIdEqualTo(storeId);
            List<CustomerMarketing> customerMarketings = customerMarketingMapper.selectByExample(customerMarketingExample);
            for(CustomerMarketing customerMarketing : customerMarketings){
                if(customerMarketing.getMarketingMethod().equals(Byte.valueOf("0"))){
                    //发送优惠卷
                    //TODO .根据优惠卷模板生成短链，发送短信，给用户发送卷入库，占用减配额（发送失败取消占用），更新发送记录和任务状态
                    //TODO .发送短信发送到IRemindService中,IRemindService统一处理减少短信数量

                }else if(customerMarketing.getMarketingMethod().equals(Byte.valueOf("1"))){
                    //活动营销，只是根据模板发送短信，更新发送记录和任务状态，发送短信发送到IRemindService中,IRemindService统一处理减少短信数量
                    doSendSMS4Activity(customerMarketing);
                }
            }
        }
        return ReturnT.SUCCESS;
    }

    /**
     * 活动营销发送短信逻辑
     * @param customerMarketing
     */
    private void doSendSMS4Activity(CustomerMarketing customerMarketing){
        MessageTemplateLocal messageTemplateLocal = templateLocalService.getTemplateLocalById(customerMarketing.getMessageTemplateId());
        if(messageTemplateLocal==null){
            //发送失败，模板不存在
            log.warn("发送失败，短信模板id{}不存在",customerMarketing.getMessageTemplateId());
            customerMarketing.setTaskType(Byte.valueOf("2"));
            customerMarketingMapper.updateByPrimaryKey(customerMarketing);
            return ;
        }
        customerMarketing.setTaskType(Byte.valueOf("1"));
        String datas = customerMarketing.getMessageDatas();
        List<String> sendDatas = GsonTool.fromJsonList(datas,String.class);
        List<Byte> sendTypes = Lists.newArrayList();
        sendTypes.add(Byte.valueOf("0"));
        List<MarketingSendRecord> marketingSendRecords = sendRecordService.listMarketingSendRecord(customerMarketing.getId()+"",sendTypes);
        for(MarketingSendRecord marketingSendRecord : marketingSendRecords){
            //发送记录一条一条发送
            String sendState = "1";
            SendRemindReq sendRemindReq = new SendRemindReq();
            sendRemindReq.setMessageTemplateId(customerMarketing.getMessageTemplateId());
            sendRemindReq.setDatas(customerMarketing.getMessageDatas());
            sendRemindReq.setStoreId(customerMarketing.getStoreId());
            sendRemindReq.setTenantId(customerMarketing.getTenantId());
            sendRemindReq.setSource(SMSTypeEnum.MARKETING_ACTIVITY.templateCode());
            sendRemindReq.setSourceId(String.valueOf(customerMarketing.getId()));
            try{
                boolean result = remindService.sendWithPhone(sendRemindReq,marketingSendRecord.getPhoneNumber());
                if(!result){
                    log.warn("定向营销活动发送失败result{}",result);
                    sendState = "2";
                }
            }catch (Exception e){
                log.warn("发送短信记录{}异常:{}",marketingSendRecord.getId(),e.getMessage());
                sendState = "2";
            }
            sendRecordService.updateMarketingSendRecord(marketingSendRecord.getCustomerId(),customerMarketing.getId()+"",sendState);
        }
        //已发送，发送失败更新记录
        customerMarketingMapper.updateByPrimaryKey(customerMarketing);
    }

}
