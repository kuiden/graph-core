package com.tuhu.store.saas.marketing.job;

import com.google.common.collect.Lists;
import com.tuhu.store.saas.marketing.dataobject.*;
import com.tuhu.store.saas.marketing.enums.SMSTypeEnum;
import com.tuhu.store.saas.marketing.mysql.marketing.write.dao.CustomerMarketingMapper;
import com.tuhu.store.saas.marketing.request.SendCouponReq;
import com.tuhu.store.saas.marketing.request.SendRemindReq;
import com.tuhu.store.saas.marketing.response.CommonResp;
import com.tuhu.store.saas.marketing.service.*;
import com.tuhu.store.saas.marketing.util.DateUtils;
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
    private IRemindService remindService;

    @Autowired
    private CustomerMarketingMapper customerMarketingMapper;

    @Autowired
    private IMessageRemindService iMessageRemindService;

    @Autowired
    private IMessageTemplateLocalService templateLocalService;

    @Autowired
    private ICouponService iCouponService;

    @Override
    public ReturnT<String> execute(String param) throws Exception {
        iMessageRemindService.getAllNeedSendReminds();
        return ReturnT.SUCCESS;
    }

}
