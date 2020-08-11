package com.tuhu.store.saas.marketing.job;

import com.tuhu.store.saas.marketing.service.INewReservationService;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.JobHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @Author: yanglanqing
 * @Date: 2020/8/11 11:34
 * 待确认和已确认的预约单，当前时间大于预约的时间30分钟后的前一秒，变为已结束状态；
 * 已取消的不根据时间变化状态
 */
@Slf4j
@Component
@JobHandler("reservationOverJob")
public class ReservationOverJob extends IJobHandler {

    @Autowired
    INewReservationService iNewReservationService;

    @Value("${reservation.expireTime}")
    private Long expireTime;

    @Override
    public ReturnT<String> execute(String s) throws Exception {
        int updateCount = iNewReservationService.updateStatusToOver(expireTime);
        log.info("结束预约单的总条数：{}", updateCount);
        return ReturnT.SUCCESS;
    }
}
