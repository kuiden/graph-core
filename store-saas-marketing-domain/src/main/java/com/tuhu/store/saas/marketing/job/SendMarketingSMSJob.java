package com.tuhu.store.saas.marketing.job;

import com.google.common.collect.Lists;
import com.tuhu.store.saas.marketing.bo.SMSResult;
import com.tuhu.store.saas.marketing.dataobject.*;
import com.tuhu.store.saas.marketing.enums.MessageStatusEnum;
import com.tuhu.store.saas.marketing.enums.SMSTypeEnum;
import com.tuhu.store.saas.marketing.mysql.marketing.write.dao.CustomerMarketingMapper;
import com.tuhu.store.saas.marketing.parameter.SMSParameter;
import com.tuhu.store.saas.marketing.request.SendCouponReq;
import com.tuhu.store.saas.marketing.request.SendRemindReq;
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
import java.util.List;
import java.util.stream.Collectors;

/**
 * 定时扫描营销任务，添加发送短信
 */
@Slf4j
@Component
@JobHandler("sendMarketingSMSJob")
public class SendMarketingSMSJob extends IJobHandler {

    /**
     * 允许发送5分钟之后合法的任务
     */
    @Value("${marketing.sendsmstask.minutesLater:5}")
    private int minutesLater;

    @Autowired
    private IMessageRemindService iMessageRemindService;

    @Autowired
    ISMSService ismsService;

    @Override
    public ReturnT<String> execute(String param) throws Exception {
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
            iMessageRemindService.updateMessageRemindById(messageRemind);
        }

        return ReturnT.SUCCESS;
    }

}
