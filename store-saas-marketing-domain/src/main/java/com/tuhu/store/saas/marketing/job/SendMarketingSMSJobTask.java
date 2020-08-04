package com.tuhu.store.saas.marketing.job;

import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.JobHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@JobHandler("summarySassOrderJobTask")
public class SendMarketingSMSJobTask extends IJobHandler {

    @Override
    public ReturnT<String> execute(String param) throws Exception {
        return null;
    }
}
