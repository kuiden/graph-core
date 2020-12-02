package com.tuhu.store.saas.marketing.job;

import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.JobHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;


/**
 * 秒杀活动定时任务自动下架
 */
@Slf4j
@Component
@JobHandler("seckillActivityJob")
public class SeckillActivityJob extends IJobHandler {
    @Override
    public ReturnT<String> execute(String s) throws Exception {
        return null;
    }
}
