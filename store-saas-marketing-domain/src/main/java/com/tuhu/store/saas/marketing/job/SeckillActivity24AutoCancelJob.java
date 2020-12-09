package com.tuhu.store.saas.marketing.job;

import com.tuhu.store.saas.marketing.service.seckill.SeckillRegistrationRecordService;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.JobHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 秒杀活动下单24小时未支付自动取消相关订单
 *
 * @author yangshengyong
 * @date 2020-12-04
 */
@Slf4j
@Component
@JobHandler("seckillActivity24AutoCancelJob")
public class SeckillActivity24AutoCancelJob extends IJobHandler {
    @Autowired
    private SeckillRegistrationRecordService seckillRegistrationRecordService;

    @Override
    public ReturnT<String> execute(String s) throws Exception {
        log.info("seckillActivity24AutoCancel start");
        seckillRegistrationRecordService.seckillActivity24AutoCancel();
        log.info("seckillActivity24AutoCancel end");
        return ReturnT.SUCCESS;
    }
}
