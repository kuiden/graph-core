package com.tuhu.store.saas.marketing.job;

import com.tuhu.store.saas.marketing.bo.SMSResult;
import com.tuhu.store.saas.marketing.dataobject.MessageRemind;
import com.tuhu.store.saas.marketing.enums.MessageStatusEnum;
import com.tuhu.store.saas.marketing.parameter.SMSParameter;
import com.tuhu.store.saas.marketing.service.IMessageRemindService;
import com.tuhu.store.saas.marketing.service.ISMSService;
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
 * 定时扫描短信待发列表，发送短信
 */
@Slf4j
@Component
@JobHandler("sendMarketingSMSJob")
public class SendMarketingSMSJob extends IJobHandler {

    @Autowired
    private IMessageRemindService iMessageRemindService;

    @Autowired
    ISMSService ismsService;

    @Override
    public ReturnT<String> execute(String param) throws Exception {
        log.info("{} -> 时间: {}", "sendMarketingSMSJob定时任务", new Date());
        List<MessageRemind> messageReminds = iMessageRemindService.getAllNeedSendMarketingReminds();

        for(MessageRemind messageRemind : messageReminds) {

            if(messageRemind.getStatus().equals(MessageStatusEnum.MESSAGE_FAIL.getCode()) && messageRemind.getTryTime()<1) {
                continue;
            }

            SMSParameter smsParameter = new SMSParameter();
            smsParameter.setPhone(messageRemind.getPhoneNumber());
            smsParameter.setTemplateId(messageRemind.getTemplateId());
            List<String> sendDatas = GsonTool.fromJsonList(messageRemind.getDatas(),String.class);
            smsParameter.setDatas(sendDatas);
            SMSResult sendResult = ismsService.sendCommonSms(smsParameter);

            if(sendResult.isSendResult()) {//成功。刷新状态为成功
                messageRemind.setStatus(MessageStatusEnum.MESSAGE_SUCCESS.getCode());
            }else {//失败

                if(MessageStatusEnum.MESSAGE_WAIT.getCode().equals(messageRemind.getStatus())) {//如果是首次发送失败，直接刷新状态
                    messageRemind.setStatus(MessageStatusEnum.MESSAGE_FAIL.getCode());
                }else {//如果是多次次发送失败，还需要扣减重试次数
                    Integer tryTimes = messageRemind.getTryTime();
                    messageRemind.setTryTime(tryTimes-1);
                    messageRemind.setStatus(MessageStatusEnum.MESSAGE_FAIL.getCode());
                }
                String newMessage = sendResult.getStatusCode()+":"+sendResult.getStatusMsg()+";";
                String lastMessage = messageRemind.getStatusMessage()==null?"":messageRemind.getStatusMessage();
                messageRemind.setStatusMessage(lastMessage+newMessage);
            }
            messageRemind.setUpdateTime(new Date());
            messageRemind.setUpdateUser("job");
            iMessageRemindService.updateMessageRemindById(messageRemind);
        }

        return ReturnT.SUCCESS;
    }

}
