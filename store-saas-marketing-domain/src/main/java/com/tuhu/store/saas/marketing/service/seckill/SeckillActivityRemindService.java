package com.tuhu.store.saas.marketing.service.seckill;

import com.baomidou.mybatisplus.service.IService;
import com.tuhu.store.saas.marketing.dataobject.SeckillActivityRemind;
import com.tuhu.store.saas.marketing.request.seckill.SeckillRemindAddReq;

/**
 * <p>
 * 秒杀活动开抢提醒 服务类
 * </p>
 *
 * @author wangyuqing
 * @since 2020-12-08
 */
public interface SeckillActivityRemindService extends IService<SeckillActivityRemind> {

    void customerActivityRemindAdd(SeckillRemindAddReq req);

}
