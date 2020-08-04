package com.tuhu.store.saas.marketing.job;

import com.google.common.collect.Lists;
import com.tuhu.store.saas.marketing.bo.SMSResult;
import com.tuhu.store.saas.marketing.dataobject.CustomerMarketing;
import com.tuhu.store.saas.marketing.dataobject.CustomerMarketingExample;
import com.tuhu.store.saas.marketing.dataobject.MarketingSendRecord;
import com.tuhu.store.saas.marketing.mysql.marketing.write.dao.CustomerMarketingMapper;
import com.tuhu.store.saas.marketing.parameter.SMSParameter;
import com.tuhu.store.saas.marketing.service.IMarketingSendRecordService;
import com.tuhu.store.saas.marketing.service.ISMSService;
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
    private ISMSService ismsService;

    @Autowired
    private CustomerMarketingMapper customerMarketingMapper;

    @Autowired
    private IMarketingSendRecordService sendRecordService;

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

                }else if(customerMarketing.getMarketingMethod().equals(Byte.valueOf("1"))){
                    //TODO 活动营销，只是根据模板发送短信，更新发送记录和任务状态
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
        customerMarketing.setTaskType(Byte.valueOf("1"));
        String datas = customerMarketing.getMessageDatas();
        List<String> sendDatas = GsonTool.fromJsonList(datas,String.class);
        List<Byte> sendTypes = Lists.newArrayList();
        sendTypes.add(Byte.valueOf("0"));
        List<MarketingSendRecord> marketingSendRecords = sendRecordService.listMarketingSendRecord(customerMarketing.getId()+"",sendTypes);
        for(MarketingSendRecord marketingSendRecord : marketingSendRecords){
            //发送记录一条一条发送
            String sendState = "1";
            try{
                SMSParameter sendParam = new SMSParameter();
                sendParam.setPhone(marketingSendRecord.getPhoneNumber());
                sendParam.setTemplateId(customerMarketing.getMessageTemplateId());
                sendParam.setDatas(sendDatas);
                SMSResult smsResult = ismsService.sendCommonSms(sendParam);
                if(!smsResult.isSendResult()){
                    log.warn("发送短信记录{}失败:{}",marketingSendRecord.getId(),smsResult.getStatusCode()+":"+smsResult.getStatusMsg());
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
