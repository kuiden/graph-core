package com.tuhu.store.saas.marketing.job;

import com.tuhu.store.saas.marketing.service.seckill.SeckillActivityRemindService;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.JobHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 秒杀活动开抢提醒
 *
 * @author wangyuqing
 * @since 2020/12/8 16:16
 */

@Slf4j
@Component
@JobHandler("SeckillActivityRemindJob")
public class SeckillActivityRemindJob extends IJobHandler {

    @Autowired
    private SeckillActivityRemindService seckillActivityRemindService;

    @Override
    public ReturnT<String> execute(String s) {
        log.info("SeckillActivityRemindJob start");
        seckillActivityRemindService.autoSendRemind();
        log.info("SeckillActivityRemindJob end");
        return ReturnT.SUCCESS;
    }
}
