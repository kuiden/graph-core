package com.tuhu.store.saas.marketing.job;

import com.tuhu.store.saas.marketing.service.seckill.SeckillActivityService;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.JobHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * <p>
 * 秒杀活动定时任务自动下架
 *
 * </p>
 *
 * @author yangshengyong
 * @since 2020-12-02
 */
@Slf4j
@Component
@JobHandler("seckillActivityJob")
public class SeckillActivityJob extends IJobHandler {

    @Autowired
    private SeckillActivityService seckillActivityService;

    @Override
    public ReturnT<String> execute(String s) throws Exception {
        log.info("seckillActivityJob start");
        int i = seckillActivityService.autoUpdateOffShelf();
        log.info("seckillActivityJob end count={}",i );
        return ReturnT.SUCCESS;
    }
}
